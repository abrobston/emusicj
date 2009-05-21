/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */
package nz.net.kallisti.emusicj.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.misc.BrowserLauncher;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;
import nz.net.kallisti.emusicj.network.http.proxy.ProxyCredentialsProvider.CredsCallback;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.AboutDialogue;
import nz.net.kallisti.emusicj.view.swtwidgets.DownloadDisplay;
import nz.net.kallisti.emusicj.view.swtwidgets.FileInfoPanel;
import nz.net.kallisti.emusicj.view.swtwidgets.PreferencesDialogue;
import nz.net.kallisti.emusicj.view.swtwidgets.ProxyDialogue;
import nz.net.kallisti.emusicj.view.swtwidgets.SelectableComposite;
import nz.net.kallisti.emusicj.view.swtwidgets.StatusLine;
import nz.net.kallisti.emusicj.view.swtwidgets.SystemTrayManager;
import nz.net.kallisti.emusicj.view.swtwidgets.UpdateDialogue;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.DynamicImage;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.apache.commons.httpclient.auth.AuthScheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tray;

import com.google.inject.Inject;

/**
 * <p>
 * This is the main class for providing the user interface. It uses SWT to do
 * it, and interacts with the controller to receive updates on the system state
 * and to notify it of user requests. It doesn't actually change any state
 * itself.
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class SWTView implements IEMusicView, IDownloadsModelListener,
		SelectionListener, ControlListener {

	private static Display display;
	private static Clipboard clipboard;
	private Shell shell;
	private final IEMusicController controller;
	private IDownloadsModel downloadsModel;
	private ScrolledComposite downloadsList;
	private ViewState state;
	private ArrayList<IDownloadMonitor> dlMonitors = new ArrayList<IDownloadMonitor>();
	private final ArrayList<DownloadDisplay> dlDisplays = new ArrayList<DownloadDisplay>();
	private SelectableComposite downloadsListComp;
	private ToolItem pauseResumeButton;
	private final IPreferences prefs;
	private boolean running = false;
	private Rectangle windowLoc;
	private FileInfoPanel fileInfo;
	private SashForm mainArea;
	private int sashTop = 50;
	private int sashBottom = 50;
	private SystemTrayManager sysTray;
	private Image downloadingIcon;
	private Image notDownloadingIcon;
	private final ArrayList<Runnable> deferredList = new ArrayList<Runnable>();
	private StatusLine statusLine;
	private boolean pausedState = false;
	private MenuItem pauseSysTrayMenuItem;
	private final IStrings strings;
	private final IImageFactory imageFactory;
	private final IURLFactory urlFactory;

	@Inject
	public SWTView(IPreferences prefs, IStrings strings,
			IEMusicController controller, IImageFactory imageFactory,
			IURLFactory urlFactory) {
		super();
		this.prefs = prefs;
		this.strings = strings;
		this.controller = controller;
		this.imageFactory = imageFactory;
		this.urlFactory = urlFactory;
	}

	public void setState(ViewState state) {
		this.state = state;
		if (state.equals(ViewState.STARTUP)) {
			// TODO do a splashscreen or something
		} else if (state.equals(ViewState.RUNNING)) {
			display = new Display();
			imageFactory.setDisplay(display);
			imageFactory.setCacheDir(prefs.getIconCacheDir());
			shell = new Shell(display);
			shell.setText(strings.getAppName());
			buildMenuBar(shell);
			int topAmount = 60;
			int bottomAmount = 40;
			try {
				topAmount = Integer.parseInt(prefs.getProperty("topRatio"));
				bottomAmount = Integer.parseInt(prefs
						.getProperty("bottomRatio"));
			} catch (Exception e) {
			}
			buildInterface(shell, topAmount, bottomAmount);
			updateListFromModel();
			shell.layout();
			shell.pack();
			try {
				int x = Integer.parseInt(prefs.getProperty("winLocX"));
				int y = Integer.parseInt(prefs.getProperty("winLocY"));
				int height = Integer.parseInt(prefs.getProperty("winHeight"));
				int width = Integer.parseInt(prefs.getProperty("winWidth"));
				Rectangle r = new Rectangle(x, y, width, height);
				shell.setBounds(r);
			} catch (Exception e) {
				shell.setSize(400, 400);
			}
			shell.open();
			deferViewEvent(new Runnable() {
				public void run() {
					if (prefs.isFirstLaunch()) {
						displayPreferences();
					}
				}
			});
		}
	}

	/**
	 * Updates the list view to ensure that it reflects the model.
	 */
	private void updateListFromModel() {
		if (downloadsModel == null || state != ViewState.RUNNING)
			return;
		final java.util.List<IDownloadMonitor> downloads = downloadsModel
				.getDownloadMonitors();
		// We need to compare this new set of download monitors, and work out
		// what has been added and what has been removed.
		Set<IDownloadMonitor> origMons = new HashSet<IDownloadMonitor>(
				dlMonitors);
		Set<IDownloadMonitor> currMons = new HashSet<IDownloadMonitor>(
				downloads);
		final Set<IDownloadMonitor> addedMons = new HashSet<IDownloadMonitor>(
				currMons);
		for (IDownloadMonitor mon : origMons)
			addedMons.remove(mon);
		final Set<IDownloadMonitor> removedMons = new HashSet<IDownloadMonitor>(
				origMons);
		for (IDownloadMonitor mon : currMons)
			removedMons.remove(mon);
		dlMonitors = new ArrayList<IDownloadMonitor>(downloads);
		display.asyncExec(new Runnable() {
			public void run() {
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
					// is in addedMons, we add it (done this way to keep the
					// order
					// correct)
					for (IDownloadMonitor mon : downloads)
						if (addedMons.contains(mon)) {
							DownloadDisplay disp = new DownloadDisplay(
									downloadsListComp, SWT.NONE, display);
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
	 * Builds the interface. This consists of a top list view containing each of
	 * the download states, an information area detailing the current selection,
	 * buttons, and a status line.
	 * 
	 * @param shell
	 *            the shell to build the interface on
	 */
	private void buildInterface(Shell shell, int topAmount, int bottomAmount) {
		setAppIcon(shell);
		downloadingIcon = imageFactory.getDownloadingIcon();
		notDownloadingIcon = imageFactory.getNotDownloadingIcon();
		buildSystemTray(this, notDownloadingIcon);
		GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		shell.setLayout(shellLayout);

		// This contains the toolbar, and lets us put an image on the right
		// side
		Composite toolbarRow = new Composite(shell, SWT.NONE);
		// This griddata is for the toolbar row within the shell
		GridData toolbarGridData = new GridData();
		toolbarGridData.grabExcessHorizontalSpace = true;
		toolbarGridData.horizontalAlignment = SWT.FILL;
		toolbarRow.setLayoutData(toolbarGridData);
		GridLayout toolbarLayout = new GridLayout(2, false);
		toolbarRow.setLayout(toolbarLayout);

		ToolBar toolBar = new ToolBar(toolbarRow, SWT.FLAT);
		buildToolBar(toolBar);
		// This griddata is for the components within the toolbar row
		GridData toolbarRowData = new GridData();
		toolbarRowData.horizontalAlignment = SWT.LEFT;
		toolBar.setLayoutData(toolbarRowData);
		DynamicImage toolbarIcon = new DynamicImage(toolbarRow, SWT.NONE,
				display, urlFactory.getToolbarIconClickURL(), imageFactory
						.getApplicationLogoProvider(), this);
		toolbarRowData = new GridData();
		toolbarRowData.grabExcessHorizontalSpace = true;
		toolbarRowData.horizontalAlignment = SWT.RIGHT;
		toolbarRowData.verticalAlignment = SWT.TOP;
		toolbarIcon.setLayoutData(toolbarRowData);

		mainArea = new SashForm(shell, SWT.VERTICAL | SWT.SMOOTH);
		mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		downloadsList = new ScrolledComposite(mainArea, SWT.V_SCROLL
				| SWT.BORDER);
		downloadsList.setExpandHorizontal(true);
		// By default the scroll increments are tiny
		downloadsList.getVerticalBar().setIncrement(10);
		downloadsList.getVerticalBar().setPageIncrement(100);
		downloadsListComp = new SelectableComposite(downloadsList, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		downloadsListComp.setLayout(layout);
		downloadsListComp.addSelectionListener(this);
		downloadsList
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		downloadsList.setContent(downloadsListComp);
		ScrolledComposite fileInfoPlace = new ScrolledComposite(mainArea,
				SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		/*
		 * GridData layoutData = new GridData();
		 * layoutData.grabExcessHorizontalSpace=true;
		 * fileInfo.setLayoutData(layoutData);
		 */
		fileInfoPlace.setExpandHorizontal(true);
		fileInfoPlace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		fileInfo = new FileInfoPanel(fileInfoPlace, SWT.NONE, display);
		fileInfoPlace.setContent(fileInfo);
		mainArea.setWeights(new int[] { topAmount, bottomAmount });
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				windowMovedOrResized();
				event.doit = true;
			}
		});
		statusLine = new StatusLine(shell, SWT.NONE);
	}

	/**
	 * Creates the system tray icon and controller object
	 * 
	 * @param view
	 *            this, i.e. the SWTView that gets the events
	 * @param icon
	 *            the image to use as the system tray icon
	 */
	private void buildSystemTray(SWTView view, Image icon) {
		Tray tray = display.getSystemTray();
		if (tray != null) {
			sysTray = new SystemTrayManager(view, icon, tray, strings
					.getShortAppName());
			Menu menu = sysTray.getMenu();
			SWTUtils.createMenuItem(menu, "Show/Hide", SWT.NONE, view,
					"trayClicked");
			if (!pausedState) {
				pauseSysTrayMenuItem = SWTUtils.createMenuItem(menu,
						"Pause downloads", SWT.NONE, view, "togglePaused");
			} else {
				pauseSysTrayMenuItem = SWTUtils.createMenuItem(menu,
						"Resume downloads", SWT.NONE, view, "togglePaused");
			}
			SWTUtils
					.createMenuItem(menu, "Quit", SWT.NONE, view, "quitProgram");
			sysTray.buildMenu();
		}
	}

	/**
	 * Sets up the application icon stuff
	 * 
	 * @param shell
	 *            the application's shell
	 */
	private void setAppIcon(Shell shell) {
		// shell.setImage(appIcon);
		shell.setImages(imageFactory.getAppIcons());
	}

	/**
	 * Builds the toolbar contents and events
	 * 
	 * @param toolBar
	 *            the toolbar to add stuff to
	 */
	private void buildToolBar(ToolBar toolBar) {
		pauseResumeButton = new ToolItem(toolBar, SWT.CHECK);
		final Image pauseImg = imageFactory.getPauseIcon();
		pauseResumeButton.setImage(pauseImg);
		pauseResumeButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				pauseImg.dispose();
			}
		});
		pauseResumeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				if (pauseResumeButton.getSelection()) {
					pauseDownloads();
				} else {
					resumeDownloads();
				}
			}
		});

		toolBar.pack();
	}

	/**
	 * Builds the menu bar for the application
	 * 
	 * @param shell
	 *            the shell to display it on
	 */
	private void buildMenuBar(Shell shell) {
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		// --- File menu ---
		Menu fileMenu = SWTUtils.createDropDown(shell, bar, "&File");
		SWTUtils.createMenuItem(fileMenu, "&Open...\tCtrl-O", SWT.CTRL + 'O',
				this, "openFile");
		new MenuItem(fileMenu, SWT.SEPARATOR);
		SWTUtils.createMenuItem(fileMenu, "&Quit\tCtrl-Q", SWT.CTRL + 'Q',
				this, "quitProgram");
		// --- Downloads menu ---
		Menu downloadsMenu = SWTUtils.createDropDown(shell, bar, "&Downloads");
		SWTUtils.createMenuItem(downloadsMenu, "&Pause downloads\tCtrl-P",
				SWT.CTRL + 'P', this, "pauseDownloads");
		SWTUtils.createMenuItem(downloadsMenu, "&Resume downloads\tCtrl-R",
				SWT.CTRL + 'R', this, "resumeDownloads");
		SWTUtils.createMenuItem(downloadsMenu, "Cancel &all downloads",
				SWT.NONE, this, "cancelAllDownloads");
		new MenuItem(downloadsMenu, SWT.SEPARATOR);
		SWTUtils.createMenuItem(downloadsMenu, "&Clean up downloads\tCtrl-C",
				SWT.CTRL + 'C', this, "cleanUpDownloads");
		// --- Settings menu
		Menu settingsMenu = SWTUtils.createDropDown(shell, bar, "&Settings");
		SWTUtils.createMenuItem(settingsMenu, "&Preferences...", SWT.NONE,
				this, "displayPreferences");
		// --- Help menu ---
		Menu aboutMenu = SWTUtils.createDropDown(shell, bar, "&Help");
		SWTUtils.createMenuItem(aboutMenu, "&User Manual...", SWT.NONE, this,
				"userManual");
		SWTUtils.createMenuItem(aboutMenu, "&About...", SWT.NONE, this,
				"aboutBox");
	}

	/**
	 * Tell the controller to pause all the current downloads, and not start any
	 * more.
	 */
	public void pauseDownloads() {
		controller.pauseDownloads();
	}

	/**
	 * Tell the controller to restart all paused downloads, and allow more to be
	 * automatically started.
	 */
	public void resumeDownloads() {
		controller.resumeDownloads();
	}

	public void togglePaused() {
		if (pausedState) {
			resumeDownloads();
		} else {
			pauseDownloads();
		}
	}

	/**
	 * Tell the controller to cancel all downloads
	 */
	public void cancelAllDownloads() {
		controller.cancelDownloads();
	}

	/**
	 * Tell the controller to remove any stopped or finished downloads from the
	 * model
	 */
	public void cleanUpDownloads() {
		controller.removeDownloads(DLState.FINISHED);
		controller.removeDownloads(DLState.CANCELLED);
		// controller.removeDownloads(DLState.FAILED);
	}

	/**
	 * Brings up the preferences dialogue
	 */
	public void displayPreferences() {
		PreferencesDialogue prefs = new PreferencesDialogue(shell, this.prefs,
				strings);
		prefs.open();
	}

	public void openFile() {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		dialog.setFilterNames(strings.getOpenDialogueFilterNames());
		dialog.setFilterExtensions(strings.getOpenDialogueFilterExtensions());
		dialog.setFilterPath(prefs.getProperty("openDefaultPath"));
		String file = dialog.open();
		if (file != null) {
			prefs.setProperty("openDefaultPath", file);
			controller.loadMetafile(dialog.getFilterPath(), dialog
					.getFileNames());
		}
	}

	public void quitProgram() {
		shell.dispose();
	}

	public void aboutBox() {
		AboutDialogue about = new AboutDialogue(shell, strings, imageFactory);
		about.open();
	}

	public void userManual() {
		new Thread() {
			@Override
			public void run() {
				try {
					BrowserLauncher.openURL(urlFactory.getManualURL());
				} catch (Exception e) {
					error(
							"Error launching browser",
							"There seemed to be a "
									+ "problem launching the browser. The user manual can"
									+ "be found at "
									+ urlFactory.getManualURL());
				}
			}
		}.start();
	}

	public void processEvents(IEMusicController controller) {
		try {
			shell.addControlListener(this);
			running = true;
			windowMovedOrResized();
			synchronized (deferredList) {
				for (Runnable r : deferredList)
					asyncExec(r);
				deferredList.clear();
			}
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			saveWindowState();
			if (!display.isDisposed())
				display.dispose();
			// Tell the DownloadDisplay instances to finish up
		} catch (SWTException e) {
			// If a GUI error occurs hopefully we can shut down somewhat
			// gracefully
			System.err.println("A GUI error occurred. Shutting down.");
			e.printStackTrace();
		}
		for (DownloadDisplay disp : dlDisplays) {
			disp.stop();
		}
	}

	private void saveWindowState() {
		prefs.setProperty("winLocX", windowLoc.x + "");
		prefs.setProperty("winLocY", windowLoc.y + "");
		prefs.setProperty("winHeight", windowLoc.height + "");
		prefs.setProperty("winWidth", windowLoc.width + "");
		prefs.setProperty("topRatio", sashTop + "");
		prefs.setProperty("bottomRatio", sashBottom + "");
		prefs.save();
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
	 * 
	 * @param runner
	 *            the Runnable to invoke
	 */
	public static void asyncExec(Runnable runner) {
		display.asyncExec(runner);
	}

	/**
	 * Gets the system color for the supplied constant from the display
	 * 
	 * @param color
	 *            the SWT constant corresponding to the colour we want
	 * @return a Color object of that colour
	 */
	public static Color getSystemColor(int color) {
		return display.getSystemColor(color);
	}

	/**
	 * This listens to events from the components of the display. At the moment
	 * this is only triggered by the downloads list. When this happens, the
	 * state of the download display is updated with the new selection.
	 */
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == downloadsListComp) {
			if (downloadsListComp.getLastSelectedControl() != null)
				fileInfo.setDownloader(((DownloadDisplay) downloadsListComp
						.getLastSelectedControl()).getDownloadMonitor());
			else
				// It's null if it is unselected, eg by cleaning up downloads
				fileInfo.setDownloader(null);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void error(final String msgTitle, final String msg) {
		deferViewEvent(new Runnable() {
			public void run() {
				MessageBox about = new MessageBox(shell, SWT.ICON_ERROR
						| SWT.OK);
				about.setText(msgTitle);
				about.setMessage(msg);
				about.open();
			}
		});
	}

	public void updateAvailable(final String newVersion) {
		deferViewEvent(new Runnable() {
			public void run() {
				UpdateDialogue dialogue = new UpdateDialogue(shell,
						SWTView.this, newVersion, prefs, strings, urlFactory);
				dialogue.open();
			}
		});
	}

	/**
	 * @return
	 */
	public static synchronized Clipboard getClipboard() {
		if (clipboard == null)
			clipboard = new Clipboard(display);
		return clipboard;
	}

	public Shell getShell() {
		return shell;
	}

	public void windowMovedOrResized() {
		windowLoc = shell.getBounds();
		sashTop = mainArea.getWeights()[0];
		sashBottom = mainArea.getWeights()[1];
	}

	public void controlMoved(ControlEvent e) {
		windowMovedOrResized();
	}

	public void controlResized(ControlEvent e) {
		windowMovedOrResized();
	}

	/**
	 * This method should be called to indicate a system tray icon has been
	 * clicked. It will determine what needs to happen as a result.
	 */
	public void trayClicked() {
		// Note that the order of checks here is fairly important, making a
		// shell not visible causes an implicit minimisation
		if (shell.isVisible())
			shell.setVisible(false);
		else if (!shell.isVisible()) {
			shell.setVisible(true);
			shell.setMinimized(false);
		} else if (shell.getMinimized()) {
			shell.setMinimized(false);
			shell.setActive();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.view.IEMusicView#downloadCount(int, int,
	 * int)
	 */
	public void downloadCount(final int dl, final int finished, final int total) {
		if (sysTray != null)
			deferViewEvent(new Runnable() {
				public void run() {
					sysTray.setText(strings.getShortAppName() + ": " + dl
							+ " downloading, " + finished + " finished, "
							+ total + " total");
					if (dl == 0)
						sysTray.setImage(notDownloadingIcon);
					else
						sysTray.setImage(downloadingIcon);
				}
			});
	}

	/**
	 * Allows execution of something to be deferred until later. Later usually
	 * means when the view has been initialised (to allow recieving and
	 * processing events when starting up, but actually executing them later),
	 * or next time the event thread comes around. Deferred events will always
	 * be executed during the UI thread, so can happily make GUI modifications.
	 * 
	 * @param code
	 */
	public void deferViewEvent(Runnable code) {
		if (running) {
			asyncExec(code);
		} else {
			synchronized (deferredList) {
				deferredList.add(code);
			}
		}
	}

	public void pausedStateChanged(boolean state) {
		pausedState = state;
		if (state) {
			statusLine.setText("All Downloads Paused");
			if (pauseSysTrayMenuItem != null) // on mac this will be null
				pauseSysTrayMenuItem.setText("Resume downloads");
		} else {
			statusLine.unsetText();
			if (pauseSysTrayMenuItem != null)
				pauseSysTrayMenuItem.setText("Pause downloads");
		}
	}

	/**
	 * Allows other things to have access to the display
	 * 
	 * @return the current display
	 */
	public static Display getDisplay() {
		return display;
	}

	public void getProxyCredentials(final AuthScheme authScheme,
			final String host, final int port, final CredsCallback credsCallback) {
		deferViewEvent(new Runnable() {
			public void run() {
				ProxyDialogue proxyDialogue = new ProxyDialogue(shell,
						authScheme, host, port, credsCallback);
				proxyDialogue.open();
			}
		});
	}

}
