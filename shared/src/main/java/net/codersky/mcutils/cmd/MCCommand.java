package net.codersky.mcutils.cmd;

import net.codersky.mcutils.MCUtils;
import net.codersky.mcutils.java.math.MCNumbers;
import net.codersky.mcutils.java.strings.MCStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Interface used to represent a cross-platform command.
 * Commands can directly be made and implemented with this interface,
 * though the {@link GlobalCommand} class is easier to use if you
 * intend to make cross-platform commands.
 *
 * @since MCUtils 1.0.0
 *
 * @author xDec0de_
 *
 * @param <P> The plugin class that owns this {@link MCCommand}.
 * @param <S> The {@link MCCommandSender} type of this {@link MCCommand}.
 */
public interface MCCommand<P, S extends MCCommandSender> {

	/**
	 * Gets the name of this  {@link MCCommand}, which is used in order
	 * to execute it as "/name".
	 *
	 * @return The name of this  {@link MCCommand}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	String getName();

	@NotNull
	List<String> getAliases();

	@NotNull
	MCUtils<P> getUtils();

	@NotNull
	default P getPlugin() {
		return getUtils().getPlugin();
	}

	boolean onCommand(@NotNull S sender, @NotNull String[] args);

	@NotNull
	List<String> onTab(@NotNull S sender, @NotNull String[] args);

	@NotNull
	MCCommand<P, S> inject(@NotNull MCCommand<P, S>... commands);

	default boolean hasAccess(@NotNull S sender, boolean message) {
		return true;
	}

	/**
	 * Returns whether this {@link MCCommand} removes
	 * <a href=https://mcutils.codersky.net/for-server-admins/event-patterns>event patterns</a>
	 * from string getters or not. This is enabled by default and it is recommended.
	 * <p>
	 * Keep in mind that this doesn't modify the {@code args} {@link String} array from the
	 * {@link #onCommand(MCCommandSender, String[])} and {@link #onTab(MCCommandSender, String[])}
	 * methods but instead affects string getter methods such as {@link #asString(int, String[])},
	 * methods that convert arguments to other objects such as {@link #asNumber(int, String[], Class)}
	 * remain unaffected because they don't have this issue.
	 *
	 * @return {@code true} if this {@link MCCommand} removes
	 * <a href=https://mcutils.codersky.net/for-server-admins/event-patterns>event patterns</a>
	 * from string getters, {@code false} otherwise.
	 *
	 * @since MCUtils 1.0.0
	 */
	default boolean removesEventPatterns() {
		return true;
	}

	// ARGUMENT CONVERSION //

	/*
	 * Generic
	 */

	/**
	 * Converts the specified {@code arg} of the {@code args} array to any object by using the
	 * {@code converter} {@link Function}. Returning {@code def} if no argument is found at the
	 * {@code arg} position or if {@code converter} returns {@code null}.
	 *
	 * @param <T> The return type of the {@code converter} {@link Function}.
	 * @param converter the {@link Function} that will convert the {@link String}
	 * found at the specified {@code arg} position. The {@link String} passed
	 * to the {@link Function} will <b>never</b> be {@code null}.
	 * @param arg The array position of the argument to get, can be out of bounds.
	 * @param args The array of arguments to use.
	 * @param def The default value to return if {@code arg} is out of bounds or
	 * {@code converter} returns {@code null}.
	 *
	 * @return The argument converted by {@code converter} if found on the {@code args} array
	 * and {@code converter} doesn't return {@code null}. {@code def} otherwise.
	 *
	 * @throws NullPointerException if {@code args}, {@code converter} or {@code def} are {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	default <T> T asGeneric(@NotNull Function<String, T> converter, int arg, @NotNull String[] args, @NotNull T def) {
		Objects.requireNonNull(def, "def cannot be null. Remove the parameter instead.");
		if (args.length <= arg)
			return def;
		final T converted = converter.apply(args[arg]);
		return converted == null ? def : converted;
	}

	/**
	 * Converts the specified {@code arg} of the {@code args} array to any object by using the
	 * {@code converter} {@link Function}. Returning {@code null} if no argument is found at the
	 * {@code arg} position or if {@code converter} returns {@code null}.
	 *
	 * @param <T> The return type of the {@code converter} {@link Function}.
	 * @param converter The {@link Function} that will convert the {@link String}
	 * found at the specified {@code arg} position. The {@link String} passed
	 * to the {@link Function} will <b>never</b> be {@code null}.
	 * @param arg The array position of the argument to get, can be out of bounds.
	 * @param args The array of arguments to use.
	 *
	 * @return The argument as converted by {@code converter} if found
	 * on the {@code args} array, {@code null} otherwise.
	 *
	 * @throws NullPointerException If {@code args} or {@code converter} are {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	default <T> T asGeneric(@NotNull Function<String, T> converter, int arg, @NotNull String[] args) {
		return args.length > arg ? converter.apply(args[arg]) : null;
	}

	/*
	 * Strings
	 */

	// Regular //

	/**
	 * Converts the specified <b>arg</b> of the <b>args</b> array to a {@link String}, this
	 * method won't do any actual conversion and will just return the argument if found, <b>def</b> if not.
	 *
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 * @param def the default value to return if <b>arg</b> is out of bounds.
	 *
	 * @return The argument as a {@link String} if found on the <b>args</b> array, <b>def</b> otherwise.
	 *
	 * @throws NullPointerException if <b>args</b> is {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	default String asString(int arg, @NotNull String[] args, @NotNull String def) {
		Objects.requireNonNull(def, "def cannot be null. Remove the parameter instead.");
		final String result = args.length > arg ? args[arg] : def;
		return removesEventPatterns() ? MCStrings.stripEventPatterns(result) : result;
	}

	/**
	 * Converts the specified <b>arg</b> of the <b>args</b> array to a {@link String}, this
	 * method won't do any actual conversion and will just return the argument if found, null if not.
	 *
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 *
	 * @return The argument as a {@link String} if found on the <b>args</b> array, null otherwise.
	 *
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	default String asString(int arg, @NotNull String[] args) {
		final String result = args.length > arg ? args[arg] : null;
		return result != null && removesEventPatterns() ? MCStrings.stripEventPatterns(result) : result;
	}

	// With modifier //

	/**
	 * Converts the specified <b>arg</b> of the <b>args</b> array to a {@link String}, this
	 * method will apply <b>modifier</b> to the argument only if one is found and <b>def</b> isn't {@code null}.
	 * If no argument is found on the <b>arg</b> position and <b>def</b> is {@code null},
	 * {@code null} will be returned and the <b>modifier</b> won't be applied.
	 *
	 * @param modifier A {@link Function} to modify the resulting {@link String} that will be applied
	 * only if the {@link String} is not {@code null}.
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 * @param def the default value to return if <b>arg</b> is out of bounds.
	 *
	 * @return The argument as a {@link String} if found on the <b>args</b> array, {@code def} otherwise.
	 *
	 * @throws NullPointerException if <b>modifier</b> is {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	default String asString(@NotNull Function<String, String> modifier, int arg, @NotNull String[] args, @NotNull String def) {
		return modifier.apply(asString(arg, args, def));
	}

	/**
	 * Converts the specified <b>arg</b> of the <b>args</b> array to a {@link String}, this
	 * method will apply <b>modifier</b> to the argument only if one is found, if no argument is found on
	 * the <b>arg</b> position, {@code null} will be returned and the <b>modifier</b> won't be applied.
	 *
	 * @param modifier A {@link Function} to modify the resulting {@link String} that will be applied
	 * only if the {@link String} is not {@code null}.
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 *
	 * @return The argument as a {@link String} if found on the <b>args</b> array, null otherwise.
	 *
	 * @throws NullPointerException if <b>modifier</b> is {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	default String asString(@NotNull Function<String, String> modifier, int arg, @NotNull String[] args) {
		final String result = asString(arg, args);
		return result == null ? null : modifier.apply(result);
	}

	/*
	 * String ranges
	 */

	@NotNull
	private String buildRange(@Nullable Function<String, String> modifier, int fromArg, @NotNull String[] args, @NotNull String first) {
		final StringBuilder builder = new StringBuilder(first);
		final boolean removeEvents = removesEventPatterns();
		Function<String, String> finalModifier = null;
		if (modifier == null && removeEvents)
			finalModifier = MCStrings::stripEventPatterns;
		else if (modifier != null)
			finalModifier = str -> modifier.apply(MCStrings.stripEventPatterns(str));
		for (int i = fromArg + 1; i < args.length; i++)
			builder.append(' ').append(finalModifier == null ? args[i] : finalModifier.apply(args[i]));
		return builder.toString();
	}

	/**
	 * Gets a range of arguments starting at <b>fromArg</b> all the way to
	 * the end of <b>args</b>, then, converts that range of arguments to a
	 * {@link String}, separating them by a space character, exactly as typed
	 * by the user that ran the command.
	 *
	 * @param fromArg the array position of the first argument to get a range from, can be out of bounds.
	 * @param args the array of arguments to use.
	 * @param def the default value to return if <b>fromArg</b> is out of bounds.
	 *
	 * @return A {@link String} of the range of arguments from <b>fromArg</b> to the end of <b>args</b>,
	 * <b>def</b> if <b>fromArg</b> is out of bounds.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	default String asStringRange(int fromArg, @NotNull String[] args, @NotNull String def) {
		final String first = asString(fromArg, args);
		return first == null ? def : buildRange(null, fromArg, args, first);
	}

	/**
	 * Gets a range of arguments starting at <b>fromArg</b> all the way to
	 * the end of <b>args</b>, then, converts that range of arguments to a
	 * {@link String}, separating them by a space character, exactly as typed
	 * by the user that ran the command.
	 *
	 * @param fromArg the array position of the first argument to get a range from, can be out of bounds.
	 * @param args the array of arguments to use.
	 *
	 * @return A {@link String} of the range of arguments from <b>fromArg</b> to the end of <b>args</b>,
	 * null if <b>fromArg</b> is out of bounds.
	 *
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	default String asStringRange(int fromArg, @NotNull String[] args) {
		final String first = asString(fromArg, args);
		return first == null ? null : buildRange(null, fromArg, args, first);
	}

	/**
	 * Gets a range of arguments starting at <b>fromArg</b> all the way to
	 * the end of <b>args</b>, then, converts that range of arguments to a
	 * {@link String}, separating them by a space character, exactly as typed
	 * by the user that ran the command. If the range isn't out of bounds, then
	 * <b>modifier</b> will be applied to the resulting {@link String}.
	 *
	 * @param modifier the {@link Function} to apply to the resulting {@link String}. This will not be applied
	 * on <b>def</b> upon returning it.
	 * @param fromArg the array position of the first argument to get a range from, can be out of bounds.
	 * @param args the array of arguments to use.
	 * @param def the default value to return if <b>fromArg</b> is out of bounds.
	 *
	 * @return A {@link String} of the range of arguments from <b>fromArg</b> to the end of <b>args</b>,
	 * <b>def</b> if <b>fromArg</b> is out of bounds.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	default String asStringRange(@NotNull Function<String, String> modifier, int fromArg, @NotNull String[] args, @NotNull String def) {
		final String first = asString(fromArg, args);
		return first == null ? def : buildRange(modifier, fromArg, args, first);
	}

	/**
	 * Gets a range of arguments starting at <b>fromArg</b> all the way to
	 * the end of <b>args</b>, then, converts that range of arguments to a
	 * {@link String}, separating them by a space character, exactly as typed
	 * by the user that ran the command. If the range isn't out of bounds, then
	 * <b>modifier</b> will be applied to the resulting {@link String}.
	 *
	 * @param modifier the {@link Function} to apply to the resulting {@link String}.
	 * @param fromArg the array position of the first argument to get a range from, can be out of bounds.
	 * @param args the array of arguments to use.
	 *
	 * @return A {@link String} of the range of arguments from <b>fromArg</b> to the end of <b>args</b>,
	 * {@code null} if <b>fromArg</b> is out of bounds.
	 *
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	default String asStringRange(@NotNull Function<String, String> modifier, int fromArg, @NotNull String[] args) {
		final String first = asString(fromArg, args);
		return first == null ? null : buildRange(modifier, fromArg, args, first);
	}

	/*
	 * List ranges
	 */

	@Nullable
	default <T> List<T> asListRange(@NotNull Function<String, T> modifier, int fromArg, @NotNull String[] args, @Nullable List<T> def) {
		if (fromArg > args.length)
			return def;
		final List<T> lst = new ArrayList<>(args.length - fromArg);
		for (int i = fromArg + 1; i < args.length; i++)
			lst.add(modifier.apply(args[i]));
		return lst;
	}

	@Nullable
	default <T> List<T> asListRange(@NotNull Function<String, T> modifier, int fromArg, @NotNull String[] args) {
		return asListRange(modifier, fromArg, args, null);
	}

	@Nullable
	default List<String> asStringListRange(int fromArg, @NotNull String[] args, @Nullable List<String> def) {
		if (fromArg > args.length)
			return def;
		final List<String> lst = new ArrayList<>(args.length - fromArg);
		for (int i = fromArg + 1; i < args.length; i++)
			lst.add(MCStrings.stripEventPatterns(args[i]));
		return lst;
	}

	@Nullable
	default List<String> asStringListRange(int fromArg, @NotNull String[] args) {
		return asStringListRange(fromArg, args, null);
	}

	/*
	 * Numbers
	 */

	/**
	 * Converts the specified {@code arg} of the {@code args} array to a {@link Number}
	 * (See {@link MCNumbers#asNumber(CharSequence, Number)} for more details).
	 *
	 * @param <T> The type of {@link Number} to return.
	 * @param arg The array position of the argument to get, can be out of bounds.
	 * @param args The array of arguments to use.
	 * @param def The default value to return if {@code arg} is out of bounds or the argument isn't a valid number.
	 *
	 * @return The argument as a {@link Number} if found on the {@code args} array, {@code def} otherwise.
	 *
	 * @throws NullPointerException If {@code args} or {@code def} is {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 *
	 * @see MCNumbers#asNumber(CharSequence, Number)
	 */
	@NotNull
	default <T extends Number> T asNumber(int arg, @NotNull String[] args, @NotNull T def) {
		return asGeneric(str -> MCNumbers.asNumber(str, def), arg, args, def);
	}

	/**
	 * Converts the specified <b>arg</b> of the <b>args</b> array to a {@link Number}
	 * (See {@link MCNumbers#asNumber(CharSequence, Class)} for more details).
	 *
	 * @param <T> the type of {@link Number} to return.
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 *
	 * @return The argument as an {@link Number} if found on the <b>args</b> array, null otherwise.
	 *
	 * @throws NullPointerException If {@code args} or {@code type} is {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 *
	 * @see MCNumbers#asNumber(CharSequence, Class)
	 */
	@Nullable
	default <T extends Number> T asNumber(int arg, @NotNull String[] args, @NotNull Class<T> type) {
		return asGeneric(str -> MCNumbers.asNumber(str, type), arg, args);
	}

	/*
	 * Enums
	 */

	/**
	 * Converts the specified {@code arg} of the {@code args} array to an {@link Enum}.
	 * <p>
	 * The argument is converted {@link String#toUpperCase() to upper case} as enum
	 * constants must be upper case, so you don't have to check if the argument is
	 * upper case or not.
	 *
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 * @param enumClass the class of the {@link Enum} to get the constant from.
	 *
	 * @return The argument as an {@link Enum} if found on the {@code args} array, null otherwise.
	 *
	 * @throws NullPointerException if {@code args} or {@code enumClass} are {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	default <T extends Enum<T>> T asEnum(int arg, @NotNull String[] args, @NotNull Class<T> enumClass) {
		// TODO Maybe find a better way to do this...
		return asGeneric(str -> {
			try {
				return Enum.valueOf(enumClass, str);
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}, arg, args);
	}

	/**
	 * Converts the specified {@code arg} of the {@code args} array to an {@link Enum}.
	 * <p>
	 * The argument is converted {@link String#toUpperCase() to upper case} as enum
	 * constants must be upper case, so you don't have to check if the argument is
	 * upper case or not.
	 *
	 * @param arg the array position of the argument to get, can be out of bounds.
	 * @param args the array of arguments to use.
	 * @param def the default value to return if {@code arg} is out of bounds or the argument
	 * isn't a valid enum constant of the same class.
	 *
	 * @return The argument as an {@link Enum} if found on the {@code args} array, {@code def} otherwise.
	 *
	 * @throws NullPointerException if {@code args} or {@code def} are {@code null}.
	 *
	 * @since MCUtils 1.0.0
	 */
	@NotNull
	default <T extends Enum<T>> T asEnum(int arg, @NotNull String[] args, @NotNull T def) {
		return asGeneric(str -> {
			final String name = str.toUpperCase();
			for (T constant : def.getDeclaringClass().getEnumConstants())
				if (name.equals(str))
					return constant;
			return null;
		}, arg, args, def);
	}
}
