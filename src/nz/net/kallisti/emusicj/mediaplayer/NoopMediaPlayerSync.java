package nz.net.kallisti.emusicj.mediaplayer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * If we don't recognise the platform, we need to supply a dummy instance of the
 * media player sync implementation so that things don't break. This is it, and
 * it will do nothing except comply with the API.
 * </p>
 * 
 * @author robin
 */
public class NoopMediaPlayerSync implements IMediaPlayerSync {

	public void addTrack(File track) {
		// do nothing
	}

	public void setPlayer(String playerKey) throws UnknownPlayerException {
		// do nothing
	}

	public void setPlaylist(String playlist) {
		// do nothing
	}

	public Set<IPlayer> supportedPlayers() {
		return new HashSet<IPlayer>(0);
	}

}
