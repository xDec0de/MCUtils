package net.codersky.skyutils.spigot.cmd;

import net.codersky.skyutils.cmd.GlobalCommand;
import net.codersky.skyutils.cmd.SkyCommand;
import net.codersky.skyutils.spigot.SpigotUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdaptedSpigotCommand<P extends JavaPlugin> extends SpigotCommand<P> {

	private final GlobalCommand<P> command;

	public AdaptedSpigotCommand(@NotNull SpigotUtils<P> utils, @NotNull GlobalCommand<P> command) {
		super(utils, command.getName(), command.getAliases().toArray(new String[0]));
		this.command = command;
	}

	@Override
	public boolean onCommand(@NotNull SpigotCommandSender sender, @NotNull String[] args) {
		return command.onCommand(sender, args);
	}

	@Nullable
	@Override
	public List<String> onTab(@NotNull SpigotCommandSender sender, @NotNull String[] args) {
		return command.onTab(sender, args);
	}

	@Override
	public boolean hasAccess(@NotNull SpigotCommandSender sender, boolean message) {
		return command.hasAccess(sender, message);
	}

	@NotNull
	@Override
	public AdaptedSpigotCommand<P> inject(@NotNull SkyCommand<P, SpigotCommandSender>... commands) {
		throw new UnsupportedOperationException("Adapted commands cannot inject new commands.");
	}
}
