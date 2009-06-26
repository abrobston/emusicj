package nz.net.kallisti.emusicj.files.cleanup;

import java.io.File;

/**
 * <p>
 * Implementations of this allow files to be cleaned up at a later date. It is
 * similar to {@link File#deleteOnExit()}, however also allows entries to be
 * removed. Along with this, it requires an explicit call to clean up the files.
 * Generally, this is a singleton, and the deletion method is called on exit.
 * </p>
 * 
 * @author robin
 */
public interface ICleanupFiles {

	/**
	 * Adds a file to be deleted when {@link #deleteFiles()} is called.
	 * 
	 * @param file
	 *            the file to delete
	 */
	public void addFile(File file);

	/**
	 * Removes a file from the 'to be deleted' list.
	 * 
	 * @param file
	 *            the file to remove
	 */
	public void removeFile(File file);

	/**
	 * Delete all the files that have been added (and not removed.) Any deletion
	 * errors are suppressed.
	 */
	public void deleteFiles();

}
