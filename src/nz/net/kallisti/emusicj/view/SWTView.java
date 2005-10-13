package nz.net.kallisti.emusicj.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;
import nz.net.kallisti.emusicj.view.swtwidgets.DownloadDisplay;
import nz.net.kallisti.emusicj.view.swtwidgets.SelectableComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

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

    private static Display display;
	private Shell shell;
	private IEMusicController controller;
	private IDownloadsModel downloadsModel;
	private ScrolledComposite downloadsList;
	private ViewState state;
    private ArrayList<IDownloadMonitor> dlMonitors = 
        new ArrayList<IDownloadMonitor>();
    private ArrayList<DownloadDisplay> dlDisplays =
        new ArrayList<DownloadDisplay>();
    private SelectableComposite downloadsListComp;

	public SWTView() {
        super();
    }

    public void setState(ViewState state) {
    	this.state = state;
    	if (state.equals(ViewState.STARTUP)) {
    		// TODO do a splashscreen or something
    	} else if (state.equals(ViewState.RUNNING)) {
    		display =new Display();
    		shell =new Shell(display);
    		shell.setText(Constants.APPNAME);
    		buildMenuBar(shell);
    		buildInterface(shell);
    		updateListFromModel();
    		shell.pack();
            shell.setSize (400, 400);
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
        // We need to compare this new set of download monitors, and work out
        // what has been added and what has been removed.
        Set<IDownloadMonitor> origMons = new HashSet<IDownloadMonitor>(dlMonitors);
        Set<IDownloadMonitor> currMons = new HashSet<IDownloadMonitor>(downloads);
        final Set<IDownloadMonitor> addedMons = new HashSet<IDownloadMonitor>(currMons);
        for (IDownloadMonitor mon : origMons)
            addedMons.remove(mon);
        final Set<IDownloadMonitor> removedMons = new HashSet<IDownloadMonitor>(origMons);
        for (IDownloadMonitor mon : currMons)
            removedMons.remove(mon);
        dlMonitors = new ArrayList<IDownloadMonitor>(downloads);
        display.asyncExec (new Runnable () {
			public void run () {
				if (!shell.isDisposed()) {
				    // Now go through the list of DownloadDisplays we have, and
                    // if its monitor is in the removed list, dispose it
                    for (DownloadDisplay dd : dlDisplays) {
                        if (removedMons.contains(dd.getMonitor())) {
                            dd.dispose();
                            downloadsListComp.removeSelectableControl(dd);
                        }
                    }
                    // Now go through the list of downloads, and if one of these
                    // is in addedMons, we add it (done this way to keep the order
                    // correct)
                    for (IDownloadMonitor mon : downloads) 
                        if (addedMons.contains(mon)) {
                            DownloadDisplay disp = new DownloadDisplay(
                                    downloadsListComp, SWT.NONE);
                            downloadsListComp.addSelectableControl(disp);
                            disp.setLayoutData(new GridData(SWT.FILL, 
                            		SWT.BEGINNING, true, false));
                            disp.setDownloadMonitor(mon);
                            dlDisplays.add(disp);
                        }
                    downloadsListComp.pack();
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
		GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		shell.setLayout(shellLayout);
		ToolBar toolBar = new ToolBar (shell, SWT.FLAT | SWT.BORDER);
		// --Testing--
		Image runIconImg = new Image(display, 
				SWTView.class.getResourceAsStream("start.gif"));
		ToolItem item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage(runIconImg);
		Image pauseIconImg = new Image(display, 
				SWTView.class.getResourceAsStream("pause.gif"));
		item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage(pauseIconImg);
		Image cancelIconImg = new Image(display, 
				SWTView.class.getResourceAsStream("cancel.gif"));
		item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage(cancelIconImg);
		
		toolBar.pack ();
		// --End testing--
		
		downloadsList = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.BORDER);
        downloadsList.setExpandHorizontal(true);
        downloadsListComp = new SelectableComposite(downloadsList, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        downloadsListComp.setLayout(layout);
        downloadsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, 
        		true, true));
        downloadsList.setContent(downloadsListComp);
        //downloadsListComp.layout();
        //shell.layout();
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
 	      // Tell the DownloadDisplay instances to finish up
 	      for (DownloadDisplay disp : dlDisplays) {
 	    	  disp.stop();
 	      }
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
        updateListFromModel();
	}

    /**
     * A handy util so that the display object down't have to be passed 
     * everywhere.
     * @param runner the Runnable to invoke
     */
    public static void asyncExec(Runnable runner) {
        display.asyncExec(runner);
    }

	/**
	 * Gets the system color for the supplied constant from the display
	 * @param color the SWT constant corresponding to the colour we want
	 * @return a Color object of that colour
	 */
	public static Color getSystemColor(int color) {
		return display.getSystemColor(color);
	}
    
}
