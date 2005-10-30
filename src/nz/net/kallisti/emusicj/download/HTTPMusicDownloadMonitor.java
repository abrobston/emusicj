package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public class HTTPMusicDownloadMonitor extends HTTPDownloadMonitor 
	implements IMusicDownloadMonitor {
	
	public HTTPMusicDownloadMonitor() {
		super();
	}
	
	public HTTPMusicDownloadMonitor(HTTPMusicDownloader downloader) {
		super(downloader);
	}
	
	public String getName() {
		return getMusicDownloader().getTrackName()+" - "+
		getMusicDownloader().getAlbumName() + " - "+
		getMusicDownloader().getArtistName();
	}
	
	public IMusicDownloader getMusicDownloader() {
		return (IMusicDownloader)getDownloader();
	}
	
	public String getAlbumName() {
		return getMusicDownloader().getAlbumName();
	}
	
	public String getArtistName() {
		return getMusicDownloader().getArtistName();
	}
	
	public String getTrackName() {
		return getMusicDownloader().getTrackName();
	}
	
	public int getTrackNum() {
		return getMusicDownloader().getTrackNum();
	}

	public File getCoverArt() {
		return getMusicDownloader().getCoverArt();
	}
	
}
