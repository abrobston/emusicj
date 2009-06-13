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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.view.images.IImageFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Loads a Naxos metafile, and creates downloaders from it. The Naxos format is
 * identical to the eMusic file, without the layer of encryption.
 * </p>
 * 
 * <p>
 * $Id: EMPMetafile.java 147 2006-12-28 07:21:53Z robin $
 * </p>
 * 
 * @author Robin Sheat <robin@kallisti.net.nz>
 * @author Paul Focke <paul.focke@gmail.com>
 */
public class NaxosMetafile extends BaseEMusicMetafile {

	/**
	 * @param prefs
	 * @param musicDownloaderProvider
	 * @param coverDownloaderProvider
	 */
	@Inject
	public NaxosMetafile(IPreferences prefs, IStrings strings,
			Provider<IMusicDownloader> musicDownloaderProvider,
			Provider<ICoverDownloader> coverDownloaderProvider,
			IImageFactory images) {
		super(prefs, strings, musicDownloaderProvider, coverDownloaderProvider,
				images);
	}

	@Override
	protected InputStream getFileStream(File file) throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * Does a simple test to see if the file is one we recognise.
	 * 
	 * @param file
	 *            the file to test
	 * @return true if the file is file looks like an EMP file
	 * @throws IOException
	 *             if the file can't be read
	 */
	public static boolean canParse(File file) throws IOException {
		if (!file.getName().endsWith(".col")
				&& !file.getName().endsWith(".COL"))
			return false;
		FileInputStream stream = new FileInputStream(file);
		// just look at the first Kb
		byte[] buff = new byte[1024];
		// ignore return value, we don't really care
		stream.read(buff);
		String s = new String(buff);
		stream.close();
		return s.indexOf("<PACKAGE>") != -1;
	}

}
