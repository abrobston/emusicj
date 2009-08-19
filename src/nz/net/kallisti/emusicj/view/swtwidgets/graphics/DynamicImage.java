package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.BrowserLauncher;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * This displays an image, and that image may be updated by an
 * {@link IDynamicImageProvider} if one is given on initialisation. This widget
 * is optionally clickable, if a non-<code>null</code> URL is provided. If it is
 * clicked, it will launch a browser to that URL.
 * </p>
 * 
 * @author robin
 */
public class DynamicImage extends Composite implements
		IDynamicImageChangeListener {

	private final Label lbl;
	private URL url;
	private Logger logger;
	private final SWTView view;
	private final GridLayout thisLayout;
	protected final Composite parent;
	private final Cursor clickyCursor;

	/**
	 * Creates an instance of the application icon widget that uses a dynamic
	 * image provider, so that it can be updated.
	 * 
	 * @param parent
	 *            the parent widget for this
	 * @param style
	 *            the SWT style settings
	 * @param url
	 *            the URL that a browser will be opened to if this widget is
	 *            clicked. May be <code>null</code>, in which case this isn't
	 *            clickable.
	 * @param dynImage
	 *            the dynamic image provider
	 */
	public DynamicImage(Composite parent, int style, Display display, URL url,
			IDynamicImageProvider dynImage, SWTView view) {
		super(parent, style);
		this.parent = parent;
		this.view = view;
		thisLayout = new GridLayout(1, false);
		thisLayout.marginWidth = 0;
		thisLayout.marginLeft = 0;
		thisLayout.marginRight = 0;
		thisLayout.horizontalSpacing = 0;
		thisLayout.marginHeight = 0;
		this.setLayout(thisLayout);
		this.url = url;
		lbl = new Label(this, SWT.NONE);
		dynImage.addListener(this);
		newImage(dynImage, dynImage.getImage());
		clickyCursor = new Cursor(display, SWT.CURSOR_HAND);
		if (url != null)
			lbl.setCursor(clickyCursor);
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				wasClicked();
			}
		});
	}

	public void newImage(IDynamicImageProvider dynImage, final Image image) {
		view.deferViewEvent(new Runnable() {
			public void run() {
				if (lbl.isDisposed())
					return;
				try {
					lbl.setImage(image);
				} catch (IllegalArgumentException e) {
					// This can occur if something is wrong with the image
					logger.log(Level.WARNING,
							"An error occurred setting a new image", e);
				}
				lbl.pack();
				pack();
				layout();
				parent.layout();
			}
		});
	}

	/**
	 * Updates the URL that will be opened when this is clicked.
	 * 
	 * @param url
	 *            the new URL, or none to make it not clickable.
	 */
	public void changeUrl(final URL url) {
		view.deferViewEvent(new Runnable() {
			public void run() {
				DynamicImage.this.url = url;
				if (url == null)
					lbl.setCursor(null);
				else
					lbl.setCursor(clickyCursor);
			}
		});
	}

	/**
	 * This gets called when the logo is clicked, and there is a URL. It opens
	 * the URL in the browser.
	 */
	private void wasClicked() {
		if (url == null)
			return;
		new Thread() {
			@Override
			public void run() {
				try {
					BrowserLauncher.openURL(url);
				} catch (Exception e) {
					logger.log(Level.WARNING,
							"Error occurred attempting to open browser", e);
				}
			}
		}.start();

	}
}
