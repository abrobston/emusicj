package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * Instances of this can be notified when an {@link IDynamicImageProvider} gets
 * a new image.
 * </p>
 * 
 * @author robin
 */
public interface IDynamicImageChangeListener {

	/**
	 * This is called when the image changes.
	 * 
	 * @param dynImage
	 *            the dynamic image provider that changed.
	 * @param image
	 *            the new image, or <code>null</code> if the image was removed
	 *            and not replaced. Note that this instance may be shared, so
	 *            don't dispose it yourself. It will be automatically disposed
	 *            on update.
	 */
	public void newImage(IDynamicImageProvider dynImage, Image image);

}
