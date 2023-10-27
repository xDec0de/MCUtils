package me.xdec0de.mcutils.java.strings.pattern.format;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity.Spigot;
import org.bukkit.entity.Player;

import me.xdec0de.mcutils.java.strings.MCStrings;
import me.xdec0de.mcutils.java.strings.pattern.FormatPattern;

public class TargetPattern implements FormatPattern {

	@Nullable
	public String process(@Nonnull CommandSender target, @Nullable String message) {
		final Spigot asPlayer = target instanceof Player ? ((Player)target).spigot() : null;
		// Process for players
		String processed = MCStrings.match(message, "<p:", "/p>", msg -> {
			if (asPlayer != null)
				asPlayer.sendMessage(MCStrings.applyEventPatterns(msg));
		});
		// Process for the console
		processed = MCStrings.match(processed, "<c:", "/c>", msg -> {
			if (asPlayer == null)
				target.sendMessage(msg);
		});
		// Send any message left for both players and the console.
		if (MCStrings.hasContent(processed)) {
			if (asPlayer != null)
				asPlayer.sendMessage(MCStrings.applyEventPatterns(processed));
			else
				target.sendMessage(processed);
		}
		return processed;
	}
}
