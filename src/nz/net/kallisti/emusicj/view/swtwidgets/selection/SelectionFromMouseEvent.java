package nz.net.kallisti.emusicj.view.swtwidgets.selection;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>
 * This indicates that a widget has been selected, as per {@link SelectionEvent}.
 * However, it is used in cases where the selection originates from a mouse
 * event, and sets the appropriate flags from that.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class SelectionFromMouseEvent implements ISelectionEvent {

	private final int stateMask;
	private Widget source;

	/**
	 * Creates a selection event from a mouse event
	 * 
	 * @param e
	 *            the mouse event to get details from
	 * @param source
	 *            the source widget providing the event. If <code>null</code>,
	 *            then the source of <code>e</code> is used
	 */
	public SelectionFromMouseEvent(MouseEvent e, Widget source) {
		this.stateMask = e.stateMask;
		if (source == null && e.getSource() instanceof Widget)
			this.source = (Widget) e.getSource();
		else
			this.source = source;

	}

	public int getKeyboardState() {
		return stateMask;
	}

	public Widget getSource() {
		return source;
	}

}
