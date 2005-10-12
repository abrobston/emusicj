package nz.net.kallisti.emusicj.models;

import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;

/**
 * This is a model that tracks the downloads that are currently in the system.
 * It has listeners that are signalled if something changes (such as downloads
 * being added or removed)
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IDownloadsModel {

	public void addListener(IDownloadsModelListener listener);
	
	public void removeListener(IDownloadsModelListener listener);
	
	public List<IDownloader> getDownloaders();
	
	public List<IDownloadMonitor> getDownloadMonitors();

    public void addDownload(IMusicDownloader dl);
	
}
