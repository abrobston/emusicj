package nz.net.kallisti.emusicj.view.swtwidgets;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * This makes the widgetSelected and widgetDefaultSelected methods both call
 * action(), which removes the need for a lot of pointless boilerplate in
 * handling selection events.
 * 
 * $Id:$
 *
 * @author robin
 */
public abstract class SelectionAdapter implements SelectionListener {

	public SelectionAdapter() {
		super();
	}

	public void widgetSelected(SelectionEvent ev) {
		action(ev);
	}

	public void widgetDefaultSelected(SelectionEvent ev) {
		action(ev);
	}

	public abstract void action(SelectionEvent ev);
	
}
