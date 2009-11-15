package nz.net.kallisti.emusicj.mediaplayer.windows;

import java.io.File;

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
	 */
	void addTrack(File track);

}
