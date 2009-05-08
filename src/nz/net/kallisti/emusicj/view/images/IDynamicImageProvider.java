package nz.net.kallisti.emusicj.view.images;

import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * Implementations of this provide an image that may be backed by a source that
 * can change. It will notify listeners should the image need updating.
 * </p>
 * 
 * @author robin
 */
public interface IDynamicImageProvider {

	/**
	 * Provides the current image
	 * 
	 * @return the current image, or <code>null</code> if there isn't one
	 */
	public Image getImage();

	/**
	 * This adds a listener that will be notified when the image changes
	 * 
	 * @param listener
	 *            the listener to notify about image changes
	 */
	public void addListener(IDynamicImageChangeListener listener);

	/**
	 * This removes a listener that was added with
	 * {@link #addListener(IDynamicImageChangeListener)}.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(IDynamicImageChangeListener listener);
}
