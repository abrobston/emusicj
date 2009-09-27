/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */
package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeTypes;
import nz.net.kallisti.emusicj.files.cleanup.ICleanupFiles;
import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3ToMP3;
import nz.net.kallisti.emusicj.network.failure.INetworkFailure;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;

/**
 * 
 * 
 * $Id$
 * 
 * @author robin
 */
public class MusicDownloader extends HTTPDownloader implements IMusicDownloader {

	String trackName;
	String albumName;
	String artistName;
	int trackNum;
	File coverArt;
	String genre;
	int duration = -1;
	IID3Data id3;
	private final IID3ToMP3 id3ToMP3;
	private final IEmusicjView view;

	static IMimeType[] mimeTypes = { MimeTypes.AUDIO, MimeTypes.APP_OCTET,
			MimeTypes.PDF, MimeTypes.OGG, MimeTypes.CUE };

	@Inject
	public MusicDownloader(IPreferences prefs,
			IHttpClientProvider clientProvider, ICleanupFiles cleanupFiles,
			INetworkFailure networkFailure, IID3ToMP3 id3ToMP3,
			IEmusicjView view) {
		super(prefs, clientProvider, cleanupFiles, networkFailure);
		this.id3ToMP3 = id3ToMP3;
		this.view = view;
	}

	public void setDownloader(URL url, File outputFile, int trackNum,
			String songName, String album, String artist) {
		super.setDownloader(url, outputFile, mimeTypes);
		this.trackName = songName;
		this.albumName = album;
		this.artistName = artist;
		this.trackNum = trackNum;
	}

	public void setDownloader(URL url, File outputFile, File coverArt,
			int trackNum, String songName, String album, String artist) {
		super.setDownloader(url, outputFile, mimeTypes);
		this.trackName = songName;
		this.albumName = album;
		this.artistName = artist;
		this.trackNum = trackNum;
		this.coverArt = coverArt;
	}

	@Override
	public void setDownloader(Element el) throws MalformedURLException {
		super.setDownloader(el);
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

	@Override
	public void saveTo(Element el, Document doc, boolean ignorePause) {
		super.saveTo(el, doc, ignorePause);
		el.setAttribute("tracknum", trackNum + "");
		el.setAttribute("trackname", trackName);
		el.setAttribute("albumname", albumName);
		el.setAttribute("artistname", artistName);
		el.setAttribute("genre", genre);
		el.setAttribute("duration", duration + "");
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
		return (IMusicDownloadMonitor) monitor;
	}

	@Override
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

	@Override
	public void setExpiry(Date expiry) {
		super.setExpiry(expiry);
	}

	public void setID3(IID3Data id3) {
		this.id3 = id3;
	}

	@Override
	protected void downloadCompleted(File file) {
		super.downloadCompleted(file);
		if (id3 != null && file.getName().toLowerCase().endsWith(".mp3")) {
			try {
				id3ToMP3.writeMP3(id3, file);
			} catch (Exception e) {
				logger.log(Level.SEVERE,
						"An error occurred saving the MP3 tag data", e);
				view.error("Error writing file descriptions",
						"An error occurred saving ID3 information to the downloaded file:\n"
								+ e.getMessage());
			}
		}
	}

}
