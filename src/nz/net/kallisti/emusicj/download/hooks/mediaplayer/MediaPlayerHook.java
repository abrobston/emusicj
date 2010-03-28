package nz.net.kallisti.emusicj.download.hooks.mediaplayer;

import java.io.File;

import nz.net.kallisti.emusicj.download.IDownloadHook;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.mediaplayer.IMediaPlayerSync;

/**
 * <p>
 * This handles syncing to a media player when downloads complete.
 * </p>
 * 
 * @author robin
 */
public class MediaPlayerHook implements IDownloadHook {

	private final IMediaPlayerSync mediaPlayer;

	/**
	 * Creates an instances of the media player hook
	 * 
	 * @param mediaPlayer
	 *            the media player object
	 */
	public MediaPlayerHook(IMediaPlayerSync mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public void downloadEvent(EventType type, IDownloader downloader) {
		if (type != EventType.FINISHED)
			return;
		// We only add music to the media player
		if (!(downloader instanceof MusicDownloader))
			return;
		File file = downloader.getOutputFile();
		mediaPlayer.addTrack(file);
	}

}
