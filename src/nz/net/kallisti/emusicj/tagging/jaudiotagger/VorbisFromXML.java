package nz.net.kallisti.emusicj.tagging.jaudiotagger;

import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFromXML;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This takes the node from the metadata file and produces the tags for it
 * </p>
 * 
 * @author robin
 */
public class VorbisFromXML implements ITagFromXML {

	public ITagData getData(Node tagNode) {
		NodeList fields = tagNode.getChildNodes();
		VorbisData tag = new VorbisData();
		for (int i = 0; i < fields.getLength(); i++) {
			Node node = fields.item(i);
			if (node.getChildNodes() == null)
				continue;
			String name = node.getNodeName().toLowerCase();
			String text = node.getTextContent();
			if (name.equals("title")) {
				tag.addFrame("TITLE", text);
			} else if (name.equals("album")) {
				tag.addFrame("ALBUM", text);
			} else if (name.equals("artist")) {
				tag.addFrame("ARTIST", text);
			} else if (name.equals("composer")) {
				tag.addFrame("COMPOSER", text);
			} else if (name.equals("genre")) {
				tag.addFrame("GENRE", text);
			} else if (name.equals("filename")) {
				tag.addFrame("FILENAME", text);
			} else if (name.equals("conductor")) {
				tag.addFrame("CONDUCTOR", text);
			} else if (name.equals("isrc")) {
				tag.addFrame("ISRC", text);
			} else if (name.equals("disc_number")) {
				tag.addFrame("DISCNUMBER", text);
			} else if (name.equals("track")) {
				tag.addFrame("TRACKNUMBER", text);
			} else if (name.equals("copyright")) {
				Node yearNode = node.getAttributes().getNamedItem("year");
				String copyright = (yearNode != null ? yearNode.getNodeValue()
						+ " " : "")
						+ text;
				tag.addFrame("COPYRIGHT", copyright);
			} else if (name.equals("www")) {
				tag.addFrame("CONTACT", text);
			} else if (name.equals("psn_id")) {
				tag.addFrame("PSN_ID", text);
			} else if (name.equals("album_artist")) {
				tag.addFrame("ALBUMARTIST", text);
			} else if (name.equals("grouping")) {
				tag.addFrame("GROUPING", text);
			} else if (name.equals("cover")) {
				tag.addFrame("COVERART", text); // this is handled specially
				// when it is written
			} else if (name.equals("priv_umg")) {
				tag.addFrame("PRIV_UMG", text);
			} else if (name.equals("recording_date")) {
				// currently ignored until we work out how best to store it
			} else if (name.equals("recording_dates")) {
				tag.addFrame("DATE", text);
			} else if (name.equals("recording_time")) {
				// currently ignored
			} else if (name.equals("recording_year")) {
				// currently ignored
			} else if (name.equals("publisher")) {
				tag.addFrame("PUBLISHER", text);
			} else if (name.equals("lyricist")) {
				tag.addFrame("LYRICIST", text);
			}
		}
		return tag;
	}

	public boolean supportedFile(String filename) {
		return VorbisWriter.supportedFileInternal(filename);
	}

}
