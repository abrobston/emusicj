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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.general.IGeneralTagFromXML;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;

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
public abstract class BaseEMusicMetafile extends AbstractMetafile {

	List<IDownloader> downloaders = new ArrayList<IDownloader>();
	private EMPServer server;
	private final IPreferences prefs;
	private final Provider<IMusicDownloader> musicDownloaderProvider;
	private final Logger logger;
	private final IGeneralTagFromXML tagMaker;

	@Inject
	public BaseEMusicMetafile(IPreferences prefs, IStrings strings,
			Provider<IMusicDownloader> musicDownloaderProvider,
			Provider<ICoverDownloader> coverDownloaderProvider,
			IImageFactory images, IURLFactory urls, IGeneralTagFromXML tagMaker) {
		super(images, urls, strings, coverDownloaderProvider);
		this.prefs = prefs;
		this.musicDownloaderProvider = musicDownloaderProvider;
		this.tagMaker = tagMaker;
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
				expiry = parseDate(node.getTextContent());
			} else if (node.getNodeName().equalsIgnoreCase("banner")) {
				setBanner(node);
			}
		}

	}

	/**
	 * This sets the logo to the URL provided in the metafile.
	 * 
	 * @param node
	 *            the node containing the URL
	 */
	private void setLogo(Node node) {
		String urlStr = node.getTextContent();
		try {
			URL url = new URL(urlStr);
			setLogo(url);
		} catch (MalformedURLException e) {
			logger.warning("Invalid logo URL provided in metafile: [" + urlStr
					+ "]");
		}
	}

	/**
	 * This sets the banner from the node provided. Banner XML looks like:
	 * &lt;banner
	 * href="http://example.com/clickdest"&gt;http://example.com/image
	 * .png&lt;/banner&gt;
	 * 
	 * @param node
	 *            the node containing the banner info
	 */
	private void setBanner(Node node) {
		String clickUrlStr = node.getAttributes().getNamedItem("href")
				.getNodeValue();
		String imgUrlStr = node.getTextContent();
		URL clickUrl = null;
		try {
			if (clickUrlStr != null && !"".equals(clickUrlStr))
				clickUrl = new URL(clickUrlStr);
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "href URL in banner node not valid: "
					+ clickUrlStr);
		}
		try {
			URL imgUrl = new URL(imgUrlStr);
			setBanner(imgUrl, clickUrl);
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING,
					"image source URL in banner node not valid: " + imgUrlStr);
		}
	}

	/**
	 * This takes the filename that was provided and turns it into a stream that
	 * will provide the raw XML.
	 * 
	 * @param file
	 *            the filename to process
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
		// The disk number this track belongs on
		String diskStr = null;
		// The total count of disks for this album
		String diskCountStr = null;
		Node tagNode = null;
		for (int count = 0; count < track.getLength(); count++) {
			Node node = track.item(count);
			String nodeName = node.getNodeName();
			if (node.getFirstChild() == null || nodeName == null)
				continue;
			nodeName = nodeName.toLowerCase();
			if (nodeName.equals("trackid"))
				id = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("tracknum"))
				num = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("title"))
				title = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("album"))
				album = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("artist"))
				artist = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("filename"))
				filename = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("format"))
				format = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("albumart"))
				coverArt = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("genre"))
				genre = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("duration"))
				duration = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("disk"))
				diskStr = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("disktotal"))
				diskCountStr = node.getFirstChild().getNodeValue();
			else if (nodeName.equals("tags"))
				tagNode = node;
		}
		URL url;
		try {
			url = server.createDownloadURL(id, filename);
		} catch (MalformedURLException e) {
			throw new UnknownFileException(e);
		}
		int trackNum = Integer.parseInt(num);
		Integer disk = null;
		Integer diskCount = null;
		if (diskStr != null && diskCountStr != null) {
			try {
				disk = Integer.parseInt(diskStr);
				diskCount = Integer.parseInt(diskCountStr);
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING,
						"Unable to parse disk or disk count information", e);
			}
		}
		File outputFile = new File(prefs.getFilename(trackNum, title, album,
				artist, format, disk, diskCount));
		File coverArtFile = null;
		if (coverArt != null)
			coverArtFile = getCoverArtCached(downloaders, coverArt, prefs,
					trackNum, title, album, artist, disk, diskCount);
		IMusicDownloader dl = musicDownloaderProvider.get();
		dl.setDownloader(url, outputFile, coverArtFile, trackNum, title, album,
				artist);
		downloaders.add(dl);
		dl.setGenre(genre);
		if (tagNode != null) {
			ITagData tagData = tagMaker.getData(tagNode, format);
			dl.setTag(tagData);
		}
		if (expiry != null)
			dl.setExpiry(expiry);
		try {
			dl.setDuration(Integer.parseInt(duration));
		} catch (NumberFormatException e) {
			// do nothing
		}
	}

	public List<IDownloader> getDownloaders() {
		return downloaders;
	}

	private static class EMPServer {

		private final String protocol = "http";
		private final String server;
		private final String location;

		public EMPServer(Node serverNode) {
			String nserver = "";
			String nlocation = "";
			NodeList nodeList = serverNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getFirstChild() == null)
					continue;
				if (node.getNodeName().equalsIgnoreCase("netname"))
					nserver = node.getFirstChild().getNodeValue();
				else if (node.getNodeName().equalsIgnoreCase("location"))
					nlocation = node.getFirstChild().getNodeValue();
			}
			server = nserver;
			location = nlocation;
		}

		public URL createDownloadURL(String id, String filename)
				throws MalformedURLException {
			String filelocation = new String(location);
			filelocation = filelocation.replaceAll("%fid", id);
			filelocation = filelocation.replaceAll("%f", filename);
			return new URL(protocol, server, filelocation);
		}

	}
}
