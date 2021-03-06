package nz.net.kallisti.emusicj.tagging.jid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFrame;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.v2.ID3V2Frame;

/**
 * <p>
 * This implements an ID3 tag in a way to work with JID3
 * </p>
 * 
 * @author robin
 */
public class JID3Data implements ITagData {

	Map<String, Set<List<String>>> frames = new HashMap<String, Set<List<String>>>();
	private final Logger logger;
	private final JID3Utils utils;

	public JID3Data(JID3Utils utils) {
		this.utils = utils;
		logger = LogUtils.getLogger(this);
	}

	public Set<String> getFrameTypes() {
		return frames.keySet();
	}

	public Set<ITagFrame> getFramesForType(String id3FrameType) {
		Set<ITagFrame> result = new HashSet<ITagFrame>();
		Set<List<String>> framesForType = frames.get(id3FrameType);
		for (List<String> frameSpec : framesForType) {
			try {
				ID3V2Frame frame = utils.listToFrame(id3FrameType, frameSpec);
				if (frame == null) {
					logger
							.warning("An error occurred creating a frame of type "
									+ id3FrameType + " with data: " + frameSpec);
					continue;
				}
				result.add(new JID3Frame(frame));
			} catch (ID3Exception e) {
				logger.log(Level.WARNING,
						"An exception was encountered creating an ID3 frame of type '"
								+ id3FrameType + "'", e);
			}
		}
		return result;
	}

	/**
	 * This adds a frame to the ID3 data, containing the specified values. See
	 * {@link JID3Utils#listToFrame(String, List)} for more details on the
	 * arguments.
	 * 
	 * @param type
	 *            the type of the frame
	 * @param values
	 *            the values for the frame. If this is <code>null</code>, no
	 *            action is performed.
	 */
	void addFrame(String type, List<String> values) {
		if (values == null)
			return;
		Set<List<String>> typeSet = frames.get(type);
		if (typeSet == null) {
			typeSet = new HashSet<List<String>>();
			frames.put(type, typeSet);
		}
		typeSet.add(values);
	}

}
