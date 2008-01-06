package nz.net.kallisti.emusicj.view.swtwidgets.selection;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>
 * Implementors of this provided information regarding widgets being selected,
 * in general by mouse-clicks or something.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface ISelectionEvent {

	/**
	 * Provides the keyboard state at the time of the event.
	 * 
	 * @return a mask giving the keyboard state
	 * @see MouseEvent#stateMask
	 */
	public int getKeyboardState();

	/**
	 * Provides the widget that is triggering this event
	 * 
	 * @return the widget that was selected
	 */
	public Widget getSource();

}
