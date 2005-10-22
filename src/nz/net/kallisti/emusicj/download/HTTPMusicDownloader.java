package nz.net.kallisti.emusicj.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import nz.net.kallisti.emusicj.controller.Preferences;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;


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
    private volatile DLState state;
    volatile long fileLength = -1;
    volatile long bytesDown = 0;

    public HTTPMusicDownloader(URL url,
            int trackNum, String songName, String album, String artist) {
        super();
        this.url = url;
        this.trackNum = trackNum;
        this.trackName = songName;
        this.albumName = album;
        this.artistName = artist;
        this.monitor = new HTTPMusicDownloadMonitor(this);
        state = DLState.NOTSTARTED;
        monitor.setState(state);
    }

    public IDownloadMonitor getMonitor() {
        return monitor;
    }

    public void start() {
    		Preferences prefs = Preferences.getInstance();
    		outputFile = new File(prefs.getFilename(trackNum, trackName, 
    				albumName, artistName));
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

    public void stop() {
        if (dlThread != null)
            dlThread.finish();
        state = DLState.STOPPED;
        monitor.setState(state);
    }

	public void pause() {
        if (dlThread != null)
            dlThread.pause(true);
		state = DLState.PAUSED;
		monitor.setState(state);
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

    /**
     * Returns the file that the download will be saved to.
     * @return the file, or null if the download hasn't started yet.
     */
    public File getOutputFile() {
        return outputFile;
    }
    
    private void downloadError(Exception e) {
        e.printStackTrace();
		state = DLState.FAILED;
		monitor.setState(state);    	
    }
    
    private void downloadError(String s) {
        System.err.println(s);
		state = DLState.FAILED;
		monitor.setState(state);	
    }
    
    /**
     * <p>This class does the actual downloading of the file</p>
     */
    public class DownloadThread extends Thread {

        private boolean pause = false;
		private boolean abort = false;

		public DownloadThread() {
            super();
        }

        public void run() {
            BufferedOutputStream out = null;
            try {
                File parent = outputFile.getParentFile();
                if (parent != null)
                    parent.mkdirs();
            		// TODO check for existing file and resume                
				out = new BufferedOutputStream(new FileOutputStream(outputFile));
			} catch (FileNotFoundException e) {
				downloadError(e);
				return;
			}
			while (pause && !abort);
            if (abort) return;
			HttpClient http = new HttpClient();
			HttpMethod get = new GetMethod(url.toString());
			InputStream in;
			try {
				int statusCode = http.executeMethod(get);
				if (statusCode != HttpStatus.SC_OK) {
					get.releaseConnection();
					downloadError("Download failed: server returned code "+statusCode);
					return;
				}
                Header[] responseHeaders = get.getResponseHeaders();                
                for (int i=0; i<responseHeaders.length; i++){
                    String hLine = responseHeaders[i].toString();
                    String[] hParts = hLine.split(" ");
                    if (hParts[0].equals("Content-Length:")) {
                        fileLength = Long.parseLong(hParts[1].
                                substring(0,hParts[1].length()-2));
                    }
                }
				in = get.getResponseBodyAsStream();
			} catch (IOException e) {
				get.releaseConnection();
				downloadError(e);
				return;
			}
            while (pause && !abort);
			if (abort) {
				try { out.close(); } catch (IOException e) {}
				get.releaseConnection();
				return;
			}
			byte[] buff = new byte[8192]; // we'll work in 8K chunks

			int count;
			try {
				while ((count = in.read(buff)) != -1) {
                    synchronized (HTTPMusicDownloader.this) {
                        // synch because an assignment to a long isn't atomic
                        bytesDown += count;
                    }
					out.write(buff, 0, count);
                    while (pause && !abort);
					if (abort) {
						try { out.close(); } catch (IOException e) {}
						get.releaseConnection();
						return;
					}
				}
				setState(DLState.FINISHED);
				out.close();
				in.close();
				get.releaseConnection();
			} catch (IOException e) {
				try {
					out.close();
					in.close();
				} catch (Exception ex) {}
				get.releaseConnection();
				downloadError(e);
				return;
			}
        }
        
        public void pause(boolean pause) {
            this.pause = pause;
        }
        
        public void finish() {
            this.abort  = true;
        }

    }


}
