package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public class HTTPMusicDownloader extends HTTPDownloader implements
		IMusicDownloader {
	
	private String trackName;
	private String albumName;
	private String artistName;
	private int trackNum;
	
	public HTTPMusicDownloader(URL url, File outputFile,
			int trackNum, String songName, String album, String artist) {
		super(url, outputFile);
		this.trackName = songName;
		this.albumName = album;
		this.artistName = artist;
		this.trackNum = trackNum;
		
	}
	
	public HTTPMusicDownloader(Element el) throws MalformedURLException {
		super(el);
		String tNum = el.getAttribute("tracknum");
		if (tNum != null)
			trackNum = Integer.parseInt(tNum);
		else
			trackNum = -1;
		albumName = el.getAttribute("albumname");
		artistName = el.getAttribute("artistname");
		trackName = el.getAttribute("trackname");
	}
	
	@Override
	protected void createMonitor() {
		monitor = new HTTPMusicDownloadMonitor(this);
	}
	
	public void saveTo(Element el, Document doc) {
		el.setAttribute("tracknum", trackNum+"");
		el.setAttribute("trackname", trackName);
		el.setAttribute("albumname", albumName);
		el.setAttribute("artistname", artistName);
		super.saveTo(el, doc);
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
	
	public int getTrackNum() {
		return trackNum;
	}
	
	public IMusicDownloadMonitor getMusicDownloadMonitor() {
		return (IMusicDownloadMonitor)monitor;
	}
	
	public IDownloadMonitor getMonitor() {
		return monitor;
	}
	
}
