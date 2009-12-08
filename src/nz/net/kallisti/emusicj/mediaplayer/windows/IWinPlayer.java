package nz.net.kallisti.emusicj.mediaplayer.windows;

import java.io.File;

import nz.net.kallisti.emusicj.mediaplayer.IMediaPlayerSync;

/**
 * <p>
 * This is an interface common to the windows players.
 * </p>
 * 
 * @author robin
 */
interface IWinPlayer {

	/**
	 * Adds a track to the player
	 * 
	 * @param track
	 *            the track to add
	 * @param playlist
	 *            the name of the playlist that the track should be added to.
	 *            May be <code>null</code>, see
	 *            {@link IMediaPlayerSync#setPlaylist(String)} for more details.
	 */
	void addTrack(File track, String playlist);

}
