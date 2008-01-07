package nz.net.kallisti.emusicj.mac;

import nz.net.kallisti.emusicj.ReggaeCountry;
import nz.net.kallisti.emusicj.controller.IEMusicController;

/**
 * <p>
 * This is a mac loader for the ReggaeCountry varient of the application. The
 * purpose of this loader is to hook in the mac open document handler to the
 * program. This should be the entry point when running on OSX
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ReggaeCountryMac extends ReggaeCountry {

	public ReggaeCountryMac(String[] args) {
		super(args);
	}

	@Override
	public void startApp(IEMusicController controller, String[] args) {
		// put the opendoc hooks in
		new OpenDocHandler(controller);
		super.startApp(controller, args);
	}

	public static void main(String[] args) {
		new ReggaeCountryMac(args);
	}

}
