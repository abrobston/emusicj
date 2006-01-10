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
    
    public void setGenre(String genre);
    
    public String getGenre();
    
    public void setDuration(int i);
    
    public int getDuration();
    
    public IMusicDownloadMonitor getMusicDownloadMonitor();
    
}
