package nz.net.kallisti.emusicj.view.swtwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * <p>Handles a status line, which is basically a single label that
 * can be put somewhere. Done in a class on it's own to allow increasing its
 * functionality should that been needed.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class StatusLine extends Composite {

    private Label label;

    /**
     * @param parent
     * @param style
     */
    public StatusLine(Composite parent, int style) {
        super(parent, style);
        label = new Label(this, SWT.NONE);
        unsetText();
    }
    
    public void setText(String text) {
        label.setText(text);
        label.pack();
        pack();
    }
    
    public void unsetText() {
        label.setText("");
        label.pack();
    }

}
