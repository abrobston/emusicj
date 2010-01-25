package nz.net.kallisti.emusicj.tagging.vorbiscomments;

import nz.net.kallisti.emusicj.tagging.ITagFrame;
import adamb.vorbis.CommentField;

/**
 * <p>
 * This holds a single frame (comment field, in vorbis terms)
 * </p>
 * 
 * @author robin
 */
public class VorbisFrame implements ITagFrame {

	private final CommentField field;

	public VorbisFrame(CommentField field) {
		this.field = field;
	}

	public CommentField getFrame() {
		return field;
	}

}
