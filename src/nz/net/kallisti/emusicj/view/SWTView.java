package nz.net.kallisti.emusicj.view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.DownloadMonitor;

/**
 * <p>This is the main class for providing the user interface. It uses SWT to
 * do it, and interacts with the controller to receive updates on the system
 * state and to notify it of user requests. It doesn't actually change any
 * state itself.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class SWTView implements IEMusicView {

	
    private Display display;
	private Shell shell;

	public SWTView() {
        super();
    }

    public void setState(ViewState state) {
    	if (state.equals(ViewState.STARTUP)) {
    		// do a splashscreen or something
    	} else if (state.equals(ViewState.RUNNING)) {
    		display =new Display();
    		shell =new Shell(display);
    		shell.setText("eMusic/J");
    		shell.pack();
    		shell.open();
    	}
    }

    public void processEvents(IEMusicController controller) {
	      while (!shell.isDisposed()){
 	         if (!display.readAndDispatch()){
 	            display.sleep();
 	         }
 	      }
 	      display.dispose();
    }

    public void addDownload(DownloadMonitor dm) {
        // TODO Auto-generated method stub
        
    }

    public void removeDownload(DownloadMonitor dm) {
        // TODO Auto-generated method stub
        
    }

}
