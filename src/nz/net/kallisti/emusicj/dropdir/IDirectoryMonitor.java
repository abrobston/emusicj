package nz.net.kallisti.emusicj.dropdir;

import java.io.File;

/**
 * <p>The interface for classes that watch a directory and notify of new files
 * found.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IDirectoryMonitor {

	/**
	 * Used to shut down the monitor. Must be called prior to the program being
	 * shut down in order to allow the JVM to exit.
	 */
	public void stopMonitor();

	/**
	 * This allows the directory being monitored to be changed.
	 * @param file the new directory to monitor
	 */
	public void setDirToMonitor(File dir);

	public void setListener(IDirectoryMonitorListener listener);

}
