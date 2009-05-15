package nz.net.kallisti.emusicj.view.images.emusicj;

import java.net.MalformedURLException;
import java.net.URL;

import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.images.AbstractImageFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;

import org.eclipse.swt.graphics.Image;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This image factory supplies the images that are used for eMusic/J
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class EmusicjImageFactory extends AbstractImageFactory implements
		IImageFactory {

	private IURLDynamicImageProvider appIconProvider;

	@Inject
	public EmusicjImageFactory(
			Provider<IStreamDynamicImageProvider> streamImageProvider,
			Provider<IURLDynamicImageProvider> urlImageProvider) {
		super(streamImageProvider, urlImageProvider);
	}

	public Image getDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"emusicj-dl-16.png"));
	}

	public Image getNotDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"emusicj-nodl-16.png"));
	}

	public Image getCancelIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"cancel.png"));
	}

	public Image getPauseIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"pause.png"));
	}

	public Image getRequeueIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"requeue.png"));
	}

	public Image getStartIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"start.png"));
	}

	public Image[] getAppIcons() {
		return new Image[] {
				new Image(display, this.getClass().getResourceAsStream(
						"emusicj-app-16.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"emusicj-app-32.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"emusicj-app-48.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"emusicj-app-64.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"emusicj-app-128.png")) };
	}

	public Image getAboutLogo() {
		return new Image(SWTView.getDisplay(), this.getClass()
				.getResourceAsStream("emusicj-about.png"));
	}

	// public IDynamicImageProvider getApplicationLogoProvider() {
	// appIconProvider = initStreamImageProvider(appIconProvider, this
	// .getClass().getResourceAsStream("emusicj-app-32.png"));
	// return appIconProvider;
	// }

	public IDynamicImageProvider getApplicationLogoProvider() {
		try {
			appIconProvider = initURLImageProvider(appIconProvider, new URL(
					"http://www.kallisti.net.nz/~robin/test.png"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// appIconProvider = initStreamImageProvider(appIconProvider, this
		// .getClass().getResourceAsStream("emusicj-app-32.png"));
		return appIconProvider;
	}

}
