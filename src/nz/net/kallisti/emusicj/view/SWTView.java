package nz.net.kallisti.emusicj.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.controller.Preferences;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;
import nz.net.kallisti.emusicj.view.swtwidgets.AboutDialogue;
import nz.net.kallisti.emusicj.view.swtwidgets.DownloadDisplay;
import nz.net.kallisti.emusicj.view.swtwidgets.PreferencesDialogue;
import nz.net.kallisti.emusicj.view.swtwidgets.SelectableComposite;
import nz.net.kallisti.emusicj.view.swtwidgets.UpdateDialogue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
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
public class SWTView implements IEMusicView, IDownloadsModelListener, 
SelectionListener, ControlListener {
	
	private static Display display;
    private static Clipboard clipboard;
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
	private ToolItem runButton;
	private ToolItem pauseButton;
	private ToolItem cancelButton;
	private Preferences prefs = Preferences.getInstance();
	private boolean running = false;
	private String update;
	private Rectangle windowLoc;
	
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
			try {
				int x = Integer.parseInt(prefs.getProperty("winLocX"));
				int y = Integer.parseInt(prefs.getProperty("winLocY"));
				int height = Integer.parseInt(prefs.getProperty("winHeight"));
				int width = Integer.parseInt(prefs.getProperty("winWidth"));
				Rectangle r = new Rectangle(x, y, width, height);
				shell.setBounds(r);
			} catch (Exception e) {
				shell.setSize (400, 400);
			}
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
						if (removedMons.contains(dd.getDownloadMonitor())) {
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
							mon.addStateListener(new IDownloadMonitorListener() {
								public void monitorStateChanged(IDownloadMonitor monitor) {
									setButtonsState();
								}
							});
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
		buildToolBar(toolBar);
		
		downloadsList = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.BORDER);
		downloadsList.setExpandHorizontal(true);
		downloadsListComp = new SelectableComposite(downloadsList, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		downloadsListComp.setLayout(layout);
		downloadsListComp.addSelectionListener(this);
		downloadsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, 
				true, true));
		downloadsList.setContent(downloadsListComp);
		setButtonsState();
	}
	
	/**
	 * Builds the toolbar contents and events
	 * @param toolBar the toolbar to add stuff to
	 */
	private void buildToolBar(ToolBar toolBar) {
		final Image runIconImg = new Image(display, 
				SWTView.class.getResourceAsStream("start.png"));
		runButton = new ToolItem (toolBar, SWT.PUSH);
		runButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				runIconImg.dispose();
			}
		});
		runButton.setImage(runIconImg);
		runButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				runSelectedDownload();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				runSelectedDownload();				
			}
		});
		
		final Image pauseIconImg = new Image(display, 
				SWTView.class.getResourceAsStream("pause.png"));
		pauseButton = new ToolItem (toolBar, SWT.PUSH);
		pauseButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				pauseIconImg.dispose();
			}
		});
		pauseButton.setImage(pauseIconImg);
		pauseButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				pauseSelectedDownload();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				pauseSelectedDownload();				
			}
		});
		
		final Image cancelIconImg = new Image(display, 
				SWTView.class.getResourceAsStream("cancel.png"));
		cancelButton = new ToolItem (toolBar, SWT.PUSH);
		cancelButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				cancelIconImg.dispose();
			}
		});
		cancelButton.setImage(cancelIconImg);
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				cancelSelectedDownload();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				cancelSelectedDownload();				
			}
		});
		
		toolBar.pack ();
	}
	
	protected void cancelSelectedDownload() {
		if (downloadsListComp == null || 
				downloadsListComp.getSelectedControl() == null)
			return;
		IDownloader dl = 
			((DownloadDisplay)downloadsListComp.getSelectedControl()).
			getDownloadMonitor().getDownloader();
		controller.stopDownload(dl);
		setButtonsState();
	}
	
	protected void pauseSelectedDownload() {
		if (downloadsListComp == null || 
				downloadsListComp.getSelectedControl() == null)
			return;
		IDownloader dl = 
			((DownloadDisplay)downloadsListComp.getSelectedControl()).
			getDownloadMonitor().getDownloader();
		controller.pauseDownload(dl);		
		setButtonsState();
	}
	
	protected void runSelectedDownload() {
		if (downloadsListComp == null || 
				downloadsListComp.getSelectedControl() == null)
			return;
		IDownloader dl = 
			((DownloadDisplay)downloadsListComp.getSelectedControl()).
			getDownloadMonitor().getDownloader();
		controller.startDownload(dl);
		setButtonsState();
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
		SWTUtils.createMenuItem(fileMenu, "&Open...\tCtrl-O", SWT.CTRL+'O', this, 
		"openFile");        
		new MenuItem(fileMenu, SWT.SEPARATOR);
		SWTUtils.createMenuItem(fileMenu, "&Quit\tCtrl-Q", SWT.CTRL+'Q', this, 
		"quitProgram");
		// --- Downloads menu ---
		Menu downloadsMenu = SWTUtils.createDropDown(shell, bar, "&Downloads");
		SWTUtils.createMenuItem(downloadsMenu, "&Pause downloads\tCtrl-P", SWT.CTRL+'P', 
				this, "pauseDownloads");
		SWTUtils.createMenuItem(downloadsMenu, "&Resume downloads\tCtrl-R", SWT.CTRL+'R', 
				this, "resumeDownloads");
		new MenuItem(downloadsMenu, SWT.SEPARATOR);
		SWTUtils.createMenuItem(downloadsMenu, "&Clean up downloads\tCtrl-C", SWT.CTRL+'C', 
				this, "cleanUpDownloads");
		// --- Settings menu
		Menu settingsMenu = SWTUtils.createDropDown(shell, bar, "&Settings");
		SWTUtils.createMenuItem(settingsMenu, "&Preferences...", SWT.NONE, 
				this, "displayPreferences");        
		// --- Help menu ---
		Menu aboutMenu = SWTUtils.createDropDown(shell, bar, "&Help");
		SWTUtils.createMenuItem(aboutMenu, "&About...", null, 
				this, "aboutBox");
	}
	
	/**
	 * Tell the controller to pause all the current downloads, and not
	 * start any more.
	 */
	public void pauseDownloads() {
		controller.pauseDownloads();
	}
	
	/**
	 * Tell the controller to restart all paused downloads, and allow more
	 * to be automatically started.
	 */
	public void resumeDownloads() {
		controller.resumeDownloads();
	}
	
	/**
	 * Tell the controller to remove any stopped or finished downloads from
	 * the model
	 */
	public void cleanUpDownloads() {
		controller.removeDownloads(DLState.FINISHED);
		controller.removeDownloads(DLState.STOPPED);
		//controller.removeDownloads(DLState.FAILED);
	}
	
	/**
	 * Brings up the preferences dialogue
	 */
	public void displayPreferences() {
		PreferencesDialogue prefs = new PreferencesDialogue(shell,
				Preferences.getInstance());
		prefs.open();
	}
	
	/**
	 * Enables and disables the buttons depending on the state of the selected
	 * download 
	 */
	private void setButtonsState() {
		if (display.isDisposed())
			return;
		asyncExec(new Runnable() {
			public void run() {
				if (downloadsListComp == null)
					return;
				if (runButton.isDisposed() || pauseButton.isDisposed() ||
						cancelButton.isDisposed())
					return;
				if (downloadsListComp.getSelectedControl() == null) {
					runButton.setEnabled(false);
					pauseButton.setEnabled(false);
					cancelButton.setEnabled(false);
					return;
				}
				IDownloadMonitor mon = 
					((DownloadDisplay)downloadsListComp.getSelectedControl()).
					getDownloadMonitor();
				if (mon.getDownloadState() == DLState.CONNECTING ||
						mon.getDownloadState() == DLState.DOWNLOADING) {
					runButton.setEnabled(false);
					pauseButton.setEnabled(true);
					cancelButton.setEnabled(true);			
				} else if (mon.getDownloadState() == DLState.FINISHED) {
					runButton.setEnabled(false);
					pauseButton.setEnabled(false);
					cancelButton.setEnabled(false);                                         
				} else {
					runButton.setEnabled(true);
					pauseButton.setEnabled(false);
					cancelButton.setEnabled(true);						
				}
			}
		});
	}
	
	public void openFile() {
		FileDialog dialog = new FileDialog (shell, SWT.OPEN | SWT.MULTI);
		dialog.setFilterNames (new String [] {"All Files (*.*)"});
		dialog.setFilterExtensions (new String [] {"*.*"});
		dialog.setFilterPath(prefs.getProperty("openDefaultPath"));
		String file = dialog.open();
		if (file != null) {
			prefs.setProperty("openDefaultPath", file);
			controller.loadMetafile(dialog.getFilterPath(),dialog.getFileNames());
		}
	}
	
	public void quitProgram() {
		shell.dispose();
	}
	
	public void aboutBox() {
		AboutDialogue about = new AboutDialogue(shell);
		about.open();
	}
	
	public void processEvents(IEMusicController controller) {
		try {
			shell.addControlListener(this);
			running  = true;
			windowMovedOrResized();
			if (update != null)
				updateAvailable(update);
			while (!shell.isDisposed()){
				if (!display.readAndDispatch()){
					display.sleep();
				}
			}
			saveWindowLocation();
			display.dispose();
			// Tell the DownloadDisplay instances to finish up
		} catch (SWTException e) {
			// If a GUI error occurs hopefully we can shut down somewhat gracefully
			System.err.println("A GUI error occurred. Shutting down.");
			e.printStackTrace();
		}
		for (DownloadDisplay disp : dlDisplays) {
			disp.stop();
		}
	}

	private void saveWindowLocation() {
		prefs.setProperty("winLocX",windowLoc.x+"");
		prefs.setProperty("winLocY",windowLoc.y+"");
		prefs.setProperty("winHeight",windowLoc.height+"");
		prefs.setProperty("winWidth",windowLoc.width+"");
		prefs.save();
	}
	
	public void setController(IEMusicController controller) {
		this.controller = controller;
	}
	
	public void setDownloadsModel(IDownloadsModel model) {
		if (model != downloadsModel && downloadsModel != null)
			downloadsModel.removeListener(this);
		this.downloadsModel = model;
		if (model != null)
			model.addListener(this);
	}
	
	public void downloadsModelChanged(IDownloadsModel model) {
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
	
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == downloadsListComp)
			setButtonsState();
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		if (e.widget == downloadsListComp)
			setButtonsState();
	}
	
	public void error(final String msgTitle, final String msg) {
		if (!running) // TODO queue up the errors for later.
			return;
		asyncExec(new Runnable() {
			public void run() {
				MessageBox about = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				about.setText(msgTitle);
				about.setMessage(msg);
				about.open();
			}
		});
	}


	public void updateAvailable(final String newVersion) {
		if (!running) {
			update = newVersion;
		} else {
			asyncExec(new Runnable() {
				public void run() {
					UpdateDialogue dialogue = new UpdateDialogue(shell, newVersion);
					dialogue.open();
				}
			});
		}
	}

    /**
     * @return
     */
    public static synchronized Clipboard getClipboard() {
        if (clipboard == null) 
            clipboard = new Clipboard(display);
        return clipboard;
    }

	public void windowMovedOrResized() {
		windowLoc = shell.getBounds();
	}

	public void controlMoved(ControlEvent e) {
		windowMovedOrResized();
	}

	public void controlResized(ControlEvent e) {
		windowMovedOrResized();
	}
	
}
