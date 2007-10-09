package nz.net.kallisti.emusicj.providers;

import java.io.FilenameFilter;

import nz.net.kallisti.emusicj.bindingtypes.WatchFiles;
import nz.net.kallisti.emusicj.dropdir.DirectoryMonitor;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitor;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>A provider that configures a DirectoryMonitor with the appropriate
 * FilenameFilter</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class WatchFilesDirectoryMonitorProvider implements Provider<IDirectoryMonitor> {

	private final FilenameFilter filter;

	@Inject
	public WatchFilesDirectoryMonitorProvider(@WatchFiles FilenameFilter filter) {
		this.filter = filter;
	}
	
	public IDirectoryMonitor get() {
		return new DirectoryMonitor(filter);
	}

}
