package nz.net.kallisti.emusicj.id3.jid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3Frame;
import nz.net.kallisti.emusicj.misc.LogUtils;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.v2.ID3V2Frame;

/**
 * <p>
 * This implements an ID3 tag in a way to work with JID3
 * </p>
 * 
 * @author robin
 */
public class JID3Data implements IID3Data {

	Map<String, Set<List<String>>> frames = new HashMap<String, Set<List<String>>>();
	private final Logger logger;

	public JID3Data() {
		logger = LogUtils.getLogger(this);
	}

	public Set<String> getFrameTypes() {
		return frames.keySet();
	}

	public Set<IID3Frame> getFramesForType(String id3FrameType) {
		Set<IID3Frame> result = new HashSet<IID3Frame>();
		Set<List<String>> framesForType = frames.get(id3FrameType);
		for (List<String> frameSpec : framesForType) {
			try {
				ID3V2Frame frame = JID3Utils.listToFrame(id3FrameType,
						frameSpec);
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
	 *            the values for the frame
	 */
	void addFrame(String type, List<String> values) {
		Set<List<String>> typeSet = frames.get(type);
		if (typeSet == null) {
			typeSet = new HashSet<List<String>>();
			frames.put(type, typeSet);
		}
		typeSet.add(values);
	}

}
