package net.codersky.skyutils.java.strings.pattern.target;

import net.codersky.skyutils.crossplatform.MessageReceiver;
import net.codersky.skyutils.crossplatform.player.MCPlayer;
import net.codersky.skyutils.java.math.MCNumbers;
import net.codersky.skyutils.java.strings.SkyStrings;
import net.codersky.skyutils.java.strings.pattern.TargetPattern;
import org.jetbrains.annotations.NotNull;

public class SoundTargetPattern implements TargetPattern {

	@NotNull
	@Override
	public String process(@NotNull MessageReceiver target, @NotNull String string, boolean applyEventPatterns) {
		return SkyStrings.match(string, "<sound:", "/>", message -> {
			if (!(target instanceof final MCPlayer player))
				return;
			final String[] soundInfo = message.split(";");
			float volume = 1;
			float pitch = 1;
			if (soundInfo.length >= 1)
				volume = MCNumbers.asNumber(soundInfo[0], 1f);
			if (soundInfo.length >= 2)
				pitch = MCNumbers.asNumber(soundInfo[1], 1f);
			final String sound = soundInfo[soundInfo.length - 1];
			player.playSound(sound.indexOf(":") > 1 ? sound : "minecraft:" + sound, volume, pitch);
		});
	}
}
