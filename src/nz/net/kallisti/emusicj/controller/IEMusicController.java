package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.view.IEMusicView;

/**
 * <p>This interface defines the functions that need to be provided by a
 * controller. The purpose of the controller is to tell the view what to
 * display, and update the state of the system in response to what the
 * user inputs (typically done via the view also)</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public interface IEMusicController {

    /**
     * Ties a view in to the controller. If the view isn't present, then the
     * controller should do something sensible, such as not try to talk to it,
     * abort on a call to run(), or run in some form of headless mode.
     * @param view the view used to interact with the user
     */
    public void setView(IEMusicView view);

    /**
     * This starts the controller running. It will perform any initialisation 
     * tasks that need to be done, and then pass control to the event loop
     * of the view (or it may do that in a seperate thread if it needs to).
     * This will only return when the program is to shut down. 
     */
    public void run();
    
}
