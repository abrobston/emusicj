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

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeTypes;
import nz.net.kallisti.emusicj.files.cleanup.ICleanupFiles;
import nz.net.kallisti.emusicj.network.failure.INetworkFailure;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;

import org.w3c.dom.Element;

import com.google.inject.Inject;

/**
 * 
 * 
 * $Id$
 * 
 * @author robin
 */
public class CoverDownloader extends HTTPDownloader implements ICoverDownloader {

	@Inject
	public CoverDownloader(IPreferences prefs,
			IHttpClientProvider clientProvider, ICleanupFiles cleanupFiles,
			INetworkFailure networkFailure) {
		super(prefs, clientProvider, cleanupFiles, networkFailure);
	}

	/**
	 * @param url
	 * @param outputFile
	 */
	public void setDownloader(URL url, File outputFile) {
		super.setDownloader(url, outputFile,
				new IMimeType[] { MimeTypes.IMAGES });
	}

	/**
	 * @param el
	 * @throws MalformedURLException
	 */
	@Override
	public void setDownloader(Element el) throws MalformedURLException {
		super.setDownloader(el);
	}

	@Override
	protected void createMonitor() {
		monitor = new CoverDownloadMonitor(this);
	}

}
