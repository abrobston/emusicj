package nz.net.kallisti.emusicj.view;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.DownloadMonitor;

public interface IEMusicView {

    /** Indicate that the program is starting up */
    int STATE_STARTUP = 1;
    /** Indicate that the program has started and is now operational */
    int STATE_RUNNING = 2;
    
    /**
     * Sets the state that the view should run in. This can be used to provide
     * a spashscreen or something. If STATE_STARTUP is set, then a startup
     * screen may be activated. When it is set to STATE_RUNNING, that will be
     * removed, and the standard interface will be put up.
     * @param s the new state for the view, one of the constants with STATE_
     * as the prefix.
     */
    public void setState(int s);

    /**
     * This runs the event loop of the view. This should recieve events from
     * the user, and pass the reqests on to the controller. It will only
     * return when the user requests the application quit.
     * @param controller the controller to pass events on to
     */
    public void processEvents(IEMusicController controller);
    
    public void addDownload(DownloadMonitor dm);
    
    public void removeDownload(DownloadMonitor dm);

}
