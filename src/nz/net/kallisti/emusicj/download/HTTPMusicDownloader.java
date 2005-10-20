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
    private DLState state;
    long fileLength = -1;
    long bytesDown = 0;

    public HTTPMusicDownloader(URL url,
            int trackNum, String songName, String album, String artist) {
        super();
        this.url = url;
        this.trackNum = trackNum;
        this.trackName = songName;
        this.albumName = album;
        this.artistName = artist;
        this.monitor = new HTTPMusicDownloadMonitor(this);
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
    		dlThread.finish();
    		state = DLState.STOPPED;
    		monitor.setState(state);
    }

	public void pause() {
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
		state = DLState.FAILED;
		monitor.setState(state);    	
    }
    
    private void downloadError(String s) {
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
            		// TODO check for existing file and resume
				out = new BufferedOutputStream(new FileOutputStream(outputFile));
			} catch (FileNotFoundException e) {
				downloadError(e);
				return;
			}
			if (abort) return;
			while (!pause);
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
                Header[] requestHeaders = get.getRequestHeaders();                
                for (int i=0; i<requestHeaders.length; i++){
                    System.err.print(requestHeaders[i]);
                    String hLine = requestHeaders[i].toString();
                    String[] hParts = hLine.split(" ");
                    if (hParts[0].equals("Length:")) {
                        fileLength = Long.parseLong(hParts[1]);
                    }
                }
				in = get.getResponseBodyAsStream();
			} catch (IOException e) {
				get.releaseConnection();
				downloadError(e);
				return;
			}
			if (abort) {
				try { out.close(); } catch (IOException e) {}
				get.releaseConnection();
				return;
			}
			while (!pause);
			byte[] buff = new byte[8192]; // we'll work in 8K chunks

			int count;
			try {
				while ((count = in.read(buff)) != -1) {
                    bytesDown += count;
					out.write(buff, 0, count);
					if (abort) {
						try { out.close(); } catch (IOException e) {}
						get.releaseConnection();
						return;
					}
					while (!pause);
				}
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
