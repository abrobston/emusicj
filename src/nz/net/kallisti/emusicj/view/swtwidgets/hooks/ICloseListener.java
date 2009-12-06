package nz.net.kallisti.emusicj.view.swtwidgets.hooks;


/**
 * <p>
 * A standard interface that allows windows or other widgets to announce when
 * they are being closed.
 * </p>
 * 
 * @author robin
 */
public interface ICloseListener<T> {

	/**
	 * This is called when the widget is closed, or is about to close.
	 * 
	 * @param widget
	 *            the thing that is doing the closing (may not actually be a gui
	 *            widget)
	 * @param okClose
	 *            <code>true</code> if this close event was initiated through an
	 *            normal action, such as clicking OK, or if it is an 'aborting'
	 *            action, such as the user hitting cancel or the window close
	 *            box.
	 * @param data
	 *            any data that resulted from the widget. May be
	 *            <code>null</code>.
	 */
	public void closed(Object widget, boolean okClose, T data);

}
