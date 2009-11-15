package nz.net.kallisti.emusicj.mediaplayer.windows;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.mediaplayer.IMediaPlayer;
import nz.net.kallisti.emusicj.mediaplayer.IPlayer;
import nz.net.kallisti.emusicj.mediaplayer.UnknownPlayerException;
import nz.net.kallisti.emusicj.misc.LogUtils;

import com.google.inject.Inject;

/**
 * <p>
 * This handles players on Windows.
 * </p>
 * 
 * @author robin
 */
public class WindowsPlayers implements IMediaPlayer {

	private final Set<IPlayer> players = new HashSet<IPlayer>();
	private IWinPlayer player = null;
	private final Logger logger;

	@Inject
	public WindowsPlayers() {
		logger = LogUtils.getLogger(this);
		players.add(new PlayerType("iTunes", "itunes"));
	}

	public void addTrack(File track) {
		if (player == null)
			return;
		try {
			player.addTrack(track);
		} catch (Throwable e) {
			logger.log(Level.WARNING,
					"Error attempting to add track to media player (track="
							+ track + ")", e);
		}
	}

	public void setPlayer(String playerKey) throws UnknownPlayerException {
		if (playerKey == null) {
			player = null;
		} else if (playerKey.equals("itunes")) {
			player = new ITunesPlayer();
		} else {
			throw new UnknownPlayerException("Unknown player key: " + playerKey);
		}
	}

	public Set<IPlayer> supportedPlayers() {
		return players;
	}

	private class PlayerType implements IPlayer {

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

}
