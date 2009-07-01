package nz.net.kallisti.emusicj.urls.naxos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.urls.AbstractURLFactory;
import nz.net.kallisti.emusicj.urls.IDynamicURL;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Provides the URLs for the eMusic/J program
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class NaxosURLFactory extends AbstractURLFactory {

	private final Properties props;
	private final Logger logger;

	/**
	 * This initialises the URL factory and loads the properties file containing
	 * the URLs.
	 */
	@Inject
	public NaxosURLFactory(Provider<IDynamicURL> dynUrlProvider) {
		super(dynUrlProvider);
		props = new Properties();
		File loc = new File("lib", "classicsonline.properties");
		logger = LogUtils.getLogger(this);
		try {
			FileInputStream inStream = new FileInputStream(loc);
			props.load(inStream);
		} catch (IOException e) {
			// e.printStackTrace();
			props.setProperty(APPURL_KEY, "http://www.kallisti.net.nz/EMusicJ");
			props.setProperty(MANUALURL_KEY,
					"http://www.kallisti.net.nz/EMusicJ/UserManual");
			props.setProperty(UPDATEURL_KEY,
					"http://www.kallisti.net.nz/~robin/emusicj-version.txt");
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

	public URL getToolbarIconClickURL() {
		try {
			return new URL(props.getProperty(TOOLBARICONDEST_KEY));
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Unable to load " + TOOLBARICONDEST_KEY
					+ " URL", e);
		}
		return null;
	}

	public URL getToolbarIconSourceURL() {
		try {
			return new URL(props.getProperty(TOOLBARICONSOURCE_KEY));
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Unable to load " + TOOLBARICONSOURCE_KEY
					+ " URL", e);
		}
		return null;
	}

	public URL getCustomerSupportURL() {
		try {
			return new URL(props.getProperty(CUSTOMERSUPPORTURL_KEY));
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Unable to load "
					+ CUSTOMERSUPPORTURL_KEY + " URL", e);
		}
		return null;
	}

}
