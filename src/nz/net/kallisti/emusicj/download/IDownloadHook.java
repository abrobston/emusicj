package nz.net.kallisti.emusicj.download;

/**
 * <p>
 * This is a hook that will be notified on a download event.
 * </p>
 * 
 * @author robin
 */
public interface IDownloadHook {

	public enum EventType {
		FINISHED
	};

	/**
	 * This is called when a download event occurs.
	 * 
	 * @param type
	 *            the type of event, see {@link EventType}.
	 * @param downloader
	 *            the downloader that triggered the event
	 */
	public void downloadEvent(EventType type, IDownloader downloader);

}
