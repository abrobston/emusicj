package nz.net.kallisti.emusicj.view.style;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * <p>
 * Styles the application according to the Passionato requirements.
 * </p>
 * 
 * @author robin
 */
public class PassionatoStyle extends DefaultStyle {

	/**
	 * This gives the toolbar a black gradient style.
	 */
	@Override
	public void styleToolbar(final Composite toolbar) {
		toolbar.setBackgroundMode(SWT.INHERIT_DEFAULT);
		toolbar.addListener(SWT.Resize, new Listener() {

			private Image prevImage;

			public void handleEvent(Event event) {
				Rectangle rect = toolbar.getClientArea();
				Image bgImage = new Image(display, 1, Math.max(1, rect.height));
				GC gc = new GC(bgImage);
				Color dark = new Color(display, 0x35, 0x34, 0x35); // #353435
				Color light = new Color(display, 0x52, 0x52, 0x55); // #525255
				gc.setForeground(dark);
				gc.setBackground(light);
				gc.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);
				gc.dispose();
				toolbar.setBackgroundImage(bgImage);
				if (prevImage != null)
					prevImage.dispose();
				prevImage = bgImage;
			}

		});
	}

}
