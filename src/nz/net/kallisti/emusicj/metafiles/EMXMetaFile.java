package nz.net.kallisti.emusicj.metafiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
 * This handles loading of the eMusic .emx format. This is a different format to
 * the old .emp type, and is unencrypted. See {@link http
 * ://code.google.com/p/emusicremote/wiki/EMX_File_Format} for a description.
 * </p>
 * 
 * @author Michael MacDonald
 * @author Robin Sheat
 */
public class EMXMetaFile extends AbstractMetafile {

	List<IDownloader> downloaders = new ArrayList<IDownloader>();
	private final IPreferences prefs;
	private final Provider<IMusicDownloader> musicDownloaderProvider;
	private final Logger logger;

	@Inject
	public EMXMetaFile(IPreferences prefs, IStrings strings,
			Provider<IMusicDownloader> musicDownloaderProvider,
			Provider<ICoverDownloader> coverDownloaderProvider,
			IImageFactory images, IURLFactory urls) {
		super(images, urls, strings, coverDownloaderProvider);
		this.prefs = prefs;
		this.musicDownloaderProvider = musicDownloaderProvider;
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
			doc = builder.parse(new FileInputStream(file));
		} catch (SAXException e) {
			throw new UnknownFileException(
					"EMXMetaFile wasn't able to load this file. "
							+ "Perhaps try downloading it again", e);
		}
		Node root = doc.getDocumentElement();
		if (!(root.getNodeType() == Node.ELEMENT_NODE && root.getNodeName()
				.equalsIgnoreCase("package"))) {
			throw new UnknownFileException("EMXMetaFile Unknown file type");
		}
		if (!root.hasChildNodes()) {
			throw new UnknownFileException(
					"EMXMetaFile File appears to contain no downloads");
		}
		NodeList pkg = root.getChildNodes();
		Date expiry = null;
		for (int count = 0; count < pkg.getLength(); count++) {
			Node node = pkg.item(count);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (node.getNodeName().equalsIgnoreCase("tracklist")) {
				loadTrackList(node, expiry);
			} else if (node.getNodeName().equalsIgnoreCase("logo")) {
				setLogo(node);
			} else if (node.getNodeName().equalsIgnoreCase("exp_date")) {
				expiry = parseDate(node.getTextContent());
			} else if (node.getNodeName().equalsIgnoreCase("banner")) {
				setBanner(node);
			}
		}

	}

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
		String num = null;
		String title = null;
		String album = null;
		String artist = null;
		String coverArt = null;
		String genre = null;
		String track_url = null;
		String duration = null;
		String diskNumStr = null;
		String diskCountStr = null;
		for (int count = 0; count < track.getLength(); count++) {
			Node node = track.item(count);
			if (node.getFirstChild() == null)
				continue;
			if (node.getNodeName().equalsIgnoreCase("tracknum"))
				num = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("title"))
				title = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("album"))
				album = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("artist"))
				artist = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("trackurl"))
				track_url = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("albumart"))
				coverArt = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("genre"))
				genre = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("duration"))
				duration = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("discnum"))
				diskNumStr = node.getFirstChild().getNodeValue();
			else if (node.getNodeName().equalsIgnoreCase("disccount"))
				diskCountStr = node.getFirstChild().getNodeValue();
		}
		URL url;
		try {
			url = new URL(track_url);
		} catch (MalformedURLException e) {
			throw new UnknownFileException(e);
		}
		int trackNum = Integer.parseInt(num);
		Integer disk = null;
		Integer diskCount = null;
		if (diskNumStr != null && diskCountStr != null) {
			try {
				disk = Integer.parseInt(diskNumStr);
				diskCount = Integer.parseInt(diskCountStr);
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING,
						"Unable to parse disk or disk count information", e);
			}
		}

		File outputFile = new File(prefs.getFilename(trackNum, title, album,
				artist, ".mp3", disk, diskCount));
		File coverArtFile = null;
		if (coverArt != null)
			coverArtFile = getCoverArtCached(downloaders, coverArt, prefs,
					trackNum, title, album, artist, disk, diskCount);
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
	 * Does a simple test to see if the file is one we recognise.
	 * 
	 * @param file
	 *            the file to test
	 * @return true if the file is file looks like an EMP file
	 * @throws IOException
	 *             if the file can't be read
	 */
	public static boolean canParse(File file) throws IOException {
		if (!file.getName().endsWith(".emx")
				&& !file.getName().endsWith(".EMX")
				&& !file.getName().endsWith(".rcm")
				&& !file.getName().endsWith(".RCM")
				&& !file.getName().endsWith(".bpm")
				&& !file.getName().endsWith(".BPM")
				&& !file.getName().endsWith(".psn")
				&& !file.getName().endsWith(".PSN"))
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

	public List<IDownloader> getDownloaders() {
		return downloaders;
	}

}
