package nz.net.kallisti.emusicj.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nz.net.kallisti.emusicj.download.HTTPDownloader;
import nz.net.kallisti.emusicj.download.HTTPMusicDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.test.TestDownloadMonitor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>This is the download model. It keeps tabs on what downloads exists
 * and notifies listeners of changes.</p> 
 * 
 * $Id$
 *
 * @author robin
 */
public class DownloadsModel implements IDownloadsModel {

	List<IDownloader> downloads;
    private List<IDownloadsModelListener> listeners;
    
	/**
	 * Initialise the class, and create some {@link TestDownloadMonitor}s.
	 * @param n the number of monitors to create
	 */
	public DownloadsModel() {
		downloads = Collections.synchronizedList(new ArrayList<IDownloader>());
		listeners = Collections.synchronizedList(new ArrayList<IDownloadsModelListener>());
	}
	
	/**
	 * Restores the download model from an XML element. Only the downloaders
	 * are restored.
	 * @param el the element to do the restore from
	 */
	public DownloadsModel(Element el) {
		
	}
	
	/**
	 * Writes the state of the downloaders into the provided stream. Note that 
	 * currently only {@link HTTPMusicDownloader} is supported. The output
	 * into the stream is XML.
	 * @param str the stream to save to
	 * @return true if everything was OK
	 */
	public boolean saveState(OutputStream str) {		
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Warning: error saving download information");
			e.printStackTrace();
			return false;
		}
		Document doc = builder.newDocument();
	    Element el = doc.createElement("emusicj-state");
		IDownloader[] dls = new IDownloader[downloads.size()];
		dls = downloads.toArray(dls);
		for (int i=0; i<dls.length; i++) {
			if (dls[i] instanceof HTTPMusicDownloader) {
				Element dlEl = doc.createElement("HTTPMusicDownloader");
				((HTTPMusicDownloader)dls[i]).saveTo(dlEl, doc);
				el.appendChild(dlEl);
			} else if (dls[i] instanceof HTTPDownloader) {
                Element dlEl = doc.createElement("HTTPDownloader");
                ((HTTPDownloader)dls[i]).saveTo(dlEl, doc);
                el.appendChild(dlEl);
            }
		}
		doc.appendChild(el);
		TransformerFactory tFactory =
			TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			System.err.println("Warning: error saving download information");
			e.printStackTrace();
			return false;
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(str);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			System.err.println("Warning: error saving download information");
			e.printStackTrace();
			return false;
		}
		try {
			str.close();
		} catch (IOException e) {}
		return true;
	}
	
	public void loadState(InputStream str) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(str);
			Node root = doc.getDocumentElement();
			if (!(root.getNodeType() == Node.ELEMENT_NODE &&
					root.getNodeName().equals("emusicj-state"))) {
				return;
			}
			if (!root.hasChildNodes()) {
				return;
			}
			NodeList dlList = root.getChildNodes(); 
			for (int count = 0; count < dlList.getLength(); count++) {
				Node dlNode = dlList.item(count);
				if (!(dlNode.getNodeType() == Node.ELEMENT_NODE)) {
					continue;
				}
				if (dlNode.getNodeName().equals("HTTPMusicDownloader")) {
				    downloads.add(new HTTPMusicDownloader((Element)dlNode));
                } else if (dlNode.getNodeName().equals("HTTPDownloader")) {
                    downloads.add(new HTTPDownloader((Element)dlNode));
                }
			}
		} catch (Exception e) {
			System.err.println("An error occurred loading the downloads state");
			e.printStackTrace();
			return;
		}
		notifyListeners();
	}	
	public void addListener(IDownloadsModelListener listener) {
	    listeners.add(listener);
	}

	public void removeListener(IDownloadsModelListener listener) {
	    listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.models.IDownloadsModel#getDownloaders()
	 */
	public List<IDownloader> getDownloaders() {		
		return Collections.unmodifiableList(downloads);
	}

	public List<IDownloadMonitor> getDownloadMonitors() {
        ArrayList<IDownloadMonitor> dm = new ArrayList<IDownloadMonitor>();
        for (IDownloader d : downloads)
            dm.add(d.getMonitor());
		return dm;
	}

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.models.IDownloadsModel#addDownload(nz.net.kallisti.emusicj.download.IMusicDownloader)
     */
    public void addDownload(IDownloader dl) {
        downloads.add(dl);
        notifyListeners();
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.models.IDownloadsModel#removeDownloads(java.util.List)
     */
    public void removeDownloads(List<IDownloader> toRemove) {
        if (toRemove.size() != 0) {
            for (IDownloader dl : toRemove)
                downloads.remove(dl);
            notifyListeners();
        }
    }

    public void removeDownload(IDownloader dl) {
        downloads.remove(dl);
        notifyListeners();
    }
    
    private void notifyListeners() {
        for (IDownloadsModelListener l : listeners)
            l.downloadsModelChanged(this);
    }

}
