package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * A standard implementation of {@link IStreamAndURLDynamicImageProvider}.
 * </p>
 * 
 * @author robin
 */
public class StreamAndURLDynamicImageProvider implements
		IStreamAndURLDynamicImageProvider, IDynamicImageChangeListener {

	private URLDynamicImageProvider urlProv = null;
	private StreamDynamicImageProvider streamProv = null;
	private Display display;
	private File cacheDir;
	private final Provider<ISimpleDownloader> downloadProvider;
	private IDynamicImageProvider currentProvider = null;
	private final List<IDynamicImageChangeListener> listeners = Collections
			.synchronizedList(new ArrayList<IDynamicImageChangeListener>());

	@Inject
	public StreamAndURLDynamicImageProvider(
			Provider<ISimpleDownloader> downloadProvider) {
		this.downloadProvider = downloadProvider;
	}

	public void setParams(Display display, URL url, File cacheDir) {
		setParams(display, url, cacheDir);
	}

	public synchronized void setParams(Display display, URL url,
			InputStream stream, File cacheDir) {
		this.display = display;
		this.cacheDir = cacheDir;
		if (url != null) {
			urlProv = new URLDynamicImageProvider(downloadProvider);
			urlProv.setParams(display, url, cacheDir);
			urlProv.addListener(this);
			currentProvider = urlProv;
		} else if (stream != null) {
			streamProv = new StreamDynamicImageProvider();
			streamProv.setParams(display, stream);
			streamProv.addListener(this);
			currentProvider = streamProv;
		}
	}

	public Image getImage() {
		if (currentProvider == null)
			return null;
		return currentProvider.getImage();
	}

	public synchronized void changeURL(URL url) {
		if (urlProv == null) {
			urlProv = new URLDynamicImageProvider(downloadProvider);
			urlProv.setParams(display, url, cacheDir);
			urlProv.addListener(this);
			currentProvider = urlProv;
		} else {
			urlProv.changeURL(url);
		}
	}

	public void addListener(IDynamicImageChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IDynamicImageChangeListener listener) {
		listeners.remove(listener);
	}

	public void newImage(IDynamicImageProvider dynImage, Image image) {
		for (IDynamicImageChangeListener l : listeners) {
			l.newImage(dynImage, image);
		}
	}

}
