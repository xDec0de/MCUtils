package net.codersky.mcutils.velocity.cmd;

import net.codersky.mcutils.cmd.GlobalCommand;
import net.codersky.mcutils.cmd.MCCommand;
import net.codersky.mcutils.cmd.MCCommandSender;
import net.codersky.mcutils.velocity.VelocityUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdaptedVelocityCommand<P> extends VelocityCommand<P> {

	private final GlobalCommand<P> command;

	public AdaptedVelocityCommand(@NotNull VelocityUtils<P> utils, @NotNull GlobalCommand<P> command) {
		super(utils, command.getName(), command.getAliases().toArray(new String[0]));
		this.command = command;
	}

	@Override
	public boolean onCommand(@NotNull VelocityCommandSender sender, @NotNull String[] args) {
		return command.onCommand(sender, args);
	}

	@Override
	public @NotNull List<String> onTab(@NotNull VelocityCommandSender sender, @NotNull String[] args) {
		return command.onTab(sender, args);
	}

	@Override
	public boolean hasAccess(@NotNull VelocityCommandSender sender, boolean message) {
		return command.hasAccess(sender, message);
	}

	@NotNull
	@Override
	public AdaptedVelocityCommand<P> inject(MCCommand<P, VelocityCommandSender>... commands) {
		throw new UnsupportedOperationException("Adapted commands cannot inject new commands.");
	}
}
