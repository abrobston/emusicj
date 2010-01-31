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

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeTypes;
import nz.net.kallisti.emusicj.files.cleanup.ICleanupFiles;
import nz.net.kallisti.emusicj.network.failure.INetworkFailure;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagSerialiser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	ITagData id3;
	private final ITagSerialiser id3Serialiser;

	static IMimeType[] mimeTypes = { MimeTypes.AUDIO, MimeTypes.APP_OCTET,
			MimeTypes.PDF, MimeTypes.OGG, MimeTypes.CUE, MimeTypes.FLAC,
			MimeTypes.FLAC2 };

	@Inject
	public MusicDownloader(IPreferences prefs,
			IHttpClientProvider clientProvider, ICleanupFiles cleanupFiles,
			INetworkFailure networkFailure, ITagSerialiser id3Serialiser,
			IDownloadHooks dlHooks) {
		super(prefs, clientProvider, cleanupFiles, networkFailure, dlHooks);
		this.id3Serialiser = id3Serialiser;
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
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n.getNodeName().toLowerCase().equals("tag")
					&& n instanceof Element) {
				id3 = id3Serialiser.deserialise((Element) n);
			}
		}
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
		if (id3 != null) {
			Element tagEl = doc.createElement("tag");
			id3Serialiser.serialise(tagEl, doc, id3);
			el.appendChild(tagEl);
		}
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

	public void setTag(ITagData id3) {
		this.id3 = id3;
	}

	public ITagData getID3() {
		return this.id3;
	}

}
