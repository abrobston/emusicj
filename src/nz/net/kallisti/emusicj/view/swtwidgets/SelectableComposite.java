package nz.net.kallisti.emusicj.view.swtwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;

/**
 * This is a Composite that allows it's children to be selected. 
 * 
 * $Id$
 *
 * @author robin
 */
public class SelectableComposite extends Composite implements SelectionListener {

	private ISelectableControl lastSelected;

	public SelectableComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	public void addSelectableControl(ISelectableControl c) {
		c.addSelectionListener(this);
	}
	
	public void removeSelectableControl(ISelectableControl c) {
		c.removeSelectionListener(this);
		if (c == lastSelected) {
			lastSelected = null;
			notifyListeners(SWT.Selection, new Event());
		}
	}


	public void widgetSelected(SelectionEvent e) {
		Object o = e.getSource(); 
		if (o instanceof ISelectableControl) {
			if (o != lastSelected) {
				((ISelectableControl)o).select();
				if (lastSelected != null)
					lastSelected.unselect();			
				lastSelected = (ISelectableControl)o;
				notifyListeners(SWT.Selection, new Event());
			}
		}		
	}

	public void widgetDefaultSelected(SelectionEvent e) { }
	
	public ISelectableControl getSelectedControl() {
		return lastSelected;
	}
	
	public void addSelectionListener(SelectionListener listener) {
		addListener(SWT.Selection, new TypedListener(listener));
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		removeListener(SWT.Selection, listener);
	}

}
