package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public interface IMusicDownloadMonitor extends IDownloadMonitor {

	public IMusicDownloader getMusicDownloader();
	
	public String getAlbumName();
	
	public String getArtistName();
	
	public String getTrackName();
	
	public int getTrackNum();
	
	public File getCoverArt();
	
}
