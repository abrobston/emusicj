package nz.net.kallisti.emusicj.view.style;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * Implementations of this can be used to provide custom styling of parts of the
 * UI.
 * </p>
 * 
 * @author robin
 */
public interface IAppStyle {

	/**
	 * Initialise the style with any objects it may need. This typically must be
	 * called before any other operations are performed.
	 * 
	 * @param display
	 *            the SWT display object
	 */
	public abstract void init(Display display);

	/**
	 * This styles the toolbar, such as providing it with a custom background.
	 * 
	 * @param toolbar
	 *            the composite that contains the toolbar contents.
	 */
	public abstract void styleToolbar(Composite toolbar);

}