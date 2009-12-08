package nz.net.kallisti.emusicj.mediaplayer;

import java.util.logging.Level;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * Standard configuration of the media player
 * </p>
 * 
 * @author robin
 */
public class ConfigureMediaPlayer implements IConfigureMediaPlayer {

	@Inject
	public ConfigureMediaPlayer(IMediaPlayerSync mediaPlayer, IStrings strings,
			IPreferences prefs) {
		mediaPlayer.setPlaylist(strings.getShortAppName());
		String playerKey = prefs.getMediaPlayerSync();
		try {
			mediaPlayer.setPlayer(playerKey);
		} catch (UnknownPlayerException e) {
			// This really shouldn't happen
			LogUtils.getLogger(this).log(Level.SEVERE,
					"Unable to set initial player type", e);
			try {
				mediaPlayer.setPlayer(null);
			} catch (UnknownPlayerException e1) {
				// This won't happen
			}
			prefs.setMediaPlayerSync(null);
		}
	}

}
