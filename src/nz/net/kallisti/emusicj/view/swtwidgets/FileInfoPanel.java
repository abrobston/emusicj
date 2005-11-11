package nz.net.kallisti.emusicj.view.swtwidgets;

import java.io.File;
import java.util.Hashtable;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IMusicDownloadMonitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * <p>This class displays information about a file (as represented by an
 * IDownloader instance). Once it is in place, a call to {@link setDownloader}
 * defines what to display. It keeps an image cache for album covers.
 * 
 * $Id$
 *
 * @author robin
 */
public class FileInfoPanel extends Composite implements DisposeListener {

	//private ScrolledComposite displayArea;
	private Label titleLabel;
	private Label albumLabel;
	private Label artistLabel;
	private Label title;
	private Label album;
	private Label artist;
	private Composite imageArea;
	private Composite textArea;
	private Label imageLabel;
	private Display display;
    private Hashtable<File, Image> imageCache;

	/**
	 * Creates an instance of FileInfoPanel with the provided parent and style.
	 * @param parent the parent of this widget
	 * @param style the style.
	 */
	public FileInfoPanel(Composite parent, int style, Display display) {
		super(parent, style);
        imageCache = new Hashtable<File, Image>();
        addDisposeListener(this);
		this.display = display;
		this.setLayout(new GridLayout(2, false));
		imageArea = new Composite(this, SWT.NONE);
		imageArea.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, 
				false, true));
		imageArea.setLayout(new GridLayout(1, false));
		imageLabel = new Label(imageArea, SWT.NONE);
		textArea = new Composite(this, SWT.NONE);
		textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, 
				true, true));
		textArea.setLayout(new GridLayout(2, false));
		titleLabel = new Label(textArea, SWT.NONE);
		titleLabel.setText("Title:");
		titleLabel.setVisible(false);
		title = new Label(textArea, SWT.NONE);
		title.setVisible(false);
		albumLabel = new Label(textArea, SWT.NONE);
		albumLabel.setText("Album:");
		albumLabel.setVisible(false);
		album = new Label(textArea, SWT.NONE);
		album.setVisible(false);
		artistLabel = new Label(textArea, SWT.NONE);
		artistLabel.setText("Artist:");
		artistLabel.setVisible(false);
		artist = new Label(textArea, SWT.NONE);
		artist.setVisible(false);
		imageArea.layout();
		textArea.layout();
		layout();
		pack();
	}
	
	public void setDownloader(IDownloadMonitor dl) {
		// TODO image caching and disposing
		// Done like this so I can add display of non-music stuff easily later
		if (dl == null || !(dl instanceof IMusicDownloadMonitor)) {
			titleLabel.setVisible(false);
			title.setVisible(false);
			albumLabel.setVisible(false);
			album.setVisible(false);
			artistLabel.setVisible(false);
			artist.setVisible(false);
            imageLabel.setImage(null);
		} else if (dl instanceof IMusicDownloadMonitor) {
			IMusicDownloadMonitor mdl = (IMusicDownloadMonitor)dl;
			title.setText(mdl.getTrackName());
			album.setText(mdl.getAlbumName());
			artist.setText(mdl.getArtistName());
            File coverFile = mdl.getCoverArt();
            if (coverFile != null && !coverFile.toString().equals("")) {
                Image im = imageCache.get(coverFile); 
                if (im == null) {
                    im = new Image(display, mdl.getCoverArt().toString());
                    imageCache.put(coverFile, im);
                }
                imageLabel.setImage(im);
            } else {
                imageLabel.setImage(null);
            }
			titleLabel.setVisible(true);
			title.setVisible(true);
			albumLabel.setVisible(true);
			album.setVisible(true);
			artistLabel.setVisible(true);
			artist.setVisible(true);
		}
		imageArea.layout();
		imageArea.pack();
		textArea.layout();
		pack();
	}

    public void widgetDisposed(DisposeEvent e) {
        for (Image im : imageCache.values()) {
            im.dispose();
        }
    }
    
    

}
