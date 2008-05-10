package nz.net.kallisti.emusicj.misc.files;

import java.util.List;

/**
 * <p>
 * This interface defines classes that can be used to clean up file names to
 * make them match certain restrictions, such as characters that are illegal on
 * some platforms.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface IFileNameCleaner {

	/**
	 * This cleans the file name by replacing bad characters with a safe
	 * equivalent, and producing a new file.
	 * 
	 * @param filenames
	 *            the list of file name parts to clean. It is assumed that each
	 *            part of the provided list is a single directory.
	 * @param spacesToUnderscore
	 *            if true, spaces will be converted to underscore
	 * @return a list with cleaned up parts, suitable for turning into a file
	 */
	public List<String> cleanName(List<String> filenames,
			boolean spacesToUnderscore);

}
