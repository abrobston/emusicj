package nz.net.kallisti.emusicj.mac;

import java.util.List;

import nz.net.kallisti.emusicj.bindings.Bindings;
import nz.net.kallisti.emusicj.bindings.EmusicjBindings;
import nz.net.kallisti.emusicj.controller.IEmusicjController;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.mac.OpenDocHandler;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

/**
 * <p>
 * This is a program to allow testing of the mac stuff
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class TestOpenDoc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestOpenDoc tod = new TestOpenDoc();
		tod.go();
	}

	private void go() {
		new OpenDocHandler(new IEmusicjController() {

			public void cancelDownloads() {
			}

			public void loadMetafile(String file) {
				System.out.println("Would be loading " + file);
			}

			public void loadMetafile(String path, String[] fileNames) {
				System.out.println("Would be loading: ");
				for (String file : fileNames) {
					System.out.println("  " + file);
				}
			}

			public void newDownloads(List<IDownloader> downloaders) {
			}

			public void pauseDownload(IDownloader dl) {
			}

			public void pauseDownloads() {
			}

			public void removeDownloads(DLState state) {
			}

			public void requeueDownload(IDownloader dl) {
			}

			public void resumeDownloads() {
			}

			public void run(String[] args) {
			}

			public void startDownload(IDownloader dl) {
			}

			@SuppressWarnings("unused")
			public void stopDownload(IDownloader dl) {
			}

			public void cancelDownload(IDownloader dl) {

			}

			public void networkIssuesDetected() {

			}

			public void removeFailedDownloads() {

			}

			public void deferMetafileLoad() {

			}

			public void restoreMetafileLoad() {

			}

			public void networkIssuesDetected() {
				// TODO Auto-generated method stub

			}

			public void removeFailedDownloads() {
				// TODO Auto-generated method stub

			}

		});
		Injector injector = Guice.createInjector(Stage.PRODUCTION,
				new Bindings(), new EmusicjBindings());
		IEmusicjView view = injector.getInstance(IEmusicjView.class);
		view.setState(IEmusicjView.ViewState.STARTUP);
		IEmusicjController controller = injector
				.getInstance(IEmusicjController.class);
		controller.run(new String[] {});
	}

}
