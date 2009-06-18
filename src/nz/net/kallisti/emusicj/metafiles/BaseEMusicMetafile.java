/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005-2007 Robin Sheat

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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Loads a file in the format of an eMusic.com file. The source of the file is
 * provided by the subclass. This is because the Naxos format is identical but
 * without the encryption.
 * </p>
 * 
 * <p>
 * $Id: EMPMetafile.java 147 2006-12-28 07:21:53Z robin $
 * </p>
 * 
 * @author Robin Sheat <robin@kallisti.net.nz>
 * @author Paul Focke <paul.focke@gmail.com>
 */
public abstract class BaseEMusicMetafile implements IMetafile {

	List<IDownloader> downloaders = new ArrayList<IDownloader>();
	private static Hashtable<String, File> coverArtCache;
	private EMPServer server;
	private final IPreferences prefs;
	private final Provider<IMusicDownloader> musicDownloaderProvider;
	private final Provider<ICoverDownloader> coverDownloaderProvider;
	private final IStrings strings;
	private final IImageFactory images;
	private final Logger logger;

	@Inject
	public BaseEMusicMetafile(IPreferences prefs, IStrings strings,
			Provider<IMusicDownloader> musicDownloaderProvider,
			Provider<ICoverDownloader> coverDownloaderProvider,
			IImageFactory images) {
		this.prefs = prefs;
		this.strings = strings;
		this.musicDownloaderProvider = musicDownloaderProvider;
		this.coverDownloaderProvider = coverDownloaderProvider;
		// This is needed so we can update the logo if the .col file tells us
		// it's changed. This is annoyingly tightly coupled to the SWT view
		// implementation, and so the design may need to be revisited in the
		// future.
		this.images = images;
		logger = LogUtils.getLogger(this);
	}

	public void setMetafile(File file) throws IOException {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new UnknownFileException(e);
		}
		Document doc;
		try {
			doc = builder.parse(getFileStream(file));
		} catch (SAXException e) {
			throw new UnknownFileException("I wasn't able to load this file. "
					+ "Perhaps try downloading it again", e);
		}
		Node root = doc.getDocumentElement();
		if (!(root.getNodeType() == Node.ELEMENT_NODE && root.getNodeName()
				.equalsIgnoreCase("package"))) {
			throw new UnknownFileException("Unknown file type");
		}
		if (!root.hasChildNodes()) {
			throw new UnknownFileException(
					"File appears to contain no downloads");
		}
		NodeList pkg = root.getChildNodes();
		// The expiry date of the track URLs.
		Date expiry = null;
		for (int count = 0; count < pkg.getLength(); count++) {
			Node node = pkg.item(count);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (node.getNodeName().equalsIgnoreCase("tracklist")) {
				loadTrackList(node, expiry);
			} else if (node.getNodeName().equalsIgnoreCase("server")) {
				server = new EMPServer(node);
			} else if (node.getNodeName().equalsIgnoreCase("logo")) {
				setLogo(node);
			} else if (node.getNodeName().equalsIgnoreCase("exp_date")) {
				expiry = parseDate(node);
			}
		}

	}

	/**
	 * This sets the logo to the URL provided in the .col file.
	 * 
	 * @param node
	 *            the node containing the URL
	 */
	private void setLogo(Node node) {
		String url = node.getTextContent();
		IDynamicImageProvider logoProvider = images
				.getApplicationLogoProvider();
		if (logoProvider instanceof IURLDynamicImageProvider) {
			try {
				((IURLDynamicImageProvider) logoProvider)
						.changeURL(new URL(url));
			} catch (MalformedURLException e) {
				logger.warning("Invalid logo URL provided in metafile: [" + url
						+ "]");
			}
		} else {
			logger
					.warning("Logo change requested on non-URL-based dynamic image (logoProvider.getClass()="
							+ logoProvider.getClass());
		}
	}

	/**
	 * This processes the date that is contained in the node text, and turns it
	 * into a real date.
	 * 
	 * @param node
	 *            the node that contains the date
	 * @return the parsed date, or <code>null</code> if it can't be parsed.
	 */
	private Date parseDate(Node node) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		ParsePosition pos = new ParsePosition(0);
		String data = node.getTextContent();
		if (data == null)
			return null;
		Date date = df.parse(node.getTextContent(), pos);
		if (date == null) {
			logger.warning("Unable to parse date (" + data + "), error in pos "
					+ pos.getErrorIndex());
			return null;
		}
		return date;
	}

	/**
	 * This takes the filename that was provided and turns it into a stream that
	 * will provide the raw XML.
	 * 
	 * @param file
	 *            the filename to process
	 * @param expiry
	 *            the date that the file tells us this track will expire, or
	 *            <code>null</code> if there isn't an expiry date.
	 * @return an input stream providing the EMP file content
	 */
	protected abstract InputStream getFileStream(File file) throws IOException;

	private void loadTrackList(Node tracklistNode, Date expiry) {
		NodeList tracklist = tracklistNode.getChildNodes();
		for (int count = 0; count < tracklist.getLength(); count++) {
			Node node = tracklist.item(count);
			if (!(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName()
					.equalsIgnoreCase("track"))) {
				continue;
			}
			loadTrack(node, expiry);
		}
	}

	/**
	 * @param node
	 * @throws MalformedURLException
	 */
	private void loadTrack(Node trackNode, Date expiry) {
		NodeList track = trackNode.getChildNodes();
		String id = null;
		String num = null;
		String title = null;
		String album = null;
		String artist = null;
		String filename = null;
		String format = null;
		String coverArt = null;
		String genre = null;
		String duration = null;
		for (int count = 0; count < track.getLength(); count++) {
			Node node = track.item(count);
			if (node.getFirstChild() == null)
				continue;
			if (node.getNodeName().equalsIgnoreCase("trackid"))
				id = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("tracknum"))
				num = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("title"))
				title = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("album"))
				album = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("artist"))
				artist = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("filename"))
				filename = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("format"))
				format = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("albumart"))
				coverArt = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("genre"))
				genre = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("duration"))
				duration = node.getFirstChild().getNodeValue();
		}
		URL url;
		try {
			url = server.createDownloadURL(id, filename);
		} catch (MalformedURLException e) {
			throw new UnknownFileException(e);
		}
		int trackNum = Integer.parseInt(num);
		File outputFile = new File(prefs.getFilename(trackNum, title, album,
				artist, format));
		File coverArtFile = null;
		if (coverArt != null)
			coverArtFile = getCoverArtCached(coverArt, prefs, trackNum, title,
					album, artist);
		IMusicDownloader dl = musicDownloaderProvider.get();
		dl.setDownloader(url, outputFile, coverArtFile, trackNum, title, album,
				artist);
		downloaders.add(dl);
		dl.setGenre(genre);
		if (expiry != null)
			dl.setExpiry(expiry);
		try {
			dl.setDuration(Integer.parseInt(duration));
		} catch (NumberFormatException e) {
			// do nothing
		}
	}

	/**
	 * This is passed a String URL of where to find the coverart for a track. It
	 * turns it into a filename. If the file doesn't exist, and we haven't
	 * already created a downloader for it, then a download is added to the
	 * list.
	 * 
	 * @param coverArt
	 *            the string form of the URL to load
	 * @param artist
	 *            the artist this cover is for
	 * @param album
	 *            the album it's from
	 * @param title
	 *            the title of the track
	 * @param trackNum
	 *            the number of the track
	 * @return a file corresponding the coverart. It may not exist yet, but that
	 *         is where it eventually will be. If we aren't going to be
	 *         downloading the cover art, this will return <code>null</code>.
	 */
	private File getCoverArtCached(final String coverArt, IPreferences prefs,
			int trackNum, String title, String album, String artist) {
		if (!prefs.downloadCoverArt())
			return null;
		if (coverArtCache == null)
			coverArtCache = new Hashtable<String, File>();
		File cachedFile = coverArtCache.get(coverArt);
		if (cachedFile != null)
			return cachedFile;
		URL coverUrl;
		try {
			coverUrl = new URL(coverArt);
		} catch (MalformedURLException e) {
			return null;
		}
		File coverFile;
		File savePath = prefs.getPathFor(trackNum, title, album, artist);
		int dotPos = coverArt.lastIndexOf(".");
		if (dotPos != -1) {
			String filetype = coverArt.substring(dotPos);
			if (filetype.equalsIgnoreCase(".jpeg"))
				filetype = ".jpg"; // who the hell uses ".jpeg" as an extension
			// anyway?
			coverFile = new File(savePath, strings.getCoverArtName() + filetype);
		} else {
			return null;
		}
		if (!coverFile.exists()) {
			coverArtCache.put(coverArt, coverFile);
			// add the downloader
			ICoverDownloader dl = coverDownloaderProvider.get();
			// Create a monitor that will remove this entry from the cache when
			// the download has finished
			IDownloadMonitor mon = dl.getMonitor();
			mon.addStateListener(new IDownloadMonitorListener() {
				// This is a bt icky, but it'll do the job in 99% of cases.
				public void monitorStateChanged(IDownloadMonitor monitor) {
					if (monitor.getDownloadState() == DLState.CANCELLED
							|| monitor.getDownloadState() == DLState.FINISHED) {
						coverArtCache.remove(coverArt);
					}
				}
			});
			dl.setDownloader(coverUrl, coverFile);
			downloaders.add(dl);
		}
		return coverFile;
	}

	public List<IDownloader> getDownloaders() {
		return downloaders;
	}

	private static class EMPServer {

		private final String protocol = "http";
		private final String name;
		private final String desc;
		private final String server;
		private final String location;
		private final String key;

		public EMPServer(String name, String desc, String server,
				String location, String key) {
			this.name = name;
			this.desc = desc;
			this.server = server;
			this.location = location;
			this.key = key;
		}

		public EMPServer(Node serverNode) {
			String nname = "";
			String ndesc = "";
			String nserver = "";
			String nlocation = "";
			String nkey = "";
			NodeList nodeList = serverNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getFirstChild() == null)
					continue;
				if (node.getNodeName().equalsIgnoreCase("name"))
					nname = node.getFirstChild().getNodeValue();
				else if (node.getNodeName().equalsIgnoreCase("desc"))
					ndesc = node.getFirstChild().getNodeValue();
				else if (node.getNodeName().equalsIgnoreCase("netname"))
					nserver = node.getFirstChild().getNodeValue();
				else if (node.getNodeName().equalsIgnoreCase("location"))
					nlocation = node.getFirstChild().getNodeValue();
				else if (node.getNodeName().equalsIgnoreCase("key"))
					nkey = node.getFirstChild().getNodeValue();
			}

			name = nname;
			desc = ndesc;
			server = nserver;
			location = nlocation;
			key = nkey;
		}

		public URL createDownloadURL(String id, String filename)
				throws MalformedURLException {
			String filelocation = new String(location);
			filelocation = filelocation.replaceAll("%fid", id);
			filelocation = filelocation.replaceAll("%f", filename);
			return new URL(protocol, server, filelocation);
		}

		/**
		 * @return Returns the desc.
		 */
		public String getDesc() {
			return desc;
		}

		/**
		 * @return Returns the key.
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @return Returns the name.
		 */
		public String getName() {
			return name;
		}
	}
}
