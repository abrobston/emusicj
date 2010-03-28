package nz.net.kallisti.emusicj.tagging.jaudiotagger;

import nz.net.kallisti.emusicj.tagging.ITagFrame;

/**
 * <p>
 * This holds a single frame (comment field, in vorbis terms)
 * </p>
 * 
 * @author robin
 */
public class VorbisFrame implements ITagFrame {

	private final String key;
	private final String value;

	public VorbisFrame(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
