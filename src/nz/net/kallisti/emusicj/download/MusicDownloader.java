package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * 
 * $Id: MusicDownloader.java 101 2006-01-10 11:07:33Z robin $
 *
 * @author robin
 */
public class MusicDownloader extends HTTPDownloader implements
		IMusicDownloader {
	
	private String trackName;
	private String albumName;
	private String artistName;
	private int trackNum;
	private File coverArt;
	private String genre;
	private int duration=-1;
	
	public MusicDownloader(URL url, File outputFile,
			int trackNum, String songName, String album, String artist) {
		super(url, outputFile);
		this.trackName = songName;
		this.albumName = album;
		this.artistName = artist;
		this.trackNum = trackNum;		
	}
	
	public MusicDownloader(URL url, File outputFile, File coverArt,
			int trackNum, String songName, String album, String artist) {
		super(url, outputFile);
		this.trackName = songName;
		this.albumName = album;
		this.artistName = artist;
		this.trackNum = trackNum;
		this.coverArt = coverArt;
	}
	
	public MusicDownloader(Element el) throws MalformedURLException {
		super(el);
		String tNum = el.getAttribute("tracknum");
		if (tNum != null && !tNum.equals(""))
			trackNum = Integer.parseInt(tNum);
		else
			trackNum = -1;
		albumName = el.getAttribute("albumname");
		artistName = el.getAttribute("artistname");
		trackName = el.getAttribute("trackname");
		genre = el.getAttribute("genre");
		tNum = el.getAttribute("duration");
		if (tNum != null && !tNum.equals(""))
			duration = Integer.parseInt(tNum);
		else
			duration = -1;
		String tCov = el.getAttribute("coverart");
		if (tCov != null) 
			coverArt = new File(tCov);
	}
	
	@Override
	protected void createMonitor() {
		monitor = new MusicDownloadMonitor(this);
	}
	
	public void saveTo(Element el, Document doc) {
		super.saveTo(el, doc);
		el.setAttribute("tracknum", trackNum+"");
		el.setAttribute("trackname", trackName);
		el.setAttribute("albumname", albumName);
		el.setAttribute("artistname", artistName);
		el.setAttribute("genre", genre);
		el.setAttribute("duration", duration+"");
		if (coverArt != null)
			el.setAttribute("coverart", coverArt.toString());
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
	
	public File getCoverArt() {
		return coverArt;
	}
	
	public IMusicDownloadMonitor getMusicDownloadMonitor() {
		return (IMusicDownloadMonitor)monitor;
	}
	
	public IDownloadMonitor getMonitor() {
		return monitor;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public void setDuration(int i) {
		this.duration = i;
	}

	public int getDuration() {
		return duration;
	}

	public String getGenre() {
		return genre;
	}
	
}
