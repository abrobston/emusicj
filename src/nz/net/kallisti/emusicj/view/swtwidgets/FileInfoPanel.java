/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */
package nz.net.kallisti.emusicj.view.swtwidgets;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.download.IDisplayableDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.view.SWTUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * This class displays information about a file (as represented by an
 * IDownloader instance). Once it is in place, a call to {@link setDownloader}
 * defines what to display. It keeps an image cache for album covers.
 * 
 * $Id$
 * 
 * @author robin
 */
public class FileInfoPanel extends Composite implements DisposeListener {

	// private ScrolledComposite displayArea;
	private final Composite imageArea;
	private final Composite textArea;
	private final Label imageLabel;
	private final Display display;
	private final Hashtable<File, Image> imageCache;
	private final ArrayList<Label> labels = new ArrayList<Label>();
	private final Logger logger;

	/**
	 * Creates an instance of FileInfoPanel with the provided parent and style.
	 * 
	 * @param parent
	 *            the parent of this widget
	 * @param style
	 *            the style.
	 */
	public FileInfoPanel(Composite parent, int style, Display display) {
		super(parent, style);
		logger = LogUtils.getLogger(this);
		imageCache = new Hashtable<File, Image>();
		addDisposeListener(this);
		this.display = display;
		this.setLayout(new GridLayout(2, false));
		imageArea = new Composite(this, SWT.NONE);
		imageArea.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		imageArea.setLayout(new GridLayout(1, false));
		imageLabel = new Label(imageArea, SWT.NONE);
		textArea = new Composite(this, SWT.NONE);
		textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textArea.setLayout(new GridLayout(2, false));
		imageArea.layout();
		textArea.layout();
		layout();
		pack();
	}

	public void setDownloader(IDownloadMonitor dl) {
		// Note that the download monitor contains all the facts to display
		// about a file, they aren't defined explicitly here.

		// Dispose all the labels being displayed at the moment...
		for (Label l : labels) {
			l.dispose();
		}
		imageLabel.setImage(null);
		labels.clear();
		if (dl instanceof IDisplayableDownloadMonitor) {
			IDisplayableDownloadMonitor ddl = (IDisplayableDownloadMonitor) dl;
			String[][] textToDisplay = ddl.getText();
			File coverFile = ddl.getImageFile();
			if (coverFile != null && !coverFile.toString().equals("")
					&& coverFile.exists()) {
				Image im = imageCache.get(coverFile);
				if (im == null) {
					try {
						im = new Image(display, ddl.getImageFile().toString());
						imageCache.put(coverFile, im);
					} catch (SWTException e) {
						// Sometimes an error here can cause the whole system
						// to fall over, so we catch it and fail more
						// gracefully
						logger.log(Level.WARNING,
								"An error occurred loading the image: "
										+ ddl.getImageFile().toString(), e);
						im = null;
					} catch (SWTError e) {
						// This seems to trigger in some cases in Windows 7, it
						// seems to be related to the size of the image: too big
						// and it blows up.
						logger.log(Level.WARNING,
								"A serious error occurred loading the image: "
										+ ddl.getImageFile().toString(), e);
					}
				}
				imageLabel.setImage(im);
			} else {
				imageLabel.setImage(null);
			}
			// create the labels
			for (int i = 0; i < textToDisplay.length; i++) {
				if (textToDisplay[i][0] != null) {
					Label l = new Label(textArea, SWT.NONE);
					String text = SWTUtils.deMonic(textToDisplay[i][0]);
					l.setText(text + ":");
					labels.add(l);
					l = new Label(textArea, SWT.NONE);
					text = SWTUtils.deMonic(textToDisplay[i][1]);
					l.setText(text);
					labels.add(l);
				}
			}
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
		for (Label l : labels) {
			l.dispose();
		}
		labels.clear();
	}

}
