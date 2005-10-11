package nz.net.kallisti.emusicj.view;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
public class SWTView implements IEMusicView, IDownloadsModelListener {

    private Display display;
	private Shell shell;
	private IEMusicController controller;
	private IDownloadsModel downloadsModel;
	private List downloadsList;
	private ViewState state;

	public SWTView() {
        super();
    }

    public void setState(ViewState state) {
    	this.state = state;
    	if (state.equals(ViewState.STARTUP)) {
    		// do a splashscreen or something
    	} else if (state.equals(ViewState.RUNNING)) {
    		display =new Display();
    		shell =new Shell(display);
    		shell.setText(Constants.APPNAME);
    		buildMenuBar(shell);
    		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
    		shell.setLayout(fillLayout);
    		buildInterface(shell);
    		updateListFromModel();
    		shell.pack();
            shell.setSize (200, 200);
    		shell.open();
    	}
    }

    /**
	 * Updates the list view to ensure that it reflects the model.
	 */
	private void updateListFromModel() {
		if (downloadsModel == null || state != ViewState.RUNNING)
			return;
		final java.util.List<IDownloadMonitor> downloads = 
			downloadsModel.getDownloadMonitors();
		display.asyncExec (new Runnable () {
			public void run () {
				if (!shell.isDisposed()) {
					// This isn't very nice - eventually do it smart
					downloadsList.removeAll();
					for (IDownloadMonitor dm : downloads) {
						downloadsList.add(dm.getName());
					}
				}					
			}
		});
	}

	/**
     * Builds the interface. This consists of a top list view containing each
     * of the download states. Eventually it will also have a lower panel
     * that shows info on the selected item.
	 * @param shell the shell to build the interface on
	 */
	private void buildInterface(Shell shell) {
		downloadsList = new List(shell, SWT.MULTI | SWT.V_SCROLL);		
	}

	/**
     * Builds the menu bar for the application
	 * @param shell the shell to display it on
	 */
	private void buildMenuBar(Shell shell) {
		Menu bar = new Menu (shell, SWT.BAR);
		shell.setMenuBar (bar);
        // --- File menu ---
		Menu fileMenu = SWTUtils.createDropDown(shell, bar, "&File");
		SWTUtils.createMenuItem(fileMenu, "&Open...", SWT.CTRL+'O', this, 
                "openFile");        
		new MenuItem(fileMenu, SWT.SEPARATOR);
		SWTUtils.createMenuItem(fileMenu, "&Quit", SWT.CTRL+'Q', this, 
                "quitProgram");
        // --- Downloads menu ---
        Menu downloadsMenu = SWTUtils.createDropDown(shell, bar, "&Downloads");
        SWTUtils.createMenuItem(downloadsMenu, "&Pause downloads", SWT.CTRL+'P', 
                this, "pauseDownloads");
        SWTUtils.createMenuItem(downloadsMenu, "&Resume downloads", SWT.CTRL+'R', 
                this, "resumeDownloads");
        // --- Help menu ---
        Menu aboutMenu = SWTUtils.createDropDown(shell, bar, "&Help");
        SWTUtils.createMenuItem(aboutMenu, "&About...", null, 
                this, "aboutBox");
	}

    public void openFile() {
    	FileDialog dialog = new FileDialog (shell, SWT.OPEN);
    	dialog.setFilterNames (new String [] {"All Files (*.*)"});
    	dialog.setFilterExtensions (new String [] {"*.*"}); 
    	String file = dialog.open();
    	if (file != null)
    		controller.loadMetafile(file);
    }
    
	public void quitProgram() {
	    shell.dispose();
	}
    
    public void aboutBox() {
        MessageBox about = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
        about.setText("About this program");
        about.setMessage("This is version "+Constants.VERSION+" of "+
                Constants.APPNAME+"\nWritten by Robin Sheat <robin@kallisti.net.nz>\n"+
                "This program downloads music bought from eMusic <http://www.emusic.com>");
        about.open();
    }
    
	public void processEvents(IEMusicController controller) {
	      while (!shell.isDisposed()){
 	         if (!display.readAndDispatch()){
 	            display.sleep();
 	         }
 	      }
 	      display.dispose();
    }

	public void setController(IEMusicController controller) {
		this.controller = controller;
	}

	public void setDownloadsModel(IDownloadsModel model) {
		this.downloadsModel = model;
		if (model != null)
			model.addListener(this);
	}

	public void downloadsListenerChanged(IDownloadsModel model) {
		// TODO update the display to reflect the model state
		// must use asyncExec or similar
		assert model == downloadsModel : "Received event for unknown IDownloadsModel";
	}

}
