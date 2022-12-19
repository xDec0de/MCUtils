package me.xdec0de.mcutils.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xdec0de.mcutils.strings.builders.Click;
import me.xdec0de.mcutils.strings.builders.Hover;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * A string utility class, also contains
 * methods for string lists.
 * 
 * @since MCUtils 1.0.0
 * 
 * @author xDec0de_
 * 
 * @see #applyColor(String)
 */
public class MCStrings {

	private HashMap<String, ColorPattern> colorPatterns = new HashMap<>();
	private LinkedList<ChatPattern> chatPatterns = new LinkedList<>();

	private final Pattern actionPattern = Pattern.compile("<(.*?)>(.*?)[/]>");

	public MCStrings() {
		addColorPattern(new Gradient(this));
		addColorPattern(new Hex());
		chatPatterns.add(new ActionBar());
		chatPatterns.add(new TargetPattern(this));
	}

	/**
	 * Sends <b>str</b> to <b>target</b> using the dynamic message format. This feature
	 * allows administrators to choose how and where a message will be sent, player specific
	 * message types such as {@link ActionBar} will be sent as a raw message to the console.
	 * <p>
	 * Here is the documentation of every format pattern:
	 * <p>
	 * - <a href=https://mcutils.xdec0de.me/chat-features/target-patterns>Target patterns</a>.
	 * <p>
	 * - <a href=https://mcutils.xdec0de.me/chat-features/event-patterns>Event patterns</a>.
	 * 
	 * @param target the {@link CommandSender} that will receive the message.
	 * @param str the message to send, if null or empty, nothing will be done.
	 * 
	 * @return Always true, to make sending messages on commands easier.
	 * 
	 * @throws IllegalArgumentException if <b>target</b> is null.
	 * 
	 * @since MCUtils 1.0.0
	 */
	public boolean sendFormattedMessage(@Nonnull CommandSender target, @Nullable String str) {
		if (target == null)
			throw new IllegalArgumentException("target cannot be null.");
		if (str == null || str.isEmpty())
			return true;
		String toChat = str;
		for (ChatPattern pattern : chatPatterns)
			toChat = pattern.process(target, toChat);
		if (toChat.isEmpty())
			return true;
		if (target instanceof Player)
			((Player)target).spigot().sendMessage(applyEventPatterns(toChat));
		else
			target.sendMessage(toChat);
		return true;
	}

	/**
	 * Applies <a href=https://mcutils.xdec0de.me/chat-features/event-patterns>Event patterns</a>
	 * to the specified string, note that this method won't apply the {@link TargetPattern} nor
	 * the {@link ActionBar} pattern as both patterns require a target to be used and send the
	 * message to said target when applied.
	 * 
	 * @param str the string that will have the events applied.
	 * 
	 * @return A {@link BaseComponent} array with the events applied to it,
	 * {@link TextComponent#toLegacyText()} will be used on <b>str</b> if no event was applied,
	 * null if <b>str</b> is null or empty.
	 * 
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	public BaseComponent[] applyEventPatterns(@Nullable String str) {
		if (str == null || str.isEmpty())
			return null;
		// Group 1: event args, Group 2: event text.
		ComponentBuilder res = new ComponentBuilder();
		Matcher matcher = actionPattern.matcher(str);
		int prevEnd = 0;
		while (matcher.find()) {
			final String prev = str.substring(prevEnd, matcher.start());
			final String[] args = matcher.group(1).split(";");
			if (args.length == 0 || args.length % 2 != 0)
				return null;
			res.append(TextComponent.fromLegacyText(prev), FormatRetention.FORMATTING).append(getAppliedContent(matcher.group(2), args));
			prevEnd = matcher.end();
		}
		return prevEnd != 0 ? res.append(TextComponent.fromLegacyText(str.substring(prevEnd))).create() : TextComponent.fromLegacyText(str);
	}

	private BaseComponent[] getAppliedContent(String text, String [] args) {
		BaseComponent[] content = TextComponent.fromLegacyText(text);
		for (int i = 0; i < args.length; i += 2) {
			switch(args[i].toLowerCase()) {
			case "text", "show_text" -> new Hover(HoverEvent.Action.SHOW_TEXT, args[i + 1]).apply(content);
			case "item", "show_item" -> new Hover(HoverEvent.Action.SHOW_ITEM, args[i + 1]).apply(content);
			case "entity", "show_entity" -> new Hover(HoverEvent.Action.SHOW_ENTITY, args[i + 1]).apply(content);
			case "url", "open_url" -> new Click(ClickEvent.Action.OPEN_URL, args[i + 1]).apply(content);
			case "file", "open_file" -> new Click(ClickEvent.Action.OPEN_FILE, args[i + 1]).apply(content);
			case "run", "run_cmd", "run_command" -> new Click(ClickEvent.Action.RUN_COMMAND, args[i + 1]).apply(content);
			case "suggest", "suggest_cmd", "suggest_command" -> new Click(ClickEvent.Action.SUGGEST_COMMAND, args[i + 1]).apply(content);
			case "copy", "copy_to_clipboard" -> new Click(ClickEvent.Action.COPY_TO_CLIPBOARD, args[i + 1]).apply(content);
			}
		}
		return content;
	}

	/**
	 * Gets a <b>previously registered</b> {@link ColorPattern} by class, that is,
	 * a {@link ColorPattern} that has been added using either {@link #addColorPattern(ColorPattern)}
	 * or {@link #addColorPatternBefore(ColorPattern, Class)}, if no {@link ColorPattern} matching
	 * <b>pattern</b> has been added, null will be returned.
	 * 
	 * @param <T> must extend {@link ColorPattern}.
	 * @param pattern the class of the {@link ColorPattern} to return.
	 * 
	 * @throws IllegalArgumentException If <b>pattern</b> is null.
	 * 
	 * @return an instance of <b>pattern</b> if registered, null otherwise.
	 */
	public <T extends ColorPattern> ColorPattern getColorPattern(@Nonnull Class<T> pattern) {
		if (pattern == null)
			throw new IllegalArgumentException("Pattern cannot be null");
		for (ColorPattern implPattern : colorPatterns.values())
			if (implPattern.getClass().equals(pattern))
				return implPattern;
		return null;
	}

	/**
	 * Adds a new {@link ColorPattern} to be used on {@link #applyColor(String)}
	 * and {@link #applyColor(List)}, it's important to take into account pattern
	 * order as patterns might conflict with one another. For example the {@link Hex}
	 * pattern directly conflicts with the {@link Gradient} pattern overwriting it 
	 * because {@link Gradient} uses hexadecimal colors, so {@link Gradient} needs to
	 * be added first as it doesn't overwrite {@link Hex}.
	 * <p>
	 * Want to add a {@link ColorPattern} that would be overwritten by a MCUtils
	 * {@link ColorPattern} like {@link Hex}? Use {@link #addColorPatternBefore(ColorPattern, Class)}
	 * 
	 * @param pattern the {@link ColorPattern} to add.
	 * 
	 * @throws IllegalArgumentException If <b>pattern</b> is null.
	 * 
	 * @see #addColorPatternBefore(ColorPattern, Class)
	 */
	public void addColorPattern(@Nonnull ColorPattern pattern) {
		if (pattern == null)
			throw new IllegalArgumentException("Pattern cannot be null");
		colorPatterns.put(pattern.getID().toLowerCase(), pattern);
	}

	/**
	 * Adds a new {@link ColorPattern} to be used on {@link #applyColor(String)}
	 * and {@link #applyColor(List)} before the specified <b>pattern</b>, it's
	 * important to take into account pattern order as patterns might conflict with
	 * one another. For example the {@link Hex} pattern directly conflicts with the
	 * {@link Gradient} pattern overwriting it because {@link Gradient} uses
	 * hexadecimal colors, so {@link Gradient} needs to be added before {@link Hex}.
	 * <p>
	 * Normally, this method won't be needed unless you are adding a pattern that uses
	 * hexadecimal colors on it's syntax as {@link Gradient} does. If that's not the case,
	 * you can just use {@link #addColorPattern(ColorPattern)} to improve performance a bit.
	 * 
	 * @param <T> must extend {@link ColorPattern}
	 * @param pattern the {@link ColorPattern} to add.
	 * @param before the {@link Class} of the {@link ColorPattern} that will
	 * go after <b>pattern</b>, putting <b>pattern</b> before it.
	 * 
	 * @throws IllegalArgumentException If <b>pattern</b> or <b>before</b> are null.
	 * 
	 * @see #addColorPattern(ColorPattern)
	 */
	public <T extends ColorPattern> void addColorPatternBefore(@Nonnull ColorPattern pattern, @Nonnull Class<T> before) {
		if (pattern == null)
			throw new IllegalArgumentException("Added pattern cannot be null");
		if (before == null)
			throw new IllegalArgumentException("Before pattern class cannot be null");
		final HashMap<String, ColorPattern> tempPatterns = new HashMap<>();
		boolean added = false;
		for (ColorPattern implPattern : colorPatterns.values()) {
			if (implPattern.getClass().equals(before)) {
				tempPatterns.put(pattern.getID().toLowerCase(), pattern);
				added = true;
			}
			tempPatterns.put(implPattern.getID(), implPattern);
		}
		if (!added)
			tempPatterns.put(pattern.getID().toLowerCase(), pattern);
		colorPatterns = tempPatterns;
	}

	/**
	 * Gets a <b>previously registered</b> {@link ChatPattern} by class, that is,
	 * a {@link ChatPattern} that has been added using either {@link #addChatPattern(ColorPattern)}
	 * or {@link #addChatPatternBefore(ChatPattern, Class)}, if no {@link ChatPattern} matching
	 * <b>pattern</b> has been added, null will be returned.
	 * 
	 * @param <T> must implement {@link ChatPattern}.
	 * @param pattern the class of the {@link ChatPattern} to return.
	 * 
	 * @throws IllegalArgumentException If <b>pattern</b> is null.
	 * 
	 * @return an instance of <b>pattern</b> if registered, null otherwise.
	 * 
	 * @see #addChatPattern(ChatPattern)
	 */
	public <T extends ChatPattern> ChatPattern getChatPattern(@Nonnull Class<T> pattern) {
		if (pattern == null)
			throw new IllegalArgumentException("Pattern cannot be null");
		for (ChatPattern implPattern : chatPatterns)
			if (implPattern.getClass().equals(pattern))
				return implPattern;
		return null;
	}

	/**
	 * Adds a new {@link ChatPattern} to be used on {@link #sendFormattedMessage(CommandSender, String)},
	 * it's important to take into account pattern order as patterns might conflict with one another.
	 * <p>
	 * Want to add a {@link ChatPattern} that would be overwritten by a MCUtils
	 * {@link ChatPattern}? Use {@link #addChatPatternBefore(ChatPattern, Class)}
	 * 
	 * @param pattern the {@link ChatPattern} to add.
	 * 
	 * @throws IllegalArgumentException If <b>pattern</b> is null.
	 * 
	 * @see #addChatPatternBefore(ColorPattern, Class)
	 */
	public void addChatPattern(@Nonnull ChatPattern pattern) {
		if (pattern == null)
			throw new IllegalArgumentException("Pattern cannot be null");
		chatPatterns.add(pattern);
	}

	/**
	 * Adds a new {@link ChatPattern} to be used on {@link #applyColor(String)}
	 * and {@link #applyColor(List)} before the specified <b>pattern</b>, it's
	 * important to take into account pattern order as patterns might conflict with
	 * one another. For example the {@link Hex} pattern directly conflicts with the
	 * {@link Gradient} pattern overwriting it because {@link Gradient} uses
	 * hexadecimal colors, so {@link Gradient} needs to be added before {@link Hex}.
	 * <p>
	 * Normally, this method won't be needed unless you are adding a pattern that uses
	 * hexadecimal colors on it's syntax as {@link Gradient} does. If that's not the case,
	 * you can just use {@link #addColorPattern(ColorPattern)} to improve performance a bit.
	 * 
	 * @param <T> must implement {@link ColorPattern}
	 * @param pattern the {@link ColorPattern} to add.
	 * @param before the {@link Class} of the {@link ColorPattern} that will
	 * go after <b>pattern</b>, putting <b>pattern</b> before it.
	 * 
	 * @throws IllegalArgumentException If <b>pattern</b> or <b>before</b> are null.
	 * 
	 * @see #addChatPattern(ColorPattern)
	 */
	public <T extends ChatPattern> void addChatPatternBefore(@Nonnull ChatPattern pattern, @Nonnull Class<T> before) {
		if (pattern == null)
			throw new IllegalArgumentException("Added pattern cannot be null");
		if (before == null)
			throw new IllegalArgumentException("Before pattern class cannot be null");
		final LinkedList<ChatPattern> tempPatterns = new LinkedList<>();
		boolean added = false;
		for (ChatPattern implPattern : chatPatterns) {
			if (implPattern.getClass().equals(before))
				added = tempPatterns.add(pattern); // Always true as the Collection changes.
			tempPatterns.add(implPattern);
		}
		if (!added)
			tempPatterns.add(pattern);
		chatPatterns = tempPatterns;
	}

	/**
	 * Applies all registered patterns to a <b>string</b> with hexadecimal
	 * color support, then, applies "traditional" colors with char '&' using
	 * {@link ChatColor#translateAlternateColorCodes(char, String)}
	 * 
	 * @param string the string to apply colors.
	 * @return The string, colored.
	 * 
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	public String applyColor(@Nullable String string) {
		if (string == null)
			return null;
		String res = string;
		for (ColorPattern pattern : colorPatterns.values())
			res = pattern.process(res);
		return applyColorChar('&', res);
	}

	/**
	 * Applies colors to every string of <b>lst</b>
	 * using {@link #applyColor(String)}. If <b>lst</b>
	 * is null, null elements on the list will be kept
	 * as null.
	 * 
	 * @param lst the list to apply colors.
	 * 
	 * @return The list, colored.
	 * 
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	public List<String> applyColor(@Nullable List<String> lst) {
		if (lst == null)
			return null;
		List<String> res = new ArrayList<>(lst.size());
		for (String str : lst)
			res.add(applyColor(str));
		return res;
	}

	/**
	 * Similar to {@link ChatColor#translateAlternateColorCodes(char, String)},
	 * replaces every occurrence of <b>ch</b> with {@link ChatColor#COLOR_CHAR} if
	 * followed by a valid color character ({@link #isColorChar(char)}),
	 * performance differences aren't noticeable, this method exists for accessibility
	 * purposes... And to have a shorter name.
	 * 
	 * @param ch the character to replace, normally '&'.
	 * @param str the string to apply color characters to.
	 * 
	 * @return The string with translated color characters, null if <b>str</b> is null.
	 * 
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	public String applyColorChar(@Nonnull char ch, @Nullable String str) {
		if (str == null)
			return null;
		final int length = str.length() - 1;
		char[] arr = str.toCharArray();
		for (int i = 0; i < length; i++) {
			char strCh = str.charAt(i);
			if (strCh == ch && isColorChar(str.charAt(i + 1)))
				arr[i++] = ChatColor.COLOR_CHAR;
		}
		return new String(arr);
	}

	/**
	 * Strips all vanilla chat formatting from the specified string,
	 * that is, color and text formatting, for example, assuming that
	 * <b>colorChar</b> is '&', this method will remove all occurrences
	 * of &[a-f], &[0-9], &[k-o] and &r, leaving the string as an uncolored,
	 * unformatted, simple string, doing the same with {@link ChatColor#COLOR_CHAR}
	 * <p>
	 * This method has been tested to be quite faster than {@link ChatColor#stripColor(String)}
	 * as {@link ChatColor}'s method uses regex, however, this difference isn't
	 * really noticeable unless millions of strings are stripped.
	 * 
	 * @param str the string to strip colors.
	 * 
	 * @return The string with stripped colors, null if <b>str</b> is null.
	 * 
	 * @since MCUtils 1.0.0
	 */
	public String stripColor(String str, char colorChar) {
		if (str == null)
			return null;
		final int length = str.length();
		char[] arr = new char[length];
		int arrIndex = 0;
		for (int i = 0; i < length; i++) {
			char ch = str.charAt(i);
			if ((ch == colorChar || ch == ChatColor.COLOR_CHAR) && (i + 1 < length) && isColorChar(str.charAt(i + 1)))
				i++;
			else {
				arr[arrIndex] = ch;
				arrIndex++;
			}
		}
		return new String(arr);
	}

	/**
	 * A simple convenience method that checks if <b>ch</b> is a character
	 * that can be used to apply color or formatting to a string, that is,
	 * r, R, x, X, or a character between this ranges: [a-f], [A-F], [k-o], [K-O] and [0-9].
	 * 
	 * @param ch the character to check.
	 * 
	 * @return true if the character is a color character, false otherwise.
	 * 
	 * @since MCUtils 1.0.0
	 */
	public boolean isColorChar(char c) {
		final char ch = Character.toLowerCase(c);
		return (ch == 'r' || ch == 'x' || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'k' && ch <= 'o'));
	}

	/**
	 * Checks if a string has any content on it. If
	 * {@link String#isBlank()} returns true or the
	 * string is null, false will be returned.
	 * 
	 * @param str the string to check.
	 * 
	 * @return true if the string has content, false otherwise.
	 * 
	 * @since MCUtils 1.0.0
	 */
	public boolean hasContent(@Nullable String str) {
		return str == null ? false : (!str.isBlank());
	}

	/**
	 * Gets a string list as a string with all elements separated by
	 * the specified <b>separator</b>, null elements will be ignored.
	 * If <b>lst</b> is null, null will be returned, if <b>separator</b>
	 * is null, "null" will be used as a separator.
	 * <p>
	 * This method doesn't treat strings without content as elements, meaning
	 * no separator will be added to them (See {@link #hasContent(String)})
	 * <p>
	 * Example:
	 * <p>
	 * Array: [" ", "a", null, "b", "c"]<br>
	 * Separator: ", "<br>
	 * Returns: " a, b, c"
	 * 
	 * @param lst the list to use.
	 * @param separator the separator to use.
	 * 
	 * @return A string list as a string with all elements separated by
	 * the specified <b>separator</b>.
	 * 
	 * @since MCUtils 1.0.0
	 */
	@Nullable
	public String asString(@Nullable List<String> lst, @Nullable String separator) {
		String res = "";
		if (lst == null)
			return null;
		for (String str : lst) {
			if (str == null)
				continue;
			res += hasContent(res) ? separator+str : str;
		}
		return res;
	}

	/**
	 * Check if <b>str</b> is numeric, meaning that it should
	 * only contain numeric characters between '0' and '9', the
	 * first character of the string can be a sign ('+' or '-').
	 * If {@link #hasContent(String)} returns false, this method
	 * will also return false.
	 * 
	 * @param str the string to check.
	 * 
	 * @return True if <b>str</b> is numeric, false otherwise.
	 * 
	 * @since MCUtils 1.0.0
	 */
	public boolean isNumeric(String str) {
		if (!hasContent(str))
			return false;
		final char sign = str.charAt(0);
		for (int i = (sign == '-' || sign == '+') ? 1 : 0; i < str.length(); i++) {
			final char ch = str.charAt(i);
			if (ch < '0' || ch > '9')
				return false;
		}
		return true;
	}
}
