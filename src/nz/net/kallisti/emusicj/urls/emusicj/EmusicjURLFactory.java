package nz.net.kallisti.emusicj.urls.emusicj;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.urls.IURLFactory;

/**
 * <p>
 * Provides the URLs for the eMusic/J program
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class EmusicjURLFactory implements IURLFactory {

	private final Logger logger;

	public EmusicjURLFactory() {
		logger = LogUtils.getLogger(this);
	}

	public URL getAppURL() {
		try {
			return new URL("http://www.kallisti.net.nz/EMusicJ");
		} catch (MalformedURLException e) {
			System.err.println("Error: can't create app URL:");
			e.printStackTrace();
			return null;
		}
	}

	public URL getManualURL() {
		try {
			return new URL("http://www.kallisti.net.nz/EMusicJ/UserManual");
		} catch (MalformedURLException e) {
			System.err.println("Error: can't create manual URL:");
			e.printStackTrace();
		}
		return null;
	}

	public URL getUpdateURL() {
		try {
			return new URL(
					"http://www.kallisti.net.nz/~robin/emusicj-version.txt");
		} catch (MalformedURLException e) {
			System.err.println("Error: can't create update URL:");
			e.printStackTrace();
		}
		return null;
	}

	public URL getToolbarIconClickURL() {
		try {
			return new URL("http://www.kallisti.net.nz/EMusicJ");
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING,
					"Unable to create toolbar icon click URL", e);
		}
		return null;
	}

	public URL getToolbarIconSourceURL() {
		return null;
	}

	public URL getCustomerSupportURL() {
		return null;
	}

}
