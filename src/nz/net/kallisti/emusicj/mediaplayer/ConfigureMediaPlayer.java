package nz.net.kallisti.emusicj.mediaplayer;

import java.util.logging.Level;

import nz.net.kallisti.emusicj.controller.IPreferenceChangeListener;
import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * Standard configuration of the media player. Also monitors updates to the
 * preferences, and sets the media player stuff accordingly.
 * </p>
 * 
 * @author robin
 */
public class ConfigureMediaPlayer implements IConfigureMediaPlayer {

	@Inject
	public ConfigureMediaPlayer(final IMediaPlayerSync mediaPlayer,
			IStrings strings, final IPreferences prefs) {
		mediaPlayer.setPlaylist(strings.getShortAppName());
		String playerKey = prefs.getMediaPlayerSync();
		setPlayer(mediaPlayer, prefs, playerKey);
		prefs.addListener(new IPreferenceChangeListener() {
			public void preferenceChanged(Pref pref) {
				if (pref != Pref.MEDIA_PLAYER_SYNC)
					return;
				setPlayer(mediaPlayer, prefs, prefs.getMediaPlayerSync());
			}
		});
	}

	private void setPlayer(IMediaPlayerSync mediaPlayer, IPreferences prefs,
			String playerKey) {
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
