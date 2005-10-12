package nz.net.kallisti.emusicj.download.test;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;

/**
 * <p>This simulates a download in progress</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class TestDownloader implements IDownloader {

    private int inital;
    private double speed;

    String name;
    double pc;
    DLState state;
    private TestDownloadMonitor monitor;
    private DownloadThread dlThread;

    /**
     * 
     */
    public TestDownloader() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param string
     * @param i
     * @param d
     */
    public TestDownloader(String name, int i, int d) {
        this.name = name;
        this.inital = i;
        this.speed = d;
    }

    public IDownloadMonitor getMonitor() {
        if (monitor == null)
            monitor = new TestDownloadMonitor(this);
        return monitor;
    }

    public void start() {
        dlThread = new DownloadThread();
        dlThread.start();
        state = DLState.DOWNLOADING;
        if (monitor != null)
            monitor.setState(state);
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloader#stop()
     */
    public void stop() {
        dlThread.finish();        
        state = DLState.STOPPED;
        if (monitor != null)
            monitor.setState(state);
    }

    /**
     * 
     */
    public void downloadFinished() {
        dlThread.finish();        
        state = DLState.FINISHED;
        if (monitor != null)
            monitor.setState(state);        
    }

    
    public class DownloadThread extends Thread {

        private boolean done = false;
        
        public void run() {
            try {
                Thread.sleep(inital);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i=0; i<100 && !done; i++) {
                try {
                    Thread.sleep((int)speed);
                    pc = i;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!done)
                downloadFinished();            
        }
        
        public void finish() {
            done = true;
        }
        
    }

}
