package nz.net.kallisti.emusicj.download;

/**
 *
 * 
 * $Id:$
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

}
