package nz.net.kallisti.emusicj.models;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;

/**
 * This is a model that tracks the downloads that are currently in the system.
 * It has listeners that are signalled if something changes (such as downloads
 * being added or removed)
 * 
 * $Id$
 *
 * @author robin
 */
public interface IDownloadsModel {

	public void addListener(IDownloadsModelListener listener);
	
	public void removeListener(IDownloadsModelListener listener);
	
	public List<IDownloader> getDownloaders();
	
	public List<IDownloadMonitor> getDownloadMonitors();

    public void addDownload(IDownloader dl);

    public void removeDownload(IDownloader dl);
    
    /**
     * Removes downloads from the model
     * @param toRemove the downloads to remove
     */
    public void removeDownloads(List<IDownloader> toRemove);

	public boolean saveState(OutputStream stream);

	public void loadState(InputStream stream);
	
}
