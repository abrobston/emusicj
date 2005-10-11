package nz.net.kallisti.emusicj.view;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>A miscellanious collection of useful functions for working with SWT.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class SWTUtils {

    /**
     * Creates a drop down menu, say, the file menu.
     * @param shell the shell that this menu lives in
     * @param base the base menu this derives from, typically a menu bar
     * @param text the text of the menu
     * @return the menu that is created
     */
    public static Menu createDropDown(Shell shell, Menu base, String text) {
        MenuItem menuI = new MenuItem(base, SWT.CASCADE);
        menuI.setText(text);
        Menu menu = new Menu (shell, SWT.DROP_DOWN);
        menuI.setMenu(menu);
        return menu;
    }

    /**
     * Creates a menu entry in a menu. Registers a handler for the entry. For
     * this to happen both <code>handler</code> and <code>callback</code> must
     * be non-null.
     * @param base the menu this item goes in
     * @param text the text of the item
     * @param accel the accelleration key (null for none)
     * @param handler the object that handles this event
     * @param callback the method to call in the <code>handler</code> object.
     * This method is called when the item is selected.
     * @return
     */
    @SuppressWarnings("boxing")
    public static MenuItem createMenuItem(Menu base, String text, Integer accel, 
            Object handler, String callback) {
        MenuItem mi = new MenuItem(base, SWT.PUSH);
        if (text != null)
            mi.setText(text);
        if (accel != null)
            mi.setAccelerator(accel);
        if (handler != null && callback != null)
            registerMenuCallback(mi, handler, callback);
        return mi;
    }

    /**
     * Creates a selection listener for a <code>MenuItem</code>. This listener
     * uses reflection to invoke the method name in the handler object.
     * @param mi the <code>MenuItem</code> to attach the listener to
     * @param handler the object that handles selection of this item
     * @param callback the method to call when the object is selected
     */
    private static void registerMenuCallback(final MenuItem mi, 
            final Object handler, final String callback) {
        mi.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    Method m = handler.getClass().getMethod(callback, (Class[])null);
                    m.invoke(handler, (Object[])null);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
}
