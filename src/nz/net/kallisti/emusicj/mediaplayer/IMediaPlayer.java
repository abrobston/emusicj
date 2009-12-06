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
	 * This sets the player to be used. Not calling this is the equivalent of
	 * calling it with <code>null</code> (i.e. all other functions become
	 * no-op.)
	 * 
	 * @param playerKey
	 *            the key of the player to be used. If this is null
	 *            <code>null</code>, then no player is used, and the functional
	 *            operations become no-ops.
	 * @throws UnknownPlayerException
	 *             if the supplied key doesn't match a known player. If this
	 *             occurs, then the current selection is not changed.
	 */
	public void setPlayer(String playerKey) throws UnknownPlayerException;

	/**
	 * This sets a playlist that the tracks will be saved into (if supported by
	 * the player.) If this is <code>null</code>, then no playlist will be used
	 * if possible. It is recommended that this is used, and if it is, it should
	 * happen prior to any calls to {@link #addTrack(File)}.
	 * 
	 * If the player doesn't support playlists, then this is ignored. If the
	 * player requires playlists and none is supplied, then adding tracks
	 * probably won't work.
	 * 
	 * @param playlist
	 *            the name of the playlist that tracks should be added to.
	 */
	public void setPlaylist(String playlist);

	/**
	 * This adds a track to the media player. Failures are hidden (but logged),
	 * so this should never cause any exception. Ideally, calls to
	 * {@link #setPlayer(String)} and {@link #setPlaylist(String)} will have
	 * come before this.
	 * 
	 * @param track
	 *            the path to the track to add
	 */
	public void addTrack(File track);

}
