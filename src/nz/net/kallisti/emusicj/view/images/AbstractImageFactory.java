package nz.net.kallisti.emusicj.view.images;

import java.io.InputStream;

import org.eclipse.swt.widgets.Display;

import com.google.inject.Provider;

/**
 * <p>
 * This provides basic utilities to help working with images, particularly
 * dynamic ones.
 * </p>
 * 
 * @author robin
 */
public abstract class AbstractImageFactory implements IImageFactory {

	protected Display display;
	protected final Provider<IStreamDynamicImageProvider> streamImageProvider;

	public AbstractImageFactory(
			Provider<IStreamDynamicImageProvider> streamImageProvider) {
		this.streamImageProvider = streamImageProvider;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * This creates and configures a stream provider if necessary. If
	 * <code>prov</code> is not <code>null</code>, it does nothing (but returns
	 * the provider you gave it). Otherwise it returns the initialised stream
	 * provider/
	 * 
	 * @param prov
	 *            the provider to test
	 * @param stream
	 *            the stream to init with if needed
	 * @return the provider you can use
	 */
	protected synchronized IStreamDynamicImageProvider initStreamImageProvider(
			IStreamDynamicImageProvider prov, InputStream stream) {
		if (prov != null)
			return prov;
		IStreamDynamicImageProvider provider = streamImageProvider.get();
		provider.setParams(display, stream);
		return provider;
	}

}