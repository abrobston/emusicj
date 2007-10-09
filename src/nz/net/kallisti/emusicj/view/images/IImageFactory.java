package nz.net.kallisti.emusicj.view.images;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * <p>Implementations of this create the images to be used in the view.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IImageFactory {

	/**
	 * Sets the display the images will use
	 * @param display the display images will be created for
	 */
	public void setDisplay(Display display);
	
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
	
	

}
