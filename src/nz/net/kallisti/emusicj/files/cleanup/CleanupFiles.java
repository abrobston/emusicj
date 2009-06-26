package nz.net.kallisti.emusicj.files.cleanup;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;

/**
 * <p>
 * Standard implementation of {@link ICleanupFiles}.
 * </p>
 * 
 * @author robin
 */
public class CleanupFiles implements ICleanupFiles {

	private final HashSet<File> files;
	private final Logger logger;

	public CleanupFiles() {
		files = new HashSet<File>();
		logger = LogUtils.getLogger(this);
	}

	public void addFile(File file) {
		files.add(file);
	}

	public void deleteFiles() {
		for (File file : files) {
			if (!file.delete()) {
				logger.log(Level.WARNING, "Unable to delete file: " + file);
			}
		}
	}

	public void removeFile(File file) {
		files.remove(file);
	}

}
