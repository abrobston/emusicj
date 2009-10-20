package nz.net.kallisti.emusicj.id3.jid;

import java.util.List;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3FromXML;
import nz.net.kallisti.emusicj.misc.ListUtils;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Converts the standard XML form from EMX-style files into an ID3 data object,
 * configured to work with the JID3 libraries.
 * </p>
 * 
 * @author robin
 */
public class JID3FromXML implements IID3FromXML {

	private final Logger logger;
	private final JID3Utils utils;

	@Inject
	public JID3FromXML(Provider<ISimpleDownloader> dlProv) {
		logger = LogUtils.getLogger(this);
		utils = new JID3Utils(dlProv);
	}

	/**
	 * Given an XML node, this builds the data object.
	 */
	public IID3Data getData(Node id3Node) {
		NodeList fields = id3Node.getChildNodes();
		JID3Data id3 = new JID3Data(utils);
		for (int i = 0; i < fields.getLength(); i++) {
			Node node = fields.item(i);
			if (node.getChildNodes() == null)
				continue;
			String name = node.getNodeName().toLowerCase();
			if (name.equals("title")) {
				id3.addFrame(JID3Utils.ID3_TITLE, makeNodeList(node));
			} else if (name.equals("album")) {
				id3.addFrame(JID3Utils.ID3_ALBUM, makeNodeList(node));
			} else if (name.equals("artist")) {
				id3.addFrame(JID3Utils.ID3_ARTIST, makeNodeList(node));
			} else if (name.equals("composer")) {
				id3.addFrame(JID3Utils.ID3_COMPOSER, makeNodeList(node));
			} else if (name.equals("genre")) {
				id3.addFrame(JID3Utils.ID3_GENRE, makeNodeList(node));
			} else if (name.equals("filename")) {
				id3.addFrame(JID3Utils.ID3_FILENAME, makeNodeList(node));
			} else if (name.equals("conductor")) {
				id3.addFrame(JID3Utils.ID3_CONDUCTOR, makeNodeList(node));
			} else if (name.equals("isrc")) {
				id3.addFrame(JID3Utils.ID3_ISRC, makeNodeList(node));
			} else if (name.equals("disc_number")) {
				id3.addFrame(JID3Utils.ID3_DISCNUM, makeNodeList(node));
			} else if (name.equals("track")) {
				id3.addFrame(JID3Utils.ID3_TRACK, makeNodeList(node));
			} else if (name.equals("copyright")) {
				Node yearNode = node.getAttributes().getNamedItem("year");
				if (yearNode == null) {
					logger
							.warning("'copyright' element has no year defined, skipping");
					continue;
				}
				id3.addFrame(JID3Utils.ID3_COPYRIGHT, makeNodeList(yearNode
						.getNodeValue(), node));
			} else if (name.equals("www")) {
				id3.addFrame(JID3Utils.ID3_WWW, makeNodeList(node));
				// } else if (name.equals("cover")) {
				// id3.addFrame(JID3Utils.ID3_COMPOSER,
				// ListUtils.list(node.getFirstChild().getNodeValue()));
			} else if (name.equals("psn_id")) {
				id3.addFrame(JID3Utils.ID3_CUSTOM_TEXT, makeNodeList("PSN ID",
						node));
			} else if (name.equals("album_artist")) {
				id3.addFrame(JID3Utils.ID3_ALBUM_ARTIST, makeNodeList(node));
			} else if (name.equals("grouping")) {
				id3.addFrame(JID3Utils.ID3_GROUPING, makeNodeList(node));
			} else if (name.equals("cover")) {
				id3.addFrame(JID3Utils.ID3_COVERART, makeNodeList(node));
			} else if (name.equals("priv_umg")) {
				id3.addFrame(JID3Utils.ID3_CUSTOM_TEXT, makeNodeList(
						"Priv UMG", node));
			}

		}
		return id3;
	}

	/**
	 * This produces a list of one item containing the text content of the
	 * provided node.
	 * 
	 * @param node
	 *            the node to get the content from
	 * @return a list containing the node, or <code>null</code> if there is no
	 *         text content
	 */
	private List<String> makeNodeList(Node node) {
		String cont = node.getTextContent();
		if (cont == null)
			return null;
		if (cont.equals(""))
			return null;
		return ListUtils.list(cont);
	}

	/**
	 * This produces a list of two items, the first item is the provided string,
	 * the second is the text content of the node.
	 * 
	 * @param str
	 *            the first string for the list
	 * @param node
	 *            the node to get the text content from
	 * @return a list containing the provided string, and the text content of
	 *         the node, or <code>null</code> if <code>str</code> is
	 *         <code>null</code> or there is no text content or it is an empty
	 *         string.
	 */
	private List<String> makeNodeList(String str, Node node) {
		if (str == null)
			return null;
		String cont = node.getTextContent();
		if (cont == null)
			return null;
		if (cont.equals(""))
			return null;
		return ListUtils.list(str, cont);
	}

}
