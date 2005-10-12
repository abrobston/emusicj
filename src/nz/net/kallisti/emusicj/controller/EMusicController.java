package nz.net.kallisti.emusicj.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.metafiles.MetafileLoader;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.test.TestDownloadsModel;
import nz.net.kallisti.emusicj.view.IEMusicView;

/**
 * <p>This is the main controller for the application. It routes stuff around,
 * ensuring that the view is kept up to date with the system, and that the
 * state is kept up to date with user requests.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class EMusicController implements IEMusicController {

    private IEMusicView view;
	private IDownloadsModel downloadsModel = new TestDownloadsModel(10);

    public EMusicController() {
        super();
    }

    public void setView(IEMusicView view) {
        this.view = view;
        view.setController(this);
    }

    public void run(String[] args) {
        // Initialise the system
    	if (view != null)
    		view.setState(IEMusicView.ViewState.STARTUP);
        for (String file : args)
            loadMetafile(file);
        // Pass the system state on to the view to ensure it's up to date
        if (view != null)
        	view.setDownloadsModel(downloadsModel);
        
        if (view != null)
        	view.setState(IEMusicView.ViewState.RUNNING);
        // Call the view's event loop
        if (view != null)
        	view.processEvents(this);
        // Clean up the program
        shutdown();
    }

    /**
	 * This tells all the downloaders to finish, otherwise the threads
	 * will keep running
	 */
	private void shutdown() {
		List<IDownloader> dls = downloadsModel.getDownloaders();
		for (IDownloader dl : dls) {
			dl.stop();
		}
	}

	/**
     * Loads a metafile. A metafile may contain any number of files to download.
     * @param file the filename of the metafile to load
     */
    public void loadMetafile(String file) {
        try {
            MetafileLoader.load(this, new File(file));
        } catch (IOException e) {
            error("Error reading file",e.getMessage());
        }
    }

    /**
     * Invokes the view to notify the user of an error condition. If the system
     * is still initialising, then the errors will be queued up and displayed
     * later.
     * @param msgTitle The title of the error 
     * @param msg The message contents
     */
    private void error(String msgTitle, String msg) {
        // TODO Auto-generated method stub        
    }

    /**
     * Adds a new set of downloaders to the model.
     * @param downloaders the downloaders to add
     */
    public void newDownloads(List<IMusicDownloader> downloaders) {
        for (IMusicDownloader dl : downloaders)
            downloadsModel.addDownload(dl);
    }

}
