package nz.net.kallisti.emusicj.view.images;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamAndURLDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;

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
	private final Provider<IURLDynamicImageProvider> urlImageProvider;
	private File cacheDir;
	private final Provider<IStreamAndURLDynamicImageProvider> streamAndUrlImageProvider;

	public AbstractImageFactory(
			Provider<IStreamDynamicImageProvider> streamImageProvider,
			Provider<IURLDynamicImageProvider> urlImageProvider,
			Provider<IStreamAndURLDynamicImageProvider> streamAndUrlImageProvider) {
		this.streamImageProvider = streamImageProvider;
		this.urlImageProvider = urlImageProvider;
		this.streamAndUrlImageProvider = streamAndUrlImageProvider;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public void setCacheDir(File cacheDir) {
		if (!cacheDir.isDirectory()) {
			if (cacheDir.mkdir()) // We only want to use this if the dir was
				// created
				this.cacheDir = cacheDir;
		} else {
			this.cacheDir = cacheDir;
		}
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

	protected synchronized IURLDynamicImageProvider initURLImageProvider(
			IURLDynamicImageProvider prov, URL url) {
		if (prov != null)
			return prov;
		IURLDynamicImageProvider provider = urlImageProvider.get();
		provider.setParams(display, url, cacheDir);
		return provider;
	}

	protected synchronized IStreamAndURLDynamicImageProvider initStreamURLImageProvider(
			IStreamAndURLDynamicImageProvider prov, InputStream stream, URL url) {
		if (prov != null)
			return prov;
		IStreamAndURLDynamicImageProvider provider = streamAndUrlImageProvider
				.get();
		provider.setParams(display, url, stream, cacheDir);
		return provider;
	}

}