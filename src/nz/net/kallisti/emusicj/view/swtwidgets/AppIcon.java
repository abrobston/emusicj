package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.images.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.images.IImageFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * This produces an application icon widget. This widget is optionally
 * clickable, if the URL factory provides a non-<code>null</code> URL for it. If
 * it is clicked, it will launch a browser to that URL.
 * </p>
 * 
 * @author robin
 */
public class AppIcon extends Composite {

	/**
	 * Creates an instance of the application icon widget that uses a static
	 * widget provided by the image factory
	 * 
	 * @param parent
	 *            the parent widget for this
	 * @param style
	 *            the SWT style settings
	 * @param urls
	 *            the URL factory
	 * @param imgs
	 *            the image factory
	 */
	public AppIcon(Composite parent, int style, IURLFactory urls,
			IImageFactory imgs) {
		super(parent, style);
		Label lbl = new Label(this, SWT.NONE);
		lbl.setImage(imgs.getApplicationLogo());
	}

	/**
	 * Creates an instance of the application icon widget that uses a dynamic
	 * image provider, so that it can be updated.
	 * 
	 * @param parent
	 *            the parent widget for this
	 * @param style
	 *            the SWT style settings
	 * @param urls
	 *            the URL factory
	 * @param dynImage
	 *            the dynamic image provider
	 */
	public AppIcon(Composite parent, int style, IURLFactory urls,
			IDynamicImageProvider dynImage) {
		super(parent, style);
	}
}
