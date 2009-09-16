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

import nz.net.kallisti.emusicj.misc.NumberUtils;

/**
 * 
 * 
 * $Id$
 * 
 * @author robin
 */
public class MusicDownloadMonitor extends HTTPDownloadMonitor implements
		IMusicDownloadMonitor {

	public MusicDownloadMonitor() {
		super();
	}

	public MusicDownloadMonitor(MusicDownloader downloader) {
		super(downloader);
	}

	@Override
	public String getName() {
		// This is a bit of a hack to make the artist name not displayed if
		// it shouldn't be.
		if ("Not used".equalsIgnoreCase(getMusicDownloader().getArtistName())) {
			return getMusicDownloader().getTrackName() + " - "
					+ getMusicDownloader().getAlbumName();
		}
		return getMusicDownloader().getTrackName() + " - "
				+ getMusicDownloader().getAlbumName() + " - "
				+ getMusicDownloader().getArtistName();
	}

	public IMusicDownloader getMusicDownloader() {
		return (IMusicDownloader) getDownloader();
	}

	public String getAlbumName() {
		return getMusicDownloader().getAlbumName();
	}

	public String getArtistName() {
		return getMusicDownloader().getArtistName();
	}

	public String getTrackName() {
		return getMusicDownloader().getTrackName();
	}

	public int getTrackNum() {
		return getMusicDownloader().getTrackNum();
	}

	public File getImageFile() {
		return getMusicDownloader().getCoverArt();
	}

	// public File getCoverArt() {
	// return getImageFile();
	// }

	public String[][] getText() {
		String[][] res = new String[5][2];
		int i = 0;
		res[i][0] = "Title";
		res[i++][1] = getTrackName();
		res[i][0] = "Album";
		res[i++][1] = getAlbumName();
		if (!"Not used".equals(getArtistName())) {
			res[i][0] = "Artist";
			res[i++][1] = getArtistName();
		}
		String genre = ((IMusicDownloader) downloader).getGenre();
		if (genre != null)
			res[i][0] = "Genre";
		res[i++][1] = genre;
		int dur = ((IMusicDownloader) downloader).getDuration();
		if (dur != -1) {
			res[i][0] = "Duration";
			res[i++][1] = NumberUtils.formatSeconds(dur);
		}
		return res;
	}

}
