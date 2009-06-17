package nz.net.kallisti.emusicj.view.menu;

import java.lang.reflect.Method;
import java.util.List;

import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.google.inject.Inject;

/**
 * <p>
 * This provides a framework for building a menu bar for an application. It is
 * designed such that it can be extended to provide menus that differ on
 * different builds.
 * </p>
 * 
 * @author robin
 */
public abstract class AbstractMenuBuilder implements IMenuBuilder {

	@Inject
	public AbstractMenuBuilder() {

	}

	public Menu getMenu(Shell shell, SWTView view) {
		List<MenuDetails> menuDetails = buildMenuDetails(view);
		Menu menuBar = new Menu(shell, SWT.BAR);
		for (MenuDetails details : menuDetails) {
			MenuItem menuI = new MenuItem(menuBar, SWT.CASCADE);
			menuI.setText(details.text);
			Menu menu = new Menu(shell, SWT.DROP_DOWN);
			menuI.setMenu(menu);
			for (MenuItemDetails itemDetails : details.entryDetails) {
				if (itemDetails == null) {
					new MenuItem(menu, SWT.SEPARATOR);
					continue;
				}
				MenuItem mi = new MenuItem(menu, SWT.PUSH);
				mi.setText(itemDetails.text);
				mi.setAccelerator(itemDetails.accelerator);
				mi.addSelectionListener(runnableToListener(itemDetails.action));
			}
		}
		return menuBar;
	}

	/**
	 * This provides a list of menu details that defines the menus to be
	 * displayed
	 * 
	 * @param view
	 *            the view instance that may want methods called on it
	 * @return a list of {@link MenuDetails} describing the menus to show
	 */
	protected abstract List<MenuDetails> buildMenuDetails(SWTView view);

	/**
	 * Creates a runnable that will call the method <code>callback</code> on the
	 * object <code>handler</code>.
	 * 
	 * @param handler
	 *            the object with the method
	 * @param callback
	 *            the method you want to call
	 * @return a runnable that will call that method when executed
	 */
	protected Runnable makeCallback(final Object handler, final String callback) {
		return new Runnable() {
			public void run() {
				try {
					Method m = handler.getClass().getMethod(callback,
							(Class[]) null);
					m.invoke(handler, (Object[]) null);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	/**
	 * This creates a selection listener that will execute the provided runnable
	 * when it's activated.
	 * 
	 * @param runnable
	 *            the runnable to run
	 * @return a selection listener that will run the runnable
	 */
	protected SelectionListener runnableToListener(final Runnable runnable) {
		return new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				runnable.run();
			}
		};
	}

	/**
	 * <p>
	 * A handy collection of menu details.
	 * </p>
	 */
	protected class MenuDetails {

		public MenuDetails(String text, List<MenuItemDetails> entryDetails) {
			this.text = text;
			this.entryDetails = entryDetails;
		}

		/**
		 * The text of the menu
		 */
		public String text;
		/**
		 * A list containing the details of the entries in this menu. A
		 * <code>null</code> in this will become a separator.
		 */
		public List<MenuItemDetails> entryDetails;

		@Override
		public String toString() {
			return "Menu:" + text;
		}
	}

	/**
	 * <p>
	 * Details on a menu item
	 * </p>
	 */
	protected class MenuItemDetails {

		public MenuItemDetails(String text, int accelerator, Runnable action) {
			this.text = text;
			this.accelerator = accelerator;
			this.action = action;
		}

		public String text;
		public int accelerator;
		public Runnable action;

		@Override
		public String toString() {
			return "MenuItem:" + text;
		}
	}

}
