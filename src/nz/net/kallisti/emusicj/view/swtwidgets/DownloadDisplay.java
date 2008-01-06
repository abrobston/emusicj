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

import java.util.HashSet;
import java.util.Set;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.ISelectableControl;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.ISelectionEvent;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.ISelectionListener;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionFromMouseEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * <p>
 * This is a SWT widget that displays the progress of a download. It consists of
 * a label, and below that a progress bar.
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class DownloadDisplay extends Composite implements
		IDownloadMonitorListener, ISelectableControl {

	private final Label titleLabel;
	private final Label statusLabel;
	private final Composite labelArea;
	private final ProgressBar progBar;
	private IDownloadMonitor monitor;
	private PollThread pThread;
	private Color oldBG;
	private Color oldLabelBG;
	private Color oldLabelFG;
	private String lblName;
	private String lblState;
	private String lblProgress;
	private final Set<ISelectionListener> selectionListeners = new HashSet<ISelectionListener>(
			1);
	private boolean selected;

	/**
	 * This constructor initialises the display, creating the parts of it and so
	 * forth.
	 * 
	 * @param parent
	 * @param style
	 */
	public DownloadDisplay(Composite parent, int style, Display display) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);
		labelArea = new Composite(this, SWT.NONE);
		labelArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				notifySelectionListeners(new SelectionFromMouseEvent(e,
						DownloadDisplay.this));
			}
		});
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		labelArea.setLayout(gridLayout);
		labelArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		titleLabel = new Label(labelArea, 0);
		titleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				notifySelectionListeners(new SelectionFromMouseEvent(e,
						DownloadDisplay.this));
			}
		});
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.LEFT;
		gd.grabExcessHorizontalSpace = true;
		titleLabel.setLayoutData(gd);

		statusLabel = new Label(labelArea, 0);
		statusLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				notifySelectionListeners(new SelectionFromMouseEvent(e,
						DownloadDisplay.this));
			}
		});
		Font initialFont = statusLabel.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(SWT.BOLD);
		}
		Font newFont = new Font(display, fontData);
		statusLabel.setFont(newFont);

		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		gd.minimumWidth = SWT.DEFAULT;
		statusLabel.setLayoutData(gd);

		progBar = new ProgressBar(this, SWT.SMOOTH | SWT.HORIZONTAL);
		progBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				notifySelectionListeners(new SelectionFromMouseEvent(e,
						DownloadDisplay.this));
			}
		});
		progBar
				.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
						false));
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				notifySelectionListeners(new SelectionFromMouseEvent(e,
						DownloadDisplay.this));
			}
		});
		layout();
	}

	public void setDownloadMonitor(IDownloadMonitor mon) {
		this.monitor = mon;
		progBar.setMinimum(0);
		progBar.setMaximum(100);
		lblName = mon.getName();
		mon.addStateListener(this);
		monitorStateChanged(mon);
		int pc = (int) mon.getDownloadPercent();
		if (pc != -1)
			progBar.setSelection(pc);
		// We now spin off a thread that polls the download progress every
		// seconds or so and updates the progress bar
		if (pThread != null)
			pThread.finish();
		pThread = new PollThread(this);
		pThread.start();
		layout();
	}

	public IDownloadMonitor getDownloadMonitor() {
		return monitor;
	}

	/**
	 * The state of the monitor has changed, so lets update the label
	 * accordingly
	 * 
	 * @param mon
	 *            the monitor. Better match the one we expect.
	 */
	public void monitorStateChanged(IDownloadMonitor mon) {
		assert mon == monitor : "Event received from unknown monitor";
		DLState state = monitor.getDownloadState();
		if (state == DLState.NOTSTARTED) {
			lblState = "Waiting";
			lblProgress = "";
		} else if (state == DLState.CONNECTING) {
			lblState = "Connecting";
			lblProgress = "";
		} else if (state == DLState.DOWNLOADING) {
			lblState = "Downloading";
		} else if (state == DLState.PAUSED) {
			lblState = "Paused";
		} else if (state == DLState.CANCELLED) {
			lblState = "Cancelled";
			lblProgress = "";
		} else if (state == DLState.FINISHED) {
			lblState = "Completed";
			lblProgress = "";
		} else if (state == DLState.FAILED) {
			lblState = "Failed";
			lblProgress = "(" + monitor.getFailureCount() + ")";
		}
		if (pThread != null)
			pThread.interrupt();
		displayLabel();
	}

	private void displayLabel() {
		final StringBuffer text = new StringBuffer();
		text.append(lblState);
		if (lblProgress != null && !lblProgress.equals(""))
			text.append(" " + lblProgress);
		if (!isDisposed()) {
			SWTView.asyncExec(new Runnable() {
				public void run() {
					if (!titleLabel.isDisposed())
						titleLabel.setText(lblName);
					if (!statusLabel.isDisposed())
						statusLabel.setText(text.toString());
					if (!DownloadDisplay.this.isDisposed()) {
						DownloadDisplay.this.layout();
						titleLabel.pack();
						statusLabel.pack();
						labelArea.pack();
						layout();
					}
				}
			});
		}

	}

	/**
	 * @param perc
	 */
	public void updateProgressBar(final int perc) {
		SWTView.asyncExec(new Runnable() {
			public void run() {
				if (!isDisposed())
					progBar.setSelection(perc);
			}
		});
	}

	/**
	 * Stops any threads running
	 */
	public void stop() {
		pThread.finish();
		pThread.interrupt();
	}

	/**
	 * <p>
	 * This thread will periodically poll the monitor's progress and update the
	 * progress bar accordingly
	 * </p>
	 */
	private class PollThread extends Thread {

		private final DownloadDisplay parent;
		private boolean done = false;

		/**
		 * Creates the thread object
		 */
		public PollThread(DownloadDisplay parent) {
			super();
			this.parent = parent;
		}

		@Override
		public void run() {
			int oldPerc = (int) monitor.getDownloadPercent();
			long oldBytesDown = monitor.getBytesDown();
			boolean alreadyFinished = false;
			while (!parent.isDisposed() && !done) {
				if (monitor.getDownloadState() == DLState.DOWNLOADING) {
					int perc = (int) monitor.getDownloadPercent();
					if (perc != oldPerc) {
						parent.updateProgressBar(perc);
						oldPerc = perc;
					}
					long bytesDown = monitor.getBytesDown();
					if (bytesDown != oldBytesDown) {
						if (monitor.getTotalBytes() == -1)
							lblProgress = "(" + (monitor.getBytesDown() / 1024)
									+ "Kb)";
						else
							lblProgress = "(" + (monitor.getBytesDown() / 1024)
									+ "Kb of "
									+ (monitor.getTotalBytes() / 1024) + "Kb)";
						oldBytesDown = bytesDown;
						displayLabel();
					}
				} else if (monitor.getDownloadState() == DLState.FINISHED
						&& !alreadyFinished) {
					alreadyFinished = true;
					parent.updateProgressBar(100);
					lblProgress = "";
					displayLabel();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}

		public synchronized void finish() {
			done = true;
		}

	}

	public void select() {
		if (selected)
			return;
		selected = true;
		oldBG = getBackground();
		// oldProgBG = progBar.getBackground();
		oldLabelBG = titleLabel.getBackground();
		oldLabelFG = titleLabel.getForeground();
		setBackground(SWTView.getSystemColor(SWT.COLOR_LIST_SELECTION));
		labelArea.setBackground(SWTView
				.getSystemColor(SWT.COLOR_LIST_SELECTION));
		// progBar.setBackground(SWTView.getSystemColor(SWT.COLOR_LIST_SELECTION));
		titleLabel.setBackground(SWTView
				.getSystemColor(SWT.COLOR_LIST_SELECTION));
		titleLabel.setForeground(SWTView
				.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
		statusLabel.setBackground(SWTView
				.getSystemColor(SWT.COLOR_LIST_SELECTION));
		statusLabel.setForeground(SWTView
				.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
	}

	public void unselect() {
		if (!selected)
			return;
		selected = false;
		setBackground(oldBG);
		labelArea.setBackground(oldBG);
		// progBar.setBackground(oldProgBG);
		titleLabel.setBackground(oldLabelBG);
		titleLabel.setForeground(oldLabelFG);
		statusLabel.setBackground(oldLabelBG);
		statusLabel.setForeground(oldLabelFG);
	}

	public void addSelectionListener(ISelectionListener listener) {
		if (!isDisposed()) {
			synchronized (selectionListeners) {
				selectionListeners.add(listener);
			}
		}
	}

	public void removeSelectionListener(ISelectionListener listener) {
		if (!isDisposed()) {
			synchronized (selectionListeners) {
				selectionListeners.remove(listener);
			}
		}
	}

	public void notifySelectionListeners(ISelectionEvent e) {
		synchronized (selectionListeners) {
			for (ISelectionListener listener : selectionListeners) {
				listener.widgetSelected(e);
			}
		}
	}
}
