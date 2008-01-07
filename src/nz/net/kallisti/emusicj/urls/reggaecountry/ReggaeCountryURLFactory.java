package nz.net.kallisti.emusicj.urls.reggaecountry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import nz.net.kallisti.emusicj.urls.IURLFactory;

/**
 * <p>
 * Provides the URLs for the Reggae Country variant of the eMusic/J program
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ReggaeCountryURLFactory implements IURLFactory {

	private static final String UPDATEURL_KEY = "updateurl";
	private static final String MANUALURL_KEY = "manualurl";
	private static final String APPURL_KEY = "appurl";
	private final Properties props;

	/**
	 * This initialises the URL factory and loads the properties file containing
	 * the URLs.
	 */
	public ReggaeCountryURLFactory() {
		props = new Properties();
		File loc = new File("lib", "reggaecountry.properties");
		try {
			FileInputStream inStream = new FileInputStream(loc);
			props.load(inStream);
		} catch (IOException e) {
			// e.printStackTrace();
			props.setProperty(APPURL_KEY,
					"http://www.reggaecountry.com/downloadmanager/");
			props.setProperty(MANUALURL_KEY,
					"http://www.reggaecountry.com/downloadmanager/manual/");
			props.setProperty(UPDATEURL_KEY,
					"http://www.reggaecountry.com/downloadmanager/update/");
			System.err.println("Warning: didn't read " + loc + ": "
					+ e.getMessage());
		}
	}

	public URL getAppURL() {
		try {
			return new URL(props.getProperty(APPURL_KEY));
		} catch (MalformedURLException e) {
			System.err.println("Error: can't create app URL:");
			e.printStackTrace();
			return null;
		}
	}

	public URL getManualURL() {
		try {
			return new URL(props.getProperty(MANUALURL_KEY));
		} catch (MalformedURLException e) {
			System.err.println("Error: can't create manual URL:");
			e.printStackTrace();
		}
		return null;
	}

	public URL getUpdateURL() {
		try {
			return new URL(props.getProperty(UPDATEURL_KEY));
		} catch (MalformedURLException e) {
			System.err.println("Error: can't create update URL:");
			e.printStackTrace();
		}
		return null;
	}

}
