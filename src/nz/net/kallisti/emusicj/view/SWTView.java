package nz.net.kallisti.emusicj.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
    		buildMenuBar(shell);
    		shell.pack();
    		shell.open();
    	}
    }

    /**
     * Builds the menu bar for the application
	 * @param shell the shell to display it on
	 */
	private void buildMenuBar(Shell shell) {
		// Generalise this gunk into a few nice methods
		
		Menu bar = new Menu (shell, SWT.BAR);
		shell.setMenuBar (bar);
		MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
		fileItem.setText ("File");
		Menu fileMenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu (fileMenu);
		
		MenuItem open = new MenuItem(fileMenu, SWT.PUSH);
		open.setText("&Open...");
		
		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem quit = new MenuItem(fileMenu, SWT.PUSH);
		quit.setText("&Quit\tCtrl+Q");
		quit.setAccelerator (SWT.CTRL + 'Q');
		quit.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				userSelected("quit");
			}
		});
	}

	/**
	 * @param command
	 */
	protected void userSelected(String command) {
				
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
