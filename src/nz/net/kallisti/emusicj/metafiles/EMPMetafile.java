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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nz.net.kallisti.emusicj.controller.Preferences;
import nz.net.kallisti.emusicj.download.CoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;
import nz.net.kallisti.emusicj.metafiles.streams.EMPDecoderStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>Loads a .emp file, and creates downloaders from it.</p>
 * 
 * <p>$Id: EMPMetafile.java 129 2006-06-21 13:06:54Z robin $</p>
 *
 * @author robin
 */
public class EMPMetafile implements IMetafile {

	List<IDownloader> downloaders = new ArrayList<IDownloader>();
	private static Hashtable<String, File> coverArtCache;
	
	public EMPMetafile(File file) throws IOException {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new UnknownFileException(e);
		}
		Document doc;
		try {
			doc = builder.parse(new EMPDecoderStream(new FileInputStream(file)));
		} catch (SAXException e) {
			throw new UnknownFileException("I wasn't able to load this file. " +
					"Perhaps try downloading it again", e);
		}
		Node root = doc.getDocumentElement();
		if (!(root.getNodeType() == Node.ELEMENT_NODE &&
				root.getNodeName().equalsIgnoreCase("package"))) {
			throw new UnknownFileException("Unknown file type");
		}
		if (!root.hasChildNodes()) {
			throw new UnknownFileException("File appears to contain no downloads");
		}
		NodeList pkg = root.getChildNodes(); 
		for (int count = 0; count < pkg.getLength(); count++) {
			Node node = pkg.item(count);
			if (!(node.getNodeType() == Node.ELEMENT_NODE &&
					node.getNodeName().equalsIgnoreCase("tracklist"))) {
				continue;
			}
			loadTrackList(node);
		}

	}
	
	private void loadTrackList(Node tracklistNode) {
		NodeList tracklist = tracklistNode.getChildNodes();
		for (int count = 0; count < tracklist.getLength(); count++) {
			Node node = tracklist.item(count);
			if (!(node.getNodeType() == Node.ELEMENT_NODE &&
					node.getNodeName().equalsIgnoreCase("track"))) {
				continue;
			}
			loadTrack(node);
		}		
	}

	/**
	 * @param node
	 * @throws MalformedURLException 
	 */
	private void loadTrack(Node trackNode) {
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
			url = new URL("http://dl.emusic.com/dl/"+id+"/"+filename);
		} catch (MalformedURLException e) {
			throw new UnknownFileException(e);
		}
		int trackNum = Integer.parseInt(num);
		Preferences prefs = Preferences.getInstance();
		File outputFile = new File(prefs.getFilename(trackNum, title, album, 
				artist, format));
		File coverArtFile = null;
		if (coverArt != null) 
			coverArtFile = getCoverArtCached(coverArt, prefs, trackNum, 
				title, album, artist);
		MusicDownloader dl = new MusicDownloader(url, outputFile, coverArtFile, trackNum, 
				title, album, artist);
		downloaders.add(dl);
		dl.setGenre(genre);
		try {
			dl.setDuration(Integer.parseInt(duration));
		} catch (NumberFormatException e) { 
//			 do nothing
		}
	}

	/**
	 * This is passed a String URL of where to find the coverart for a track.
	 * It turns it into a filename. If the file doesn't exist, and we haven't
	 * already created a downloader for it, then a download is added to the 
	 * list. 
	 * @param coverArt the string form of the URL to load
	 * @param artist the artist this cover is for
	 * @param album the album it's from
	 * @param title the title of the track
	 * @param trackNum the number of the track
	 * @return a file corresponding the coverart. It may not exist yet, but 
	 * that is where it eventually will be.
	 */
	private File getCoverArtCached(String coverArt, Preferences prefs, 
			int trackNum, String title, String album, String artist) {
		if (coverArtCache == null)
			coverArtCache = new Hashtable<String, File>();
		File cachedFile = coverArtCache.get(coverArt);
		if (cachedFile != null) 
			return cachedFile;
		URL coverUrl;
		try {
			coverUrl = new URL(coverArt);
		} catch (MalformedURLException e) { return null; }
		File coverFile;
		File savePath = prefs.getPathFor(trackNum, title, album, artist);
		int dotPos = coverArt.lastIndexOf(".");
		if (dotPos != -1) {
			String filetype = coverArt.substring(dotPos);
			if (filetype.equalsIgnoreCase(".jpeg"))
				filetype = ".jpg"; // who the hell uses ".jpeg" as an extension anyway?
			coverFile = new File(savePath,"cover"+filetype);
		} else {
			return null;
		}
		coverArtCache.put(coverArt, coverFile);
		if (!coverFile.exists()) {
			// add the downloader
			downloaders.add(new CoverDownloader(coverUrl, coverFile));
		}
		return coverFile;
	}

	/**
	 * Does a simple test to see if the file is one we recognise.
	 * @param file the file to test
	 * @return true if the file is file looks like an EMP file 
	 * @throws IOException if the file can't be read
	 */
	public static boolean canParse(File file) throws IOException {
		EMPDecoderStream stream = new EMPDecoderStream(new FileInputStream(file));
		// just look at the first Kb
		byte[] buff = new byte[1024];
        // ignore return value, we don't really care
		stream.read(buff);
		String s = new String(buff);		
        stream.close();
		return s.indexOf("<PACKAGE>") != -1;
	}
	
	public List<IDownloader> getDownloaders() {
		return downloaders;
	}

}
