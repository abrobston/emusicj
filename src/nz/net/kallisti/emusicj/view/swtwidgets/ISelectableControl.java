package nz.net.kallisti.emusicj.view.swtwidgets;

import org.eclipse.swt.events.SelectionListener;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public interface ISelectableControl {

	/**
	 * Tells the object that it is selected
	 */
	void select();

	/**
	 * Tells the object that it is not selected
	 */
	void unselect();

	/**
	 * Add a selection listener. Note that when the event is triggered, the
	 * SelectableControl doesn't assume that it will be selected. It
	 * requires a call to {@link select()} to establish that.
	 * @param listener the listener to add
	 */
	void addSelectionListener(SelectionListener listener);

}
