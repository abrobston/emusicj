package nz.net.kallisti.emusicj.tagging.general;

import java.util.logging.Logger;

import nz.net.kallisti.emusicj.bindingtypes.ID3Tagger;
import nz.net.kallisti.emusicj.bindingtypes.VorbisTagger;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagSerialiser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;

/**
 * <p>
 * This understands the different types of serialisers and will choose the
 * appropriate one depending on what the data is
 * </p>
 * 
 * @author robin
 */
public class GeneralTagSerialiser implements ITagSerialiser {

	private final ITagSerialiser id3Serialiser;
	private final ITagSerialiser vorbisSerialiser;
	private final Logger logger;

	@Inject
	public GeneralTagSerialiser(@ID3Tagger ITagSerialiser id3Serialiser,
			@VorbisTagger ITagSerialiser vorbisSerialiser) {
		this.id3Serialiser = id3Serialiser;
		this.vorbisSerialiser = vorbisSerialiser;
		logger = LogUtils.getLogger(this);
	}

	public ITagData deserialise(Element e) {
		String tagType = e.getAttribute("type");
		if (tagType == null || tagType.equals(""))
			return null;
		if (tagType.equals("id3")) {
			return id3Serialiser.deserialise(e);
		} else if (tagType.equals("vorbis")) {
			return vorbisSerialiser.deserialise(e);
		} else {
			logger.warning("Unknown tag type: " + tagType);
		}
		return null;
	}

	public void serialise(Element e, Document doc, ITagData data)
			throws IllegalArgumentException {
		// exceptions for flow control sucks. Refactor properly some time soon
		// TODO
		try {
			id3Serialiser.serialise(e, doc, data);
			e.setAttribute("type", "id3");
			return;
		} catch (IllegalArgumentException ex) {
			// ignore
		}
		try {
			vorbisSerialiser.serialise(e, doc, data);
			e.setAttribute("type", "vorbis");
			return;
		} catch (IllegalArgumentException ex) {
			// ignore
		}
		logger.warning("Unable to find a serialiser for type: "
				+ data.getClass());
	}
}
