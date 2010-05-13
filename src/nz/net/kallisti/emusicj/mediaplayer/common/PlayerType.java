package nz.net.kallisti.emusicj.mediaplayer.common;

import nz.net.kallisti.emusicj.mediaplayer.IPlayer;

/**
 * <p>
 * A convenience class that manages player type records
 * </p>
 * 
 * @author robin
 */
public class PlayerType implements IPlayer {

	private final String name;
	private final String key;

	public PlayerType(String name, String key) {
		this.name = name;
		this.key = key;
	}

	public String key() {
		return key;
	}

	public String playerName() {
		return name;
	}

}