package nz.net.kallisti.emusicj.id3;

import java.util.Set;

/**
 * <p>
 * Instances of this contain data to write into ID3 tags when the MP3 file is
 * saved.
 * </p>
 * <p>
 * The ID3 spec is useful for working with this, it can be found at {@link http
 * ://www.id3.org/id3v2.3.0}.
 * </p>
 * 
 * @author robin
 */
public interface IID3Data {

	/**
	 * This provides the frame types that this object contains data for. This
	 * will be a set of 4-character strings, such as 'TCOP' and so on. These
	 * correspond to the frame types in the spec.
	 * 
	 * @return
	 */
	public Set<String> getFrameTypes();

	/**
	 * This gets a set of frames for the specified frame type.
	 * 
	 * @param id3FrameType
	 *            the type you want the frames for, should be something returned
	 *            by {@link #getFrameTypes()}
	 * @return a set of frames
	 */
	public Set<IID3Frame> getFramesForType(String id3FrameType);

}
