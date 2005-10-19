package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.URL;

import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;


/**
 * <p></p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class HTTPMusicDownloader implements IMusicDownloader {

    private URL url;
    private String trackName;
    private String albumName;
    private String artistName;
    private int trackNum;
    private HTTPMusicDownloadMonitor monitor;
    private File outputFile;
    private DownloadThread dlThread;
    private DLState state;

    public HTTPMusicDownloader(URL url, File outputFile,
            int trackNum, String songName, String album, String artist) {
        super();
        this.url = url;
        this.trackNum = trackNum;
        this.trackName = songName;
        this.albumName = album;
        this.artistName = artist;
        this.outputFile = outputFile;
        this.monitor = new HTTPMusicDownloadMonitor(this);
    }

    public IDownloadMonitor getMonitor() {
        return monitor;
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloader#start()
     */
    public void start() {
        if (dlThread == null) {
            dlThread = new DownloadThread();
            dlThread.start();
        } else {
            dlThread.pause(false);
        }
        setState(DLState.DOWNLOADING);       
    }

    private void setState(DLState state) {
        this.state = state;
        monitor.setState(state);
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloader#stop()
     */
    public void stop() {
        // TODO Auto-generated method stub
        
    }

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloader#pause()
	 */
	public void pause() {
		// TODO Auto-generated method stub
		
	}

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public URL getURL() {
        return url;
    }

    public int getTrackNum() {
        return trackNum;
    }

    public File getOutputFile() {
        return outputFile;
    }
    
    /**
     * <p></p>
     */
    public class DownloadThread extends Thread {

        public DownloadThread() {
            super();
        }

        public void run() {
            
        }
        
        public void pause(boolean pause) {
            
        }
        
        public void finish() {
            
        }

    }


}
