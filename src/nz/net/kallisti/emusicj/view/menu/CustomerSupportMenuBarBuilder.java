package nz.net.kallisti.emusicj.view.menu;

import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.misc.BrowserLauncher;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;

import com.google.inject.Inject;

/**
 * <p>
 * This is the menu builder for the standard menu bar of the Classicsonline app
 * (it just includes an extra help entry)
 * </p>
 * 
 * @author robin
 */
public class CustomerSupportMenuBarBuilder extends StandardMenuBarBuilder {

	private final IURLFactory urls;

	@Inject
	public CustomerSupportMenuBarBuilder(IURLFactory urls) {
		this.urls = urls;
	}

	@Override
	protected List<MenuDetails> buildMenuDetails(SWTView view) {
		List<MenuDetails> menuDetails = super.buildMenuDetails(view);
		for (MenuDetails details : menuDetails) {
			if ("&Help".equals(details.text)) {
				List<MenuItemDetails> items = details.entryDetails;
				ArrayList<MenuItemDetails> newItems = new ArrayList<MenuItemDetails>(
						items);
				newItems.add(0, new MenuItemDetails("&Customer Support",
						SWT.NONE, customerSupport(view)));
				details.entryDetails = newItems;
			}
		}
		return menuDetails;
	}

	private Runnable customerSupport(final SWTView view) {
		return new Runnable() {
			public void run() {
				new Thread() {
					@Override
					public void run() {
						try {
							BrowserLauncher.openURL(urls
									.getCustomerSupportURL());
						} catch (Exception e) {
							view
									.error(
											"Error launching browser",
											"There seemed to be a "
													+ "problem launching the browser. The customer support page can"
													+ "be found at "
													+ urls
															.getCustomerSupportURL());
						}
					}
				}.start();

			}
		};
	}

}
