package nz.net.kallisti.emusicj.download;

import java.net.URL;

import nz.net.kallisti.emusicj.controller.IEMusicController;


/**
 * <p></p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class HTTPMusicDownloader implements IMusicDownloader {

    public HTTPMusicDownloader(IEMusicController controller, URL url, 
            int trackNum, String songName, String album, String artist) {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloader#getMonitor()
     */
    public IDownloadMonitor getMonitor() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloader#start()
     */
    public void start() {
        // TODO Auto-generated method stub
        
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

}
