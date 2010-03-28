package nz.net.kallisti.emusicj.tagging.general;

import java.util.logging.Logger;

import nz.net.kallisti.emusicj.bindingtypes.ID3Tagger;
import nz.net.kallisti.emusicj.bindingtypes.VorbisTagger;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFromXML;

import org.w3c.dom.Node;

import com.google.inject.Inject;

/**
 * <p>
 * This is aware of the different file type tagging things, and chooses the
 * appropriate one to use. note that it doesn't implement {@link ITagFromXML} as
 * it needs more data than that provides.
 * </p>
 * 
 * @author robin
 */
public class GeneralFromXML implements IGeneralTagFromXML {

	private final ITagFromXML id3Tagger;
	private final ITagFromXML vorbisTagger;
	private final Logger logger;

	@Inject
	public GeneralFromXML(@ID3Tagger ITagFromXML id3Tagger,
			@VorbisTagger ITagFromXML vorbisTagger) {
		this.id3Tagger = id3Tagger;
		this.vorbisTagger = vorbisTagger;
		logger = LogUtils.getLogger(this);
	}

	public ITagData getData(Node tagNode, String extension) {
		if (id3Tagger.supportedFile(extension))
			return id3Tagger.getData(tagNode);
		if (vorbisTagger.supportedFile(extension))
			return vorbisTagger.getData(tagNode);
		logger.info("No tagger found for extension: " + extension);
		return null;
	}

}
