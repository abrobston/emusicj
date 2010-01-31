package nz.net.kallisti.emusicj.tagging.jaudiotagger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFrame;

/**
 * <p>
 * This contains a file's worth of tag information.
 * </p>
 * 
 * @author robin
 */
public class VorbisData implements ITagData {

	Map<String, Set<String>> fields = new HashMap<String, Set<String>>();

	public Set<String> getFrameTypes() {
		return fields.keySet();
	}

	public Set<ITagFrame> getFramesForType(String frameType) {
		Set<String> fieldSet = fields.get(frameType);
		Set<ITagFrame> result = new HashSet<ITagFrame>();
		for (String field : fieldSet) {
			VorbisFrame frame = new VorbisFrame(frameType, field);
			result.add(frame);
		}
		return result;
	}

	void addFrame(String name, String value) {
		if (name == null || value == null)
			return;
		Set<String> nameSet = fields.get(name);
		if (nameSet == null) {
			nameSet = new HashSet<String>();
			fields.put(name, nameSet);
		}
		nameSet.add(value);
	}

}
