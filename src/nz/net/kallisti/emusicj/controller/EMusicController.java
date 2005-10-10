package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.view.IEMusicView;

/**
 * <p>This is the main controller for the application. It routes stuff around,
 * ensuring that the view is kept up to date with the system, and that the
 * state is kept up to date with user requests.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class EMusicController implements IEMusicController {

    private IEMusicView view;

    public EMusicController() {
        super();
    }

    public void setView(IEMusicView view) {
        this.view = view;
    }

    public void run() {
        // Initialise the system
        view.setState(IEMusicView.STATE_RUNNING);
        // Pass the system state on to the view to ensure it's up to date
        // Call the view's event loop
        view.processEvents(this);
        // Clean up the program
    }

}
