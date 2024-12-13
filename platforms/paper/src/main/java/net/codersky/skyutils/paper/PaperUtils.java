package net.codersky.skyutils.paper;

import net.codersky.skyutils.MCPlatform;
import net.codersky.skyutils.SkyUtils;
import net.codersky.skyutils.crossplatform.server.ServerUtils;
import net.codersky.skyutils.spigot.SpigotUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Paper platform extension of the {@link SpigotUtils} class,
 * which at the same time extends the {@link ServerUtils} and
 * {@link SkyUtils} classes. It offers access to the SkyUtils
 * API for Paper servers.
 * <p>
 * The use of the Spigot platform is recommended unless you
 * are making a plugin for Paper only, incompatible with Spigot.
 *
 * @param <P> The {@link JavaPlugin} that owns this utils.
 *
 * @since SkyUtils 1.0.0
 *
 * @author xDec0de_
 */
public class PaperUtils<P extends JavaPlugin> extends SpigotUtils<P> {

	public PaperUtils(@NotNull P plugin) {
		super(plugin);
	}

	@NotNull
	@Override
	public MCPlatform getPlatform() {
		return MCPlatform.PAPER;
	}
}