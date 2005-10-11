package nz.net.kallisti.emusicj.models;

/**
 * <p>Specifies that the implementing class can receive events when the download
 * model changes state.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IDownloadsModelListener {

	public void downloadsListenerChanged(IDownloadsModel model);
	
}
