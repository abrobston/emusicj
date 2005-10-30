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
import nz.net.kallisti.emusicj.download.HTTPDownloader;
import nz.net.kallisti.emusicj.download.HTTPMusicDownloader;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;
import nz.net.kallisti.emusicj.metafiles.streams.EMPDecoderStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>Loads a .emp file, and creates downloaders from it.</p>
 * 
 * <p>$Id$</p>
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
			throw new UnknownFileException(e);
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
		downloaders.add(new HTTPMusicDownloader(url, outputFile, coverArtFile, trackNum, 
				title, album, artist));
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
			downloaders.add(new HTTPDownloader(coverUrl, coverFile));
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
		stream.read(buff);
		String s = new String(buff);		
		return s.indexOf("<PACKAGE>") != -1;
	}
	
	public List<IDownloader> getDownloaders() {
		return downloaders;
	}

}
