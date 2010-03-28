package nz.net.kallisti.emusicj.download;

import java.util.List;

/**
 * <p>
 * This is used to provide a collection of download hooks. Hooks will
 * automatically be applied to all download instances (no matter what type), and
 * aren't really intended to be changed at runtime.
 * </p>
 * <p>
 * A list of hooks is intended to be ordered, and they will be called in this
 * sequence.
 * </p>
 * 
 * @author robin
 */
public interface IDownloadHooks {

	/**
	 * These hooks are called when a download successfully completes.
	 * 
	 * @return a list of hooks to be run on download completion.
	 */
	public List<IDownloadHook> getCompletionHooks();

}
