package nz.net.kallisti.emusicj.download.hooks.tagging;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.bindingtypes.ID3Tagger;
import nz.net.kallisti.emusicj.bindingtypes.VorbisTagger;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagWriter;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import com.google.inject.Inject;

/**
 * <p>
 * This hook handles the writing of ID3v2 tags into the music file.
 * </p>
 * 
 * @author robin
 */
public class TaggingHook implements ITaggingHook {

	private final IEmusicjView view;
	private final ITagWriter id3Tagger;
	private final Logger logger;
	private final ITagWriter vorbisTagger;

	@Inject
	public TaggingHook(IEmusicjView view, @ID3Tagger ITagWriter id3Tagger,
			@VorbisTagger ITagWriter vorbisTagger) {
		this.view = view;
		this.id3Tagger = id3Tagger;
		this.vorbisTagger = vorbisTagger;
		logger = LogUtils.getLogger(this);
	}

	public void downloadEvent(EventType type, IDownloader downloader) {
		if (!(downloader instanceof MusicDownloader))
			return;
		MusicDownloader dl = (MusicDownloader) downloader;
		ITagData tagData = dl.getID3();
		if (tagData == null)
			return;
		File file = dl.getOutputFile();
		if (!file.getName().toLowerCase().endsWith(".mp3"))
			return;
		try {
			if (id3Tagger.supportedFile(file))
				id3Tagger.writeTag(tagData, file);
			else if (vorbisTagger.supportedFile(file))
				vorbisTagger.writeTag(tagData, file);
			else
				logger.info("No supported tagger for " + file);
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"An error occurred saving the MP3 tag data", e);
			view.error("Error writing file descriptions",
					"An error occurred saving ID3 information to the downloaded file:\n"
							+ e.getMessage());
		}
	}
}
