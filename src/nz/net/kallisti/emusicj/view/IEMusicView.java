package nz.net.kallisti.emusicj.view;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.models.IDownloadsModel;

public interface IEMusicView {

	/**
	 * <p>These states are used to tell the view the large-scale progress of the 
	 * system so that it can report them to the user, and change how it looks
	 * accordingly (e.g. by providing a splash screen)</p>
	 * <ul><li><code>STARTUP</code> indicates the program is initialising</li>
	 * <li><code>RUNNING</code> indicates the program is in its normal running
	 * mode</li></ul>
	 */
	enum ViewState { STARTUP, RUNNING }
    
    /**
     * Sets the state that the view should run in. This can be used to provide
     * a spashscreen or something. If STATE_STARTUP is set, then a startup
     * screen may be activated. When it is set to STATE_RUNNING, that will be
     * removed, and the standard interface will be put up.
     * @param s the new state for the view, one of the constants with STATE_
     * as the prefix.
     */
    public void setState(ViewState state);

    /**
     * This runs the event loop of the view. This should recieve events from
     * the user, and pass the reqests on to the controller. It will only
     * return when the user requests the application quit.
     * @param controller the controller to pass events on to
     */
    public void processEvents(IEMusicController controller);
    
    /**
     * A view has a downloads model to keep track of what downloads are in the
     * system. This tells the view the model to watch.
     * @param model the model that the view will use
     */
    public void setDownloadsModel(IDownloadsModel model);
    
    /**
	 * Sets the controller instance that the view should communicate with. 
	 * If this isn't set by the time the view needs to talk to the controller,
	 * <code>NullPointerExceptions</code> will be generated.
	 * @param controller the controller instance the view talks to
	 */
	public void setController(IEMusicController controller);

}
