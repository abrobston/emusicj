package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 * <p></p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public interface IMusicDownloader extends IDownloader {

    public int getTrackNum();
    
    public String getTrackName();
    
    public String getArtistName();
    
    public String getAlbumName();
    
    public File getCoverArt();
    
    public IMusicDownloadMonitor getMusicDownloadMonitor();
    
}
