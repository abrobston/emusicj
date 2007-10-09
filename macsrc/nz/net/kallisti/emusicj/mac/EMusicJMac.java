package nz.net.kallisti.emusicj.mac;

import nz.net.kallisti.emusicj.EMusicJ;
import nz.net.kallisti.emusicj.controller.IEMusicController;

/**
 * <p>This is a mac loader for the eMusic/J varient of the application. The
 * purpose of this loader is to hook in the mac open document handler to
 * the program. This should be the entry point when running on OSX</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class EMusicJMac extends EMusicJ {

	public EMusicJMac(String[] args) {
		super(args);
	}

	public void startApp(IEMusicController controller, String[] args) {
		// put the opendoc hooks in
		new OpenDocHandler(controller);
		super.startApp(controller, args);
	}
	
	public static void main(String[] args) {
		new EMusicJMac(args);
	}

}
