package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * <p>This is a SWT widget that displays the progress of a download. 
 * It consists of a label, and below that a progress bar.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class DownloadDisplay extends Composite implements IDownloadMonitorListener {



    private Label label;
    private ProgressBar progBar;
    private IDownloadMonitor monitor;
    
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
        progBar = new ProgressBar(this, SWT.SMOOTH | SWT.HORIZONTAL);
        pack();
    }
    
    public void setDownloadMonitor(IDownloadMonitor mon) {
        this.monitor = mon;
        progBar.setMinimum(0);
        progBar.setMaximum(100);
        monitorStateChanged(mon);
        mon.addListener(this);
        int pc = (int)mon.getDownloadPercent();
        if (pc != -1)
            progBar.setSelection(pc);
        // We now spin off a thread that polls the download progress every
        // seconds or so and updates the progress bar
        PollThread thread = new PollThread(this);
        thread.start();
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
        SWTView.asyncExec(new Runnable() {
            public void run() {
                label.setText(text.toString());
                DownloadDisplay.this.layout();
            }
        });        
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
     * <p>This thread will periodically poll the monitor's progress and
     * update the progress bar accordingly</p>
     */
    private class PollThread extends Thread {

        private DownloadDisplay parent;

        /**
         * Creates the thread object
         */
        public PollThread(DownloadDisplay parent) {
            super();
            this.parent = parent;
        }
        
        public void run() {
            int oldPerc = (int)monitor.getDownloadPercent();
            while (!parent.isDisposed()) {
                if (monitor.getDownloadState() == DLState.DOWNLOADING) {
                    int perc = (int)monitor.getDownloadPercent();
                    if (perc != oldPerc) {
                        parent.updateProgressBar(perc);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }

    }



}
