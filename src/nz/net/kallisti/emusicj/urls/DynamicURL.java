package nz.net.kallisti.emusicj.urls;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Standard implementation of a dynamic URL
 * </p>
 * 
 * @author robin
 */
public class DynamicURL implements IDynamicURL {

	private final List<IDynamicURLListener> listeners = Collections
			.synchronizedList(new ArrayList<IDynamicURLListener>());

	private URL url;

	public void addListener(IDynamicURLListener listener) {
		listeners.add(listener);
	}

	public URL getURL() {
		return url;
	}

	public void removeListener(IDynamicURLListener listener) {
		listeners.remove(listener);
	}

	public void setURL(URL url) {
		this.url = url;
		for (IDynamicURLListener l : listeners) {
			l.newURL(this, url);
		}
	}

}
