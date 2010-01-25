package nz.net.kallisti.emusicj.tagging;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Implementations of this write the tags to the audio file
 * </p>
 * 
 * @author robin
 */
public interface ITagToFile {

	/**
	 * Adds the provided MP3 data to the file.
	 * 
	 * @param tagData
	 *            the tag data object
	 * @param file
	 *            the file to write the tags for
	 * @throws RuntimeException
	 *             if the type of <code>tagData</code> isn't known to this
	 *             implementation
	 * @throws IOException
	 *             if an error occurs working with the file
	 */
	public void writeTag(ITagData tagData, File file) throws RuntimeException,
			IOException;

	/**
	 * This indicates that this writer can write tags to the provided file. For
	 * example, the ID3 tagger can only write to MP3 files, not Ogg types.
	 * 
	 * @param file
	 *            the file to check
	 * @return <code>true</code> if this file is supported, <code>false</code>
	 *         otherwise.
	 */
	public boolean supportedFile(File file);

}
