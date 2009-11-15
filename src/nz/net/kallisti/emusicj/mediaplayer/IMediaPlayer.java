package nz.net.kallisti.emusicj.mediaplayer;

import java.io.File;
import java.util.Set;

/**
 * <p>
 * This is the core interface to all media-player related functions. The purpose
 * of these functions is to allow media players on the system to be updated with
 * the tracks as they get downloaded.
 * </p>
 * <p>
 * Implementations of this will do things in a way that makes sense for the
 * platform and the player, but generally it will be along the lines of adding
 * tracks to a playlist. This will also be able to supply lists of supported
 * players to allow the user to choose which one they want to use.
 * </p>
 * 
 * @author robin
 */
public interface IMediaPlayer {

	/**
	 * This provides a set containing the media players that are supported
	 * 
	 * @return the supported media players
	 */
	public Set<IPlayer> supportedPlayers();

	/**
	 * This sets the player to be used.
	 * 
	 * @param playerKey
	 *            the key of the player to be used. If this is null
	 *            <code>null</code>, then no player is used, and the functional
	 *            operations become noops.
	 * @throws UnknownPlayerException
	 *             if the supplied key doesn't match a known player. If this
	 *             occurs, then the current selection is not changed.
	 */
	public void setPlayer(String playerKey) throws UnknownPlayerException;

	/**
	 * This adds a track to the media player. Failures are hidden, so this
	 * should never cause any exception.
	 * 
	 * @param track
	 *            the path to the track to add
	 */
	public void addTrack(File track);

}
