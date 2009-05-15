package nz.net.kallisti.emusicj.view.images;

import java.io.File;

import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * Implementations of this create the images to be used in the view.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface IImageFactory {

	/**
	 * Sets the display the images will use
	 * 
	 * @param display
	 *            the display images will be created for
	 */
	public void setDisplay(Display display);

	/**
	 * This sets the cache dir that is used for images that are fetched from
	 * URLs
	 * 
	 * @param cacheDir
	 *            the directory that is to be used for caching. If it unable to
	 *            be used, then URL-based images probably won't work.
	 */
	public void setCacheDir(File cacheDir);

	/**
	 * @return the system tray icon that indicates downloading is happening
	 */
	public Image getDownloadingIcon();

	/**
	 * @return the system tray icon that indicates no downloading is happening
	 */
	public Image getNotDownloadingIcon();

	/**
	 * @return the icon used on the 'start download' button
	 */
	public Image getStartIcon();

	/**
	 * @return the icon used on the 'requeue track' button
	 */
	public Image getRequeueIcon();

	/**
	 * @return the icon used on the 'pause download' button
	 */
	public Image getPauseIcon();

	/**
	 * @return the icon used on the 'cancel download' button
	 */
	public Image getCancelIcon();

	/**
	 * @return the icons to be provided to the system to show the application
	 */
	public Image[] getAppIcons();

	/**
	 * @return the image for the about box logo
	 */
	public Image getAboutLogo();

	/**
	 * Gets the application logo for display on the button bar
	 * 
	 * @param parent
	 *            the parent widget for this one
	 * @param style
	 *            the SWT style
	 * @return a label containing the logo
	 */
	public IDynamicImageProvider getApplicationLogoProvider();

}
