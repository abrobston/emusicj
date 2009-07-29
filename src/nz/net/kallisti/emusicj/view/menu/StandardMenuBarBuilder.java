package nz.net.kallisti.emusicj.view.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;

/**
 * <p>
 * This is the menu builder for the standard menu bar of eMusic/J
 * </p>
 * <p>
 * This probably should be refactored to work with composition rather than
 * inheritance at some stage.
 * </p>
 * 
 * @author robin
 */
public class StandardMenuBarBuilder extends AbstractMenuBuilder {

	@Override
	protected List<MenuDetails> buildMenuDetails(SWTView view) {
		// Here we'll construct the standard menu information
		List<MenuDetails> menuDetails = new ArrayList<MenuDetails>();
		// File menu
		menuDetails.add(new MenuDetails("&File", Arrays
				.asList(
						// Open
						new MenuItemDetails("&Open...\tCtrl-O", SWT.CTRL + 'O',
								makeCallback(view, "openFile")),

						null,
						// Clean
						new MenuItemDetails("&Clean up download list\tCtrl-C",
								SWT.CTRL + 'C', makeCallback(view,
										"cleanUpDownloads")),
						// Prefs
						new MenuItemDetails("&Preferences...", SWT.NONE,
								makeCallback(view, "displayPreferences")),
						null, new MenuItemDetails("&Network Failure Test",
								SWT.NONE,
								makeCallback(view, "connectionIssues")),
						// Quit
						new MenuItemDetails("&Quit\tCtrl-Q", SWT.CTRL + 'Q',
								makeCallback(view, "quitProgram")))));
		// Help menu
		menuDetails.add(new MenuDetails("&Help", Arrays.asList(
				new MenuItemDetails("&User Manual", SWT.NONE, makeCallback(
						view, "userManual")),

				new MenuItemDetails("&About...", SWT.NONE, makeCallback(view,
						"aboutBox")))));
		return menuDetails;
	}

}
