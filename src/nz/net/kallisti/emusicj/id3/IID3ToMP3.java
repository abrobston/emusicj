package nz.net.kallisti.emusicj.id3;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Implementations of this write the ID3 tags
 * </p>
 * 
 * @author robin
 */
public interface IID3ToMP3 {

	/**
	 * Adds the provided MP3 data to the file.
	 * 
	 * @param id3Data
	 *            the ID3 data object
	 * @param file
	 *            the file to write the tags for
	 * @throws RuntimeException
	 *             if the type of <code>id3Data</code> isn't known to this
	 *             implementation
	 * @throws IOException
	 *             if an error occurs working with the file
	 */
	public void writeMP3(IID3Data id3Data, File file) throws RuntimeException,
			IOException;

}
