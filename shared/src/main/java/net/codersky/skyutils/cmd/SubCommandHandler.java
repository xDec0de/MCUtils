package net.codersky.skyutils.cmd;

import net.codersky.skyutils.java.SkyCollections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

public class SubCommandHandler<P, S extends SkyCommandSender> {

	private final HashSet<SkyCommand<P, S>> subCommands = new HashSet<>();

	private <T> T onUsedCommand(@NotNull SkyCommand<P, S> mainCmd, @NotNull S sender, @NotNull String[] args,
	                            @NotNull BiFunction<SkyCommand<P, S>, String[], T> action, @NotNull T def, boolean message) {
		if (!mainCmd.hasAccess(sender, message))
			return def;
		if (args.length == 0)
			return action.apply(mainCmd, args);
		for (SkyCommand<P, S> subCommand : subCommands)
			if (subCommand.matches(args[0]))
				return onUsedCommand(subCommand, sender, Arrays.copyOfRange(args, 1, args.length), action, def, message);
		return action.apply(mainCmd, args);
	}

	/*
	 - Command execution
	 */

	public boolean onCommand(@NotNull SkyCommand<P, S> mainCmd, @NotNull S sender, @NotNull String[] args) {
		return onUsedCommand(mainCmd, sender, args, (cmd, newArgs) -> cmd.onCommand(sender, newArgs), true, true);
	}

	/*
	 - Tab complete
	 */

	public List<String> onTab(@NotNull SkyCommand<P, S> mainCommand, @NotNull S sender, @NotNull String[] args) {
		return onUsedCommand(mainCommand, sender, args, (cmd, newArgs) -> {
			if (subCommands.isEmpty() || args.length > 1)
				return prepareSuggestions(cmd.onTab(sender, newArgs), newArgs);
			List<String> cmdTabs = cmd.onTab(sender, newArgs);
			if (cmdTabs == null)
				cmdTabs = new ArrayList<>(0);
			final List<String> tabs = new ArrayList<>(subCommands.size() + cmdTabs.size());
			tabs.addAll(cmdTabs);
			for (SkyCommand<P, S> subCmd : subCommands) {
				if (!subCmd.hasAccess(sender, false))
					continue;
				tabs.add(subCmd.getName());
				tabs.addAll(subCmd.getAliases());
			}
			return prepareSuggestions(tabs, newArgs);
		}, List.of(), false);
	}

	/**
	 * Prepares a list of {@code suggestions} to be used by {@link #onTab(SkyCommand, SkyCommandSender, String[])}.
	 * By default, this method will:
	 * <ul>
	 * <li>Return an {@link List#of() empty list} if {@code suggestions} is {@code null}.</li>
	 * <li>Filter {@code suggestions} based on the last argument from {@code args}, case insensitive,
	 * meaning that {@code suggestions} that don't start with the last argument won't be included</li>
	 * </ul>
	 *
	 * @param suggestions The list of suggestions to prepare.
	 * @param args The command arguments to use for filtering.
	 *
	 * @return A {@link NotNull} list of suggestions to be used on the
	 * {@link #onTab(SkyCommand, SkyCommandSender, String[])} method.
	 *
	 * @since SkyUtils 1.0.0
	 */
	protected List<String> prepareSuggestions(@Nullable List<String> suggestions, @NotNull String[] args) {
		if (suggestions == null || suggestions.isEmpty())
			return List.of();
		if (args.length == 0)
			return suggestions;
		final String lastArg = args[args.length - 1].toLowerCase();
		return SkyCollections.clone(suggestions, tab -> tab.startsWith(lastArg));
	}

	/*
	 - SubCommand injection
	 */

	@SafeVarargs
	public final void inject(@NotNull SkyCommand<P, S>... commands) {
		Collections.addAll(subCommands, commands);
	}
}
