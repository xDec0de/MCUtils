package net.codersky.skyutils.spigot.player;

import net.codersky.skyutils.crossplatform.player.MCPlayer;
import net.codersky.skyutils.crossplatform.player.PlayerProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpigotPlayerProvider extends PlayerProvider<Player> {

	@Override
	protected @Nullable MCPlayer fetchPlayer(@NotNull UUID uuid) {
		final Player bukkit = Bukkit.getPlayer(uuid);
		return bukkit == null ? null : new SpigotPlayer(bukkit);
	}

	@Override
	public @Nullable UUID getUUID(@NotNull Player handle) {
		return handle.getUniqueId();
	}

	@Override
	protected final void removeFromCache(@NotNull UUID uuid) {
		super.removeFromCache(uuid);
	}
}
