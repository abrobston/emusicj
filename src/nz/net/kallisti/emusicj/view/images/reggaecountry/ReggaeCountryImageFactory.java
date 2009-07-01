package nz.net.kallisti.emusicj.view.images.reggaecountry;

import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.images.AbstractImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamAndURLDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Provider;

/**
 * <p>
 * This image factory supplies the images that are used for the ReggaeCountry
 * branding
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ReggaeCountryImageFactory extends AbstractImageFactory {

	public ReggaeCountryImageFactory(
			Provider<IStreamDynamicImageProvider> streamImageProvider,
			Provider<IURLDynamicImageProvider> urlImageProvider,
			Provider<IStreamAndURLDynamicImageProvider> streamAndUrlImageProvider) {
		super(streamImageProvider, urlImageProvider, streamAndUrlImageProvider);
	}

	private Display display;
	private IStreamDynamicImageProvider appIconProvider;

	@Override
	public void setDisplay(Display display) {
		this.display = display;
	}

	public Image getDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"rc-dl-16.png"));
	}

	public Image getNotDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"rc-nodl-16.png"));
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
						"rc-app-16.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"rc-app-32.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"rc-app-48.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"rc-app-64.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"rc-app-128.png")) };
	}

	public Image getAboutLogo() {
		return new Image(SWTView.getDisplay(), this.getClass()
				.getResourceAsStream("rc-about.png"));
	}

	public Image getApplicationLogo() {
		return new Image(display, this.getClass().getResourceAsStream(
				"rc-app-32.png"));
	}

	public IDynamicImageProvider getApplicationLogoProvider() {
		appIconProvider = initStreamImageProvider(appIconProvider, this
				.getClass().getResourceAsStream("rc-app-32.png"));
		return appIconProvider;
	}

	public Image getFolderIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"folder_32.png"));
	}

}
