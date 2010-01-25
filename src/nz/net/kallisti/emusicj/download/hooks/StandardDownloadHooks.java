package nz.net.kallisti.emusicj.download.hooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadHook;
import nz.net.kallisti.emusicj.download.IDownloadHooks;
import nz.net.kallisti.emusicj.download.hooks.id3.ID3v2Hook;
import nz.net.kallisti.emusicj.download.hooks.mediaplayer.MediaPlayerHook;
import nz.net.kallisti.emusicj.mediaplayer.IMediaPlayerSync;
import nz.net.kallisti.emusicj.tagging.ITagToFile;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import com.google.inject.Inject;

/**
 * <p>
 * This is the normal list of download hooks. At the moment, just the media
 * player sync.
 * </p>
 * 
 * @author robin
 */
public class StandardDownloadHooks implements IDownloadHooks {

	List<IDownloadHook> hooks = new ArrayList<IDownloadHook>();

	@Inject
	public StandardDownloadHooks(IMediaPlayerSync mediaPlayer,
			IEmusicjView view, ITagToFile id3ToMp3) {
		MediaPlayerHook mediaPlayerHook = new MediaPlayerHook(mediaPlayer);
		ID3v2Hook id3Hook = new ID3v2Hook(view, id3ToMp3);
		// this list is ordered by what should happen first
		hooks.add(id3Hook);
		hooks.add(mediaPlayerHook);
	}

	public List<IDownloadHook> getCompletionHooks() {
		return Collections.unmodifiableList(hooks);
	}

}
