package nz.net.kallisti.emusicj.view.menu;

import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * This interface provides access to the menu that menu builders make
 * </p>
 * 
 * @author robin
 */
public interface IMenuBuilder {

	/**
	 * Creates the menu bar
	 * 
	 * @param shell
	 *            the shell that is the parent of this menu bar
	 * @param view
	 *            the view which contains some standard functions
	 * @return a menu
	 */
	public Menu getMenu(Shell shell, SWTView view);

}
