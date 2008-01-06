package nz.net.kallisti.emusicj.view.swtwidgets.selection;

/**
 * <p>
 * Allows widgets to be notified of selection events.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface ISelectionListener {

	/**
	 * This is called when a widget is selected.
	 * 
	 * @param e
	 *            the event details
	 */
	public void widgetSelected(ISelectionEvent e);

}
