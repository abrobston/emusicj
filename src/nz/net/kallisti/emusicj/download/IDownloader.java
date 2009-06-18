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

import org.w3c.dom.Element;

/**
 * <p>
 * The interface for classes that download files
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public interface IDownloader {

	public IDownloadMonitor getMonitor();

	public void start();

	/**
	 * This tells the downloader to shut down any downloads. This should stop
	 * any threads running.
	 */
	public void stop();

	/**
	 * This tells the downloads to shut down any downloads as fast as possible.
	 * To be used when the program is shutting down.
	 */
	public void hardStop();

	/**
	 * This tells the downloader to pause any downloads. This may or may not
	 * stop threads.
	 */
	public void pause();

	public void requeue();

	public URL getURL();

	public File getOutputFile();

	public int getFailureCount();

	public void resetFailureCount();

	/**
	 * Initialise, but do not start, the downloader
	 * 
	 * @param url
	 *            the URL to download
	 * @param outputFile
	 *            the file to save the output to
	 * @param mimeType
	 *            the MIME type to restrict the downloading to. Anything else
	 *            will be considered an error.
	 */
	public void setDownloader(URL url, File outputFile, IMimeType[] mimeType);

	/**
	 * Loads the downloader state from the provided element
	 * 
	 * @param el
	 *            the element to load from
	 * @throws MalformedURLException
	 *             if the URL in the XML is wrong or missing
	 */
	public void setDownloader(Element el) throws MalformedURLException;

	/**
	 * This will check to see if the URL information has expired. It is publicly
	 * visible as it may be useful to poll this every so often to keep it
	 * reasonably up to date.
	 * 
	 * @return true if the URL has expired, false otherwise
	 */
	public boolean hasExpired();

}
