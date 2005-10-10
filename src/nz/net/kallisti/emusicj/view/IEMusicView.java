package nz.net.kallisti.emusicj.view;

public interface IEMusicView {

    int STATE_STARTUP = 1;
    int STATE_RUNNING = 2;
    
    /**
     * Sets the state that the view should run in. This can be used to provide
     * a spashscreen or something. If STATE_STARTUP is set, then a startup
     * screen may be activated. When it is set to STATE_RUNNING, that will be
     * removed, and the standard interface will be put up.
     * @param s
     */
    public void setState(int s); 

}
