package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.io.InputStream;


import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * This creates images from streams. It doesn't detect changes.
 * </p>
 * 
 * @author robin
 */
public class StreamDynamicImageProvider implements IDynamicImageProvider,
		IStreamDynamicImageProvider {

	private Image image;

	public void setParams(Display display, InputStream stream) {
		image = new Image(display, stream);
	}

	public void addListener(IDynamicImageChangeListener listener) {
	}

	public Image getImage() {
		return image;
	}

	public void removeListener(IDynamicImageChangeListener listener) {
	}

}
