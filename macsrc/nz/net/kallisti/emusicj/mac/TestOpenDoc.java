package nz.net.kallisti.emusicj.mac;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import nz.net.kallisti.emusicj.bindings.Bindings;
import nz.net.kallisti.emusicj.bindings.EmusicjBindings;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.view.IEMusicView;

/**
 * <p>This is a program to allow testing of the mac stuff</p>
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
		new OpenDocHandler(new IEMusicController() {

			public void cancelDownloads() {
			}

			public void loadMetafile(String file) {
				System.out.println("Would be loading "+file);
			}

			public void loadMetafile(String path, String[] fileNames) {
				System.out.println("Would be loading: ");
				for (String file : fileNames) {
					System.out.println("  "+file);
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

			public void stopDownload(IDownloader dl) {
			}
			
		});
    	Injector injector = Guice.createInjector(Stage.PRODUCTION, new Bindings(), new EmusicjBindings());
        IEMusicView view = injector.getInstance(IEMusicView.class);
        view.setState(IEMusicView.ViewState.STARTUP);
        IEMusicController controller = injector.getInstance(IEMusicController.class);
        controller.run(new String[] {});
	}

}
