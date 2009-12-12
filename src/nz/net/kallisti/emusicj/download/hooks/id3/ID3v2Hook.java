package nz.net.kallisti.emusicj.download.hooks.id3;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.download.IDownloadHook;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3ToMP3;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import com.google.inject.Inject;

/**
 * <p>
 * This hook handles the writing of ID3v2 tags into the music file.
 * </p>
 * 
 * @author robin
 */
public class ID3v2Hook implements IDownloadHook {

	private final IEmusicjView view;
	private final IID3ToMP3 id3ToMP3;
	private final Logger logger;

	@Inject
	public ID3v2Hook(IEmusicjView view, IID3ToMP3 id3ToMP3) {
		this.view = view;
		this.id3ToMP3 = id3ToMP3;
		logger = LogUtils.getLogger(this);
	}

	public void downloadEvent(EventType type, IDownloader downloader) {
		if (!(downloader instanceof MusicDownloader))
			return;
		MusicDownloader dl = (MusicDownloader) downloader;
		IID3Data id3 = dl.getID3();
		if (id3 == null)
			return;
		File file = dl.getOutputFile();
		if (!file.getName().toLowerCase().endsWith(".mp3"))
			return;
		try {
			id3ToMP3.writeMP3(id3, file);
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"An error occurred saving the MP3 tag data", e);
			view.error("Error writing file descriptions",
					"An error occurred saving ID3 information to the downloaded file:\n"
							+ e.getMessage());
		}
	}

}
