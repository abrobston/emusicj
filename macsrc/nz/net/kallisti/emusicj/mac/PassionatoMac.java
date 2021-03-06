package nz.net.kallisti.emusicj.mac;

import nz.net.kallisti.emusicj.Passionato;
import nz.net.kallisti.emusicj.controller.IEmusicjController;

/**
 * <p>
 * This is a mac loader for the Passionato variant of the application. The
 * purpose of this loader is to hook in the mac open document handler to the
 * program. This should be the entry point when running on OSX
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class PassionatoMac extends Passionato {

	public PassionatoMac(String[] args) {
		super(args);
	}

	@Override
	public void startApp(IEmusicjController controller, String[] args) {
		// put the opendoc hooks in
		new OpenDocHandler(controller);
		super.startApp(controller, args);
	}

	public static void main(String[] args) {
		new PassionatoMac(args);
	}

}
