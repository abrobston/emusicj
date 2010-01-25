package nz.net.kallisti.emusicj.tagging.vorbiscomments;

import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFrame;
import nz.net.kallisti.emusicj.tagging.ITagSerialiser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adamb.vorbis.CommentField;

import com.google.inject.Inject;

/**
 * <p>
 * This de/serialises a VorbisData object to an XML node
 * </p>
 * 
 * @author robin
 */
public class VorbisSerialiser implements ITagSerialiser {

	private final Logger logger;

	@Inject
	public VorbisSerialiser() {
		logger = LogUtils.getLogger(this);
	}

	public ITagData deserialise(Element e) {
		NodeList children = e.getChildNodes();
		VorbisData data = new VorbisData();
		for (int i = 0; i < children.getLength(); i++) {
			Node tag = children.item(i);
			String name;
			try {
				name = tag.getAttributes().getNamedItem("name").getNodeValue();
			} catch (NullPointerException ex) {
				logger
						.warning("Unknown data in VorbisComment description, skipping");
				continue;
			}
			String value = tag.getTextContent();
			data.addFrame(name, value);
		}
		return data;
	}

	public void serialise(Element e, Document doc, ITagData tagData)
			throws IllegalArgumentException {
		if (!(tagData instanceof VorbisData))
			throw new IllegalArgumentException(
					"Supplied data object is not of type VorbisData, it is "
							+ tagData.getClass());
		VorbisData data = (VorbisData) tagData;
		for (String type : data.getFrameTypes()) {
			for (ITagFrame f : data.getFramesForType(type)) {
				VorbisFrame frame = (VorbisFrame) f;
				CommentField field = frame.getFrame();
				Element fieldEl = doc.createElement("field");
				fieldEl.setAttribute("name", field.name);
				fieldEl.setTextContent(field.value);
				e.appendChild(fieldEl);
			}
		}
	}

}
