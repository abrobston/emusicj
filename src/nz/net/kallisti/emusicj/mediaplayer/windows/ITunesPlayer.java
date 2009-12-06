package nz.net.kallisti.emusicj.mediaplayer.windows;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;

import com.dt.iTunesController.ITPlaylist;
import com.dt.iTunesController.ITPlaylistCollection;
import com.dt.iTunesController.ITSource;
import com.dt.iTunesController.ITSourceCollection;
import com.dt.iTunesController.ITUserPlaylist;
import com.dt.iTunesController.iTunes;

/**
 * <p>
 * This is the interface to the iTunes application. When a track is added, it
 * will check for a playlist named after the the application short name, create
 * it if necessary, and add the track to it.
 * </p>
 * 
 * @author robin
 */
public class ITunesPlayer implements IWinPlayer {

	public static final String KEY = "itunes";
	public static final String NAME = "iTunes";
	private iTunes itunes;
	private final Logger logger;

	public ITunesPlayer() {
		logger = LogUtils.getLogger(this);
	}

	public synchronized void addTrack(File track, String playlistName) {
		try {
			if (itunes == null)
				itunes = new iTunes();
			// Check for our playlist
			ITUserPlaylist pl = getPlaylist(itunes, playlistName);
			if (pl == null)
				// Whatever happened has already been logged
				return;
			pl.addFile(track.getPath());
		} catch (Exception e) {
			// We don't want the DLM to crash, whereas it's sorta OK for the
			// iTunes integration to fail
			logger
					.log(Level.WARNING, "Failure while adding track to iTunes",
							e);
		}
	}

	private final HashMap<String, ITUserPlaylist> playlistCache = new HashMap<String, ITUserPlaylist>(
			1);

	/**
	 * This will pull a playlist from a cache. If it's not there, it'll find it
	 * in iTunes. If it's not there, it'll create it.
	 * 
	 * @param itunes
	 *            the itunes communication object
	 * @param playlistName
	 *            the name of the playlist that we're looking for. May not be
	 *            <code>null</code>.
	 * @return a playlist, or <code>null</code> if it wasn't possible to create
	 *         one
	 */
	private ITUserPlaylist getPlaylist(iTunes itunes, String playlistName) {
		if (playlistCache.containsKey(playlistName))
			return playlistCache.get(playlistName);
		if (playlistName == null) {
			logger.severe("Unable to add tracks to itunes without a playlist");
			return null;
		}
		ITSourceCollection sources = itunes.getSources();
		ITUserPlaylist playlist = null;
		OUTER: for (int s = 0; s < sources.getCount(); s++) {
			// things index from 1 in this place
			ITSource src = sources.getItem(s + 1);
			ITPlaylistCollection playlists = src.getPlaylists();
			for (int p = 0; p < playlists.getCount(); p++) {
				ITPlaylist pl = playlists.getItem(p + 1);
				if (pl instanceof ITUserPlaylist
						&& playlistName.equals(pl.getName())) {
					playlist = (ITUserPlaylist) pl;
					break OUTER;
				}
			}
		}
		if (playlist == null) {
			// Create one
			ITPlaylist pl = itunes.createPlaylist(playlistName);
			if (!(pl instanceof ITUserPlaylist)) {
				logger.severe("Unable to create an itunes playlist, got back: "
						+ pl);
				return null;
			}
			playlist = (ITUserPlaylist) pl;
		}

		playlistCache.put(playlistName, playlist);
		return playlist;
	}
}
