package nz.net.kallisti.emusicj.view.images.emusicj;

import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.images.IImageFactory;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * This image factory supplies the images that are used for eMusic/J
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class EmusicjImageFactory implements IImageFactory {

	private Display display;

	public void setDisplay(Display display) {
		this.display = display;
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

	public Image getApplicationLogo() {
		return new Image(display, this.getClass().getResourceAsStream(
				"emusicj-app-32.png"));
	}

}
