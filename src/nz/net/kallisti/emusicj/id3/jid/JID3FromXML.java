package nz.net.kallisti.emusicj.id3.jid;

import java.util.logging.Logger;

import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3FromXML;
import nz.net.kallisti.emusicj.misc.ListUtils;
import nz.net.kallisti.emusicj.misc.LogUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;

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

	@Inject
	public JID3FromXML() {
		logger = LogUtils.getLogger(this);
	}

	/**
	 * Given an XML node, this builds the data object.
	 */
	public IID3Data getData(Node id3Node) {
		NodeList fields = id3Node.getChildNodes();
		JID3Data id3 = new JID3Data();
		for (int i = 0; i < fields.getLength(); i++) {
			Node node = fields.item(i);
			if (node.getChildNodes() == null)
				continue;
			String name = node.getNodeName().toLowerCase();
			if (name.equals("title")) {
				id3.addFrame(JID3Utils.ID3_TITLE, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("album")) {
				id3.addFrame(JID3Utils.ID3_ALBUM, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("artist")) {
				id3.addFrame(JID3Utils.ID3_ARTIST, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("composer")) {
				id3.addFrame(JID3Utils.ID3_COMPOSER, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("genre")) {
				id3.addFrame(JID3Utils.ID3_GENRE, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("filename")) {
				id3.addFrame(JID3Utils.ID3_FILENAME, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("conductor")) {
				id3.addFrame(JID3Utils.ID3_CONDUCTOR, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("isrc")) {
				id3.addFrame(JID3Utils.ID3_ISRC, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("disc_number")) {
				id3.addFrame(JID3Utils.ID3_DISCNUM, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("track")) {
				id3.addFrame(JID3Utils.ID3_TRACK, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("copyright")) {
				Node yearNode = node.getAttributes().getNamedItem("year");
				if (yearNode == null) {
					logger
							.warning("'copyright' element has no year defined, skipping");
					continue;
				}
				id3.addFrame(JID3Utils.ID3_COPYRIGHT, ListUtils.list(node
						.getNodeValue(), node.getTextContent()));
			} else if (name.equals("www")) {
				id3.addFrame(JID3Utils.ID3_WWW, ListUtils.list(node
						.getTextContent()));
				// } else if (name.equals("cover")) {
				// id3.addFrame(JID3Utils.ID3_COMPOSER,
				// ListUtils.list(node.getFirstChild().getNodeValue()));
			} else if (name.equals("psn_id")) {
				id3.addFrame(JID3Utils.ID3_PSN_ID, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("grouping")) {
				id3.addFrame(JID3Utils.ID3_GROUPING, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("album_artist")) {
				id3.addFrame(JID3Utils.ID3_ALBUM_ARTIST, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			} else if (name.equals("priv_umg")) {
				id3.addFrame(JID3Utils.ID3_PRIV_UMG, ListUtils.list(node
						.getFirstChild().getNodeValue()));
			}

		}
		return id3;
	}

}
