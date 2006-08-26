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
package nz.net.kallisti.emusicj.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nz.net.kallisti.emusicj.download.CoverDownloader;
import nz.net.kallisti.emusicj.download.HTTPDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
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

	/**
	 * Contains the downloads as an ordered list
	 */
	private List<IDownloader> downloads;
	/**
	 * Contains the listeners listening to changes in the state of the model
	 */
    private List<IDownloadsModelListener> listeners;
    /**
     * Contains the download in a way that can be searched quickly. <i>Must</i>
     * be kept synchronous with {@link downloads} at all times. 
     */
    private Set<IDownloader> dlsHash;
    
	/**
	 * Initialise the class, and create some {@link TestDownloadMonitor}s.
	 * @param n the number of monitors to create
	 */
	public DownloadsModel() {
		downloads = Collections.synchronizedList(new ArrayList<IDownloader>());
		dlsHash = Collections.synchronizedSet(new HashSet<IDownloader>());
		listeners = Collections.synchronizedList(new ArrayList<IDownloadsModelListener>());
	}
	
	/**
	 * Restores the download model from an XML element. Only the downloaders
	 * are restored. (Note: not actually implemented yet)
	 * @param el the element to do the restore from
	 */
	public DownloadsModel(Element el) {
		
	}
	
	/**
	 * Writes the state of the downloaders into the provided stream. Note that 
	 * currently only {@link MusicDownloader} is supported. The output
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
			if (dls[i] instanceof MusicDownloader) {
				Element dlEl = doc.createElement("MusicDownloader");
				((MusicDownloader)dls[i]).saveTo(dlEl, doc);
				el.appendChild(dlEl);
			} else if (dls[i] instanceof CoverDownloader) {
				Element dlEl = doc.createElement("CoverDownloader");
				((CoverDownloader)dls[i]).saveTo(dlEl, doc);
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
				if (dlNode.getNodeName().equals("MusicDownloader") ||
						(dlNode.getNodeName().equals("HTTPMusicDownloader"))) { // backwards compatibility after class rename
					MusicDownloader dl = new MusicDownloader((Element)dlNode);
				    downloads.add(dl);
					dlsHash.add(dl);
				} else if (dlNode.getNodeName().equals("CoverDownloader")) {
					CoverDownloader dl = new CoverDownloader((Element)dlNode);
				    downloads.add(dl);
					dlsHash.add(dl);					
                } else if (dlNode.getNodeName().equals("HTTPDownloader")) {
                		HTTPDownloader dl = new HTTPDownloader((Element)dlNode);
                    downloads.add(dl);
                    dlsHash.add(dl);
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
    		if (!dlsHash.contains(dl)) {
    			downloads.add(dl);
    			dlsHash.add(dl);
    			notifyListeners();
    		}
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.models.IDownloadsModel#removeDownloads(java.util.List)
     */
    public void removeDownloads(List<IDownloader> toRemove) {
        if (toRemove.size() != 0) {
            for (IDownloader dl : toRemove) {
            		if (dlsHash.contains(dl)) {
            			downloads.remove(dl);
            			dlsHash.remove(dl);
            		}
            }
            notifyListeners();
        }
    }

    public void removeDownload(IDownloader dl) {
    		if (dlsHash.contains(dl)) {
    			downloads.remove(dl);
    			dlsHash.remove(dl);
    			notifyListeners();
    		}
    }
    
    private void notifyListeners() {
        for (IDownloadsModelListener l : listeners)
            l.downloadsModelChanged(this);
    }

}
