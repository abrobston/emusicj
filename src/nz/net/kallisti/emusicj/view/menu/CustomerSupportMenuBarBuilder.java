package nz.net.kallisti.emusicj.view.menu;

import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;

/**
 * <p>
 * This is the menu builder for the standard menu bar of the Classicsonline app
 * (it just includes an extra help entry)
 * </p>
 * 
 * @author robin
 */
public class CustomerSupportMenuBarBuilder extends StandardMenuBarBuilder {

	@Override
	protected List<MenuDetails> buildMenuDetails(final SWTView view) {
		List<MenuDetails> menuDetails = super.buildMenuDetails(view);
		for (MenuDetails details : menuDetails) {
			if ("&Help".equals(details.text)) {
				List<MenuItemDetails> items = details.entryDetails;
				ArrayList<MenuItemDetails> newItems = new ArrayList<MenuItemDetails>(
						items);
				newItems.add(0, new MenuItemDetails("&Customer Support",
						SWT.NONE, new Runnable() {
							public void run() {
								view.customerSupport();
							}
						}));
				details.entryDetails = newItems;
			}
		}
		return menuDetails;
	}

}
