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

import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeTypes;

import org.w3c.dom.Element;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public class CoverDownloader extends HTTPDownloader {

	/**
	 * @param url
	 * @param outputFile
	 */
	public CoverDownloader(URL url, File outputFile) {
		super(url, outputFile, new IMimeType[] {MimeTypes.IMAGES});
	}

	/**
	 * @param el
	 * @throws MalformedURLException
	 */
	public CoverDownloader(Element el) throws MalformedURLException {
		super(el);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void createMonitor() {
        monitor = new CoverDownloadMonitor(this);
    }
    
}
