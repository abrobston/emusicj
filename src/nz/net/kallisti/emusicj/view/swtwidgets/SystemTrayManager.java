package nz.net.kallisti.emusicj.view.swtwidgets;

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
 * <p>$Id$</p>
 *
 * @author robin
 */
public class SystemTrayManager {

	private TrayItem item;
	private Menu menu;

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
		menu = new Menu (shell, SWT.POP_UP);
	}

	/**
	 * This allows other classes to add things to the system tray menu
	 * @return the system tray menu instance
	 */
	public Menu getMenu() {
		return menu;
	}
	
	/**
	 * This must be called in order for the menu to be functional. It allows
	 * menu items to be added to the menu first, and then this is called to
	 * enable it.
	 */
	public void buildMenu() {
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
		item.setToolTipText(text);
	}

	/**
	 * Sets the icon on the system tray (not disposing the old one)
	 * @param icon the new icon
	 */
	public void setImage(Image icon) {
		item.setImage(icon);
	}
}
