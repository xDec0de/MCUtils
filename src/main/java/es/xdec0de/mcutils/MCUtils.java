package es.xdec0de.mcutils;

import javax.annotation.Nonnull;

import es.xdec0de.mcutils.general.MCStrings;
import es.xdec0de.mcutils.general.PlayerUtils;

/**
 * The main class of the MCUtils API, used
 * to access the majority of the features of
 * said API.
 * <p>
 * <b>Getting an instance on a class extending {@link MCPlugin}:</b>
 * <p>
 * <code>MCUtils mcUtils = this.getMCUtils();
 * <p>
 * MCStrings strings = mcUtils.strings();
 * </code>
 * <p>
 * <b>Getting an instance on any class <i>(Not recommended, use constructor parameters):</i></b>
 * <p>
 * <code>MCUtils mcUtils = JavaPlugin.getPlugin(MCUtils.class);
 * <p>
 * MCStrings strings = mcUtils.strings();
 * </code>
 * 
 * @since MCUtils 1.0.0
 * 
 * @author xDec0de_
 */
public class MCUtils extends MCPlugin {

	private final MCStrings strings = new MCStrings(this);

	@Override
	public void onEnable() {
		logCol("&8[&6MCUtils&8] &aPlugin enabled &8| &bv"+getDescription().getVersion()+" &8| &bMC "+getServerVersion().getFormatName());
	}

	@Override
	public void onDisable() {
		logCol("&8[&6MCUtils&8] &cPlugin disabled &8| &bv"+getDescription().getVersion()+" &8| &bMC "+getServerVersion().getFormatName());
	}

	/**
	 * Gets an instance of {@link MCStrings}.
	 * 
	 * @return An instance of {@link MCStrings}.
	 * 
	 * @since MCUtils 1.0.0
	 */
	@Nonnull
	public MCStrings strings() {
		return strings;
	}

	/**
	 * Gets an instance of {@link PlayerUtils}, this
	 * method exists purely for accessibility, you
	 * can just call {@link PlayerUtils}'s constructor.
	 * 
	 * @return An instance of {@link PlayerUtils}.
	 * 
	 * @since MCUtils 1.0.0
	 */
	public PlayerUtils players() {
		return new PlayerUtils();
	}
}
