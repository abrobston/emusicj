package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.io.InputStream;


import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * Implementations of this can create images from streams
 * </p>
 * 
 * @author robin
 */
public interface IStreamDynamicImageProvider extends IDynamicImageProvider {

	/**
	 * Sets the parameters needed to create the image
	 * 
	 * @param display
	 *            the display used to generate it
	 * @param stream
	 *            the stream to create it from
	 */
	public abstract void setParams(Display display, InputStream stream);

}