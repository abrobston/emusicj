package nz.net.kallisti.emusicj.view.swtwidgets;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This is a Composite that allows it's children to be selected. 
 * 
 * $Id:$
 *
 * @author robin
 */
public class SelectableComposite extends Composite implements MouseListener {

	private SelectableControl lastSelected;

	public SelectableComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	public void addSelectableControl(SelectableControl c) {
		c.addMouseListener(this);
	}

	public void mouseDoubleClick(MouseEvent e) {
		
	}

	public void mouseDown(MouseEvent e) {
		Object o = e.getSource(); 
		if (o instanceof SelectableControl) {
			if (o != lastSelected) {
				((SelectableControl)o).select();
				if (lastSelected != null)
					lastSelected.unselect();			
				lastSelected = (SelectableControl)o;
			}
		}
	}

	public void mouseUp(MouseEvent e) {
		
	}

}
