package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TypedListener;

/**
 * <p>This is a SWT widget that displays the progress of a download. 
 * It consists of a label, and below that a progress bar.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class DownloadDisplay extends Composite 
implements IDownloadMonitorListener, ISelectableControl {



    private Label label;
    private ProgressBar progBar;
    private IDownloadMonitor monitor;
    private PollThread pThread;
	private Color oldBG;
	private Color oldProgBG;
	private Color oldLabelBG;
	/**
     * This constructor initialises the display, creating the parts of it and
     * so forth.
     * @param parent
     * @param style
     */
    public DownloadDisplay(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.setLayout(gridLayout);
        label = new Label(this, 0);
        label.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
        		notifyListeners(SWT.Selection, new Event());
        	}
        });
        progBar = new ProgressBar(this, SWT.SMOOTH | SWT.HORIZONTAL);
        progBar.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
        		notifyListeners(SWT.Selection, new Event());
        	}
        });
        progBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        this.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
        		notifyListeners(SWT.Selection, new Event());
        	}
        });
        layout();
    }
    
    public void setDownloadMonitor(IDownloadMonitor mon) {
        this.monitor = mon;
        progBar.setMinimum(0);
        progBar.setMaximum(100);
        monitorStateChanged(mon);
        mon.addStateListener(this);
        int pc = (int)mon.getDownloadPercent();
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
     * @param mon the monitor. Better match the one we expect.
     */
    public void monitorStateChanged(IDownloadMonitor mon) {
        assert mon == monitor : "Event received from unknown monitor";
        final StringBuffer text = new StringBuffer(mon.getName()+" - ");
        DLState state = mon.getDownloadState();
        if (state == DLState.NOTSTARTED) { text.append("Waiting"); } 
        else if (state == DLState.CONNECTING) { text.append("Connecting"); }
        else if (state == DLState.DOWNLOADING) { text.append("Downloading"); }
        else if (state == DLState.PAUSED) { text.append("Paused"); }
        else if (state == DLState.STOPPED) { text.append("Stopped"); }
        else if (state == DLState.FINISHED) { text.append("Finished"); }
        else if (state == DLState.FAILED) { text.append("Failed"); }
        if (!isDisposed()) {
        	SWTView.asyncExec(new Runnable() {
        		public void run() {
        			label.setText(text.toString());
        			DownloadDisplay.this.layout();
        			//DownloadDisplay.this.pack();
        		}
        	});
        };        
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
     * <p>This thread will periodically poll the monitor's progress and
     * update the progress bar accordingly</p>
     */
    private class PollThread extends Thread {

        private DownloadDisplay parent;
		private boolean done = false;

        /**
         * Creates the thread object
         */
        public PollThread(DownloadDisplay parent) {
            super();
            this.parent = parent;
        }
        
        public void run() {
            int oldPerc = (int)monitor.getDownloadPercent();
            while (!parent.isDisposed() && !done) {
                if (monitor.getDownloadState() == DLState.DOWNLOADING) {
                    int perc = (int)monitor.getDownloadPercent();
                    if (perc != oldPerc) {
                        parent.updateProgressBar(perc);
                        oldPerc = perc;
                    }
                } else if (monitor.getDownloadState() == DLState.FINISHED) {
                	parent.updateProgressBar(100);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
        
        public synchronized void finish() {
        	done  = true;
        }

    }

	public void select() {
		oldBG = getBackground();
        oldProgBG = progBar.getBackground();
        oldLabelBG = label.getBackground();
        setBackground(SWTView.getSystemColor(SWT.COLOR_LIST_SELECTION));
        progBar.setBackground(SWTView.getSystemColor(SWT.COLOR_LIST_SELECTION));
        label.setBackground(SWTView.getSystemColor(SWT.COLOR_LIST_SELECTION));

	}

	public void unselect() {
		setBackground(oldBG);
		progBar.setBackground(oldProgBG);
		label.setBackground(oldLabelBG);
	}

	public void addSelectionListener(SelectionListener listener) {
		addListener(SWT.Selection, new TypedListener(listener));
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		removeListener(SWT.Selection, listener);
	}
}
