package nz.net.kallisti.emusicj.tagging;

import java.util.Set;

/**
 * <p>
 * Instances of this contain data to write into file tags when the file is
 * saved. It's oriented towards ID3v2 tags, but can work with others, such as
 * vorbis comments.
 * </p>
 * <p>
 * The ID3 spec is useful for working with this, it can be found at {@link http
 * ://www.id3.org/id3v2.3.0}.
 * </p>
 * 
 * @author robin
 */
public interface ITagData {

	/**
	 * This provides the frame types that this object contains data for. This
	 * will be a set of strings, such as 'TCOP' and so on. These correspond to
	 * the frame types in the spec.
	 * 
	 * @return
	 */
	public Set<String> getFrameTypes();

	/**
	 * This gets a set of frames for the specified frame type.
	 * 
	 * @param frameType
	 *            the type you want the frames for, should be something returned
	 *            by {@link #getFrameTypes()}
	 * @return a set of frames
	 */
	public Set<ITagFrame> getFramesForType(String frameType);

}
