package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.view.SWTUtils;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * <p>Manages the system tray icon and events for the application. Any events that
 * occur are passed on to SWTView.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class SystemTrayManager {

	private TrayItem item;

	/**
	 * 
	 */
	public SystemTrayManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a system tray icon, and associated handlers
	 * @param view the SWTView to report events back to
	 * @param icon the icon to use in the tray
	 * @param tray the tray
	 */
	public SystemTrayManager(final SWTView view, Image icon, Tray tray, String text) {
		if (tray == null || view == null || icon == null) return;
		item = new TrayItem (tray, SWT.NONE);
		item.setImage(icon);
		item.setToolTipText(text);
		item.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				view.trayClicked();
			}
		});
		Shell shell = view.getShell();
		final Menu menu = new Menu (shell, SWT.POP_UP);
		SWTUtils.createMenuItem(menu, "Show/Hide", SWT.NONE, view, "trayClicked");
		SWTUtils.createMenuItem(menu, "Quit", SWT.NONE, view, "quitProgram");
		item.addListener (SWT.MenuDetect, new Listener () {
			public void handleEvent (Event event) {
				menu.setVisible (true);
			}
		});
	}
	
	/**
	 * Sets the text of the tooltip on the tray
	 * @param text the new text
	 */
	public void setText(String text) {
		item.setText(text);
	}

	/**
	 * Sets the icon on the system tray (not disposing the old one)
	 * @param icon the new icon
	 */
	public void setImage(Image icon) {
		item.setImage(icon);
	}
}
