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

import nz.net.kallisti.emusicj.tagging.ITagData;

import org.w3c.dom.Element;

/**
 * <p>
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
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

	public void setDownloader(URL url, File outputFile, int trackNum,
			String songName, String album, String artist);

	public void setDownloader(URL url, File outputFile, File coverArt,
			int trackNum, String songName, String album, String artist);

	public void setDownloader(Element el) throws MalformedURLException;

	/**
	 * The music download tracks may expire. This is that date that this will
	 * happen to this one.
	 * 
	 * @param expiry
	 *            the expiry date. May be <code>null</code> if there is no
	 *            expiry for this track.
	 */
	public void setExpiry(Date expiry);

	/**
	 * Sets the tag data that will be saved to the audio file after it is saved.
	 * 
	 * @param tagData
	 *            the audio data
	 */
	public void setTag(ITagData tagData);

}
