package nz.net.kallisti.emusicj.download;


/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public interface IMusicDownloadMonitor extends IDownloadMonitor, 
IDisplayableDownloadMonitor {

	public IMusicDownloader getMusicDownloader();
	
	public String getAlbumName();
	
	public String getArtistName();
	
	public String getTrackName();
	
	public int getTrackNum();
	
}
