package nz.net.kallisti.emusicj.ipc;

/**
 * <p>A listener for IPC events. The data is retreived as an array of strings,
 * split an newlines.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IIPCListener {

	public void ipcData(String[] data);
	
}
