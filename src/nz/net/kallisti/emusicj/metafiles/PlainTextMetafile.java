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
package nz.net.kallisti.emusicj.metafiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This is a parser for plaintext metafiles. These metafiles have the format:<br />
 * <tt>URL tracknum songname album artist</tt><br />
 * with any number of lines each containing this information. (note that this
 * format doesn't allow spaces in fields, it is primarily intended for testing)
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class PlainTextMetafile implements IMetafile {

	private File file;
	private final IPreferences prefs;
	private final Provider<IMusicDownloader> musicDownloaderProvider;

	@Inject
	public PlainTextMetafile(IPreferences prefs,
			Provider<IMusicDownloader> musicDownloaderProvider) {
		this.prefs = prefs;
		this.musicDownloaderProvider = musicDownloaderProvider;
	}

	public void setMetafile(File file) {
		this.file = file;
	}

	public List<IDownloader> getDownloaders() {
		ArrayList<IDownloader> downloaders = new ArrayList<IDownloader>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			return null;
		}
		boolean done = false;
		while (!done) {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException e1) {
				return null;
			}
			if (line == null || line.equals("")) {
				done = true;
				break;
			}
			String[] parts = line.split(" ");
			if (parts.length != 5) {
				return null;
			}
			// Check for the URL being valid
			URL url;
			try {
				url = new URL(parts[0]);
			} catch (MalformedURLException e) {
				return null;
			}
			// check the track number is a valid number
			int tnum;
			try {
				tnum = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				return null;
			}
			File outputFile = new File(prefs.getFilename(tnum, parts[2],
					parts[3], parts[4], ".mp3", null, null));
			IMusicDownloader dl = musicDownloaderProvider.get();
			dl.setDownloader(url, outputFile, tnum, parts[2], parts[3],
					parts[4]);
			downloaders.add(dl);
		}
		return downloaders;
	}

	@SuppressWarnings("unused")
	public static boolean canParse(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		boolean valid = true;
		boolean done = false;
		boolean atLeastOne = false;
		while (valid && !done) {
			String line = reader.readLine();
			if (line == null || line.equals("")) {
				return valid && atLeastOne;
			}
			String[] parts = line.split(" ");
			if (parts.length != 5) {
				valid = false;
				break;
			}
			// Check for the URL being valid
			try {
				URL url = new URL(parts[0]);
			} catch (MalformedURLException e) {
				valid = false;
				break;
			}
			// check the track number is a valid number
			try {
				int tnum = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				valid = false;
				break;
			}
			atLeastOne = true;
		}
		return valid;
	}

}
