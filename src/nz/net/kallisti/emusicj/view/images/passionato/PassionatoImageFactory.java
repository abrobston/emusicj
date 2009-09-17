package nz.net.kallisti.emusicj.view.images.passionato;

import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.images.AbstractImageFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamAndURLDynamicImageProvider;
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
public class PassionatoImageFactory extends AbstractImageFactory implements
		IImageFactory {

	private IStreamDynamicImageProvider appIconProvider;

	@Inject
	public PassionatoImageFactory(
			Provider<IStreamDynamicImageProvider> streamImageProvider,
			Provider<IURLDynamicImageProvider> urlImageProvider,
			Provider<IStreamAndURLDynamicImageProvider> streamAndUrlImageProvider) {
		super(streamImageProvider, urlImageProvider, streamAndUrlImageProvider);
	}

	public Image getDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-dl-16.png"));
	}

	public Image getNotDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-nodl-16.png"));
	}

	public Image getCancelIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-cancel-16.png"));
	}

	public Image getPauseIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-pause-32.png"));
	}

	public Image getRequeueIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-requeue-16.png"));
	}

	public Image getStartIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-start-16.png"));
	}

	public Image getStartSmallIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-start-16.png"));
	}

	public Image[] getAppIcons() {
		return new Image[] {
				new Image(display, this.getClass().getResourceAsStream(
						"passionato-app-16.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"passionato-app-32.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"passionato-app-48.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"passionato-app-64.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"passionato-app-128.png")) };
	}

	public Image getAboutLogo() {
		return new Image(SWTView.getDisplay(), this.getClass()
				.getResourceAsStream("passionato-about-128.png"));
	}

	public IDynamicImageProvider getApplicationLogoProvider() {
		appIconProvider = initStreamImageProvider(appIconProvider, this
				.getClass().getResourceAsStream("passionato-logo.png"));
		return appIconProvider;
	}

	public Image getFolderIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"passionato-open-32.png"));
	}

}
