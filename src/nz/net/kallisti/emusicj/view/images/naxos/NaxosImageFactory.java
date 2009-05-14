package nz.net.kallisti.emusicj.view.images.naxos;

import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.images.AbstractImageFactory;
import nz.net.kallisti.emusicj.view.images.IStreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

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
public class NaxosImageFactory extends AbstractImageFactory {

	@Inject
	public NaxosImageFactory(
			Provider<IStreamDynamicImageProvider> streamImageProvider) {
		super(streamImageProvider);
	}

	private Display display;
	private IStreamDynamicImageProvider appIconProvider;

	@Override
	public void setDisplay(Display display) {
		this.display = display;
	}

	public Image getDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"col-dl-16.png"));
	}

	public Image getNotDownloadingIcon() {
		return new Image(display, this.getClass().getResourceAsStream(
				"col-nodl-16.png"));
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
						"col-app-16.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"col-app-32.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"col-app-48.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"col-app-64.png")),
				new Image(display, this.getClass().getResourceAsStream(
						"col-app-128.png")) };
	}

	public Image getAboutLogo() {
		return new Image(SWTView.getDisplay(), this.getClass()
				.getResourceAsStream("col-about.png"));
	}

	public IDynamicImageProvider getApplicationLogoProvider() {
		appIconProvider = initStreamImageProvider(appIconProvider, this
				.getClass().getResourceAsStream("col-app-32.png"));
		return appIconProvider;
	}

}
