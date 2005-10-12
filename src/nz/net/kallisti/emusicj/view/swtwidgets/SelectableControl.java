package nz.net.kallisti.emusicj.view.swtwidgets;

import org.eclipse.swt.events.MouseListener;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public interface SelectableControl {

	/**
	 * 
	 */
	void select();

	/**
	 * 
	 */
	void unselect();

	/**
	 * @param composite
	 */
	void addMouseListener(MouseListener listener);

}
