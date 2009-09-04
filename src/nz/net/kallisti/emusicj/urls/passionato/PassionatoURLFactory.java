package nz.net.kallisti.emusicj.urls.passionato;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.urls.AbstractURLFactory;
import nz.net.kallisti.emusicj.urls.IDynamicURL;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Provides the URLs for the Passionato DLM
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class PassionatoURLFactory extends AbstractURLFactory {

	private final Logger logger;

	@Inject
	public PassionatoURLFactory(Provider<IDynamicURL> dynUrlProvider) {
		super(dynUrlProvider);
		logger = LogUtils.getLogger(this);
	}

	public URL getAppURL() {
		try {
			return new URL("http://www.passionato.com/");
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Can't create app URL", e);
			return null;
		}
	}

	public URL getManualURL() {
		try {
			return new URL("http://www.kallisti.net.nz/EMusicJ/UserManual");
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Can't create manual URL", e);
		}
		return null;
	}

	public URL getUpdateURL() {
		try {
			return new URL("http://www.passionato.com/update");
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Can't create update URL", e);
		}
		return null;
	}

	public URL getToolbarIconClickURL() {
		try {
			return new URL("http://www.passionato.com");
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
