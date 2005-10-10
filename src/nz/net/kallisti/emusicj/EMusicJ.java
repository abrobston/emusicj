package nz.net.kallisti.emusicj;

import nz.net.kallisti.emusicj.controller.EMusicController;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.view.IEMusicView;
import nz.net.kallisti.emusicj.view.SWTView;

/**
 * <p>This is the main class for the eMusic/J downloader. It doesn't do a
 * whole lot except for start the other parts of the system going. This
 * involves creating an instance of the controller, and giving it a view
 * to use.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class EMusicJ {

    /**
     * Initialises the components of the system.
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        IEMusicView view = new SWTView();
        view.setState(IEMusicView.ViewState.STARTUP);
        IEMusicController controller = new EMusicController();
        controller.setView(view);
        controller.run(args);
    }

}
