package nz.net.kallisti.emusicj.dropdir;

import java.io.File;

/**
 * <p>Interface for classes wanting to be notified when a new file is discovered 
 * in the drop directory.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public interface IDirectoryMonitorListener {

	/**
	 * Notifies an object of a new file. 
	 * @param mon the monitor that detected the file
	 * @param file the file that it found
	 */
	public void newFile(DirectoryMonitor mon, File file);
	
}
