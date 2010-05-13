package nz.net.kallisti.emusicj.view.style;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * This class provides the default styling of the application (which is
 * generally just to leave things alone.)
 * </p>
 * 
 * @author robin
 */
public class DefaultStyle implements IAppStyle {

	protected Display display;

	public void init(Display display) {
		this.display = display;
	}

	public void styleToolbar(Composite toolbar) {
		// Do nothing.
	}

}
