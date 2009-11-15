package nz.net.kallisti.emusicj.mediaplayer;

/**
 * <p>
 * This represents a particular player, suitable for having the user select
 * which player they want, and saving that value.
 * </p>
 * 
 * @author robin
 */
public interface IPlayer {

	/**
	 * The full, human-readable name of the type of media player
	 * 
	 * @return the name of the media player
	 */
	public String playerName();

	/**
	 * A unique key that identifies that player internally. This is what will be
	 * saved into the settings file.
	 * 
	 * @return the key that represents this player
	 */
	public String key();

}
