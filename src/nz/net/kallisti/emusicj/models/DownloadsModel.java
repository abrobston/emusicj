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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.strings.IStrings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This is the download model. It keeps tabs on what downloads exists and
 * notifies listeners of changes.
 * </p>
 * 
 * $Id$
 * 
 * @author robin
 */
public class DownloadsModel implements IDownloadsModel {

	/**
	 * Contains the downloads as an ordered list
	 */
	private final List<IDownloader> downloads;
	/**
	 * Contains the listeners listening to changes in the state of the model
	 */
	private final List<IDownloadsModelListener> listeners;
	/**
	 * Contains the download in a way that can be searched quickly. <i>Must</i>
	 * be kept synchronous with {@link downloads} at all times. Note that both
	 * the key and the value must be the same. This allows the specific instance
	 * to be retrieved, as downloader hash and equals tests are done based only
	 * on the output file.
	 */
	private final Map<IDownloader, IDownloader> dlsHash;
	private final Provider<IMusicDownloader> musicDownloaderProvider;
	private final Provider<ICoverDownloader> coverDownloaderProvider;
	private final Provider<IDownloader> downloaderProvider;
	private final IStrings strings;

	/**
	 * Initialise the class, Guice supplies providers to create the instances of
	 * things when it needs to
	 */
	@Inject
	public DownloadsModel(Provider<IMusicDownloader> musicDownloaderProvider,
			Provider<ICoverDownloader> coverDownloaderProvider,
			Provider<IDownloader> downloaderProvider, IStrings strings) {
		this.musicDownloaderProvider = musicDownloaderProvider;
		this.coverDownloaderProvider = coverDownloaderProvider;
		this.downloaderProvider = downloaderProvider;
		this.strings = strings;
		downloads = Collections.synchronizedList(new ArrayList<IDownloader>());
		dlsHash = Collections
				.synchronizedMap(new HashMap<IDownloader, IDownloader>());
		listeners = Collections
				.synchronizedList(new ArrayList<IDownloadsModelListener>());
	}

	/**
	 * Writes the state of the downloaders into the provided stream. The output
	 * into the stream is XML.
	 * 
	 * @param str
	 *            the stream to save to
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
		Element el = doc.createElement(strings.getXMLBaseNodeName());
		IDownloader[] dls = new IDownloader[downloads.size()];
		dls = downloads.toArray(dls);
		for (int i = 0; i < dls.length; i++) {
			if (dls[i] instanceof MusicDownloader) {
				Element dlEl = doc.createElement("MusicDownloader");
				((MusicDownloader) dls[i]).saveTo(dlEl, doc);
				el.appendChild(dlEl);
			} else if (dls[i] instanceof CoverDownloader) {
				Element dlEl = doc.createElement("CoverDownloader");
				((CoverDownloader) dls[i]).saveTo(dlEl, doc);
				el.appendChild(dlEl);
			} else if (dls[i] instanceof HTTPDownloader) {
				Element dlEl = doc.createElement("HTTPDownloader");
				((HTTPDownloader) dls[i]).saveTo(dlEl, doc);
				el.appendChild(dlEl);
			}
		}
		doc.appendChild(el);
		TransformerFactory tFactory = TransformerFactory.newInstance();
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
		} catch (IOException e) {
		}
		return true;
	}

	public void loadState(InputStream str) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(str);
			Node root = doc.getDocumentElement();
			if (!(root.getNodeType() == Node.ELEMENT_NODE && root.getNodeName()
					.equals(strings.getXMLBaseNodeName()))) {
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
				if (dlNode.getNodeName().equals("MusicDownloader")
						|| (dlNode.getNodeName().equals("HTTPMusicDownloader"))) { // backwards
					// compatibility
					// after
					// class
					// rename
					IMusicDownloader dl = musicDownloaderProvider.get();
					dl.setDownloader((Element) dlNode);
					downloads.add(dl);
					dlsHash.put(dl, dl);
				} else if (dlNode.getNodeName().equals("CoverDownloader")) {
					ICoverDownloader dl = coverDownloaderProvider.get();
					dl.setDownloader((Element) dlNode);
					downloads.add(dl);
					dlsHash.put(dl, dl);
				} else if (dlNode.getNodeName().equals("HTTPDownloader")) {
					IDownloader dl = downloaderProvider.get();
					dl.setDownloader((Element) dlNode);
					downloads.add(dl);
					dlsHash.put(dl, dl);
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

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.net.kallisti.emusicj.models.IDownloadsModel#addDownload(nz.net.kallisti
	 * .emusicj.download.IMusicDownloader)
	 */
	public void addDownload(IDownloader dl) {
		if (!dlsHash.containsKey(dl)) {
			downloads.add(dl);
			dlsHash.put(dl, dl);
			notifyListeners();
		} else {
			IDownloader existingDl = dlsHash.get(dl);
			existingDl.updateFrom(dl);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.net.kallisti.emusicj.models.IDownloadsModel#removeDownloads(java.util
	 * .List)
	 */
	public void removeDownloads(List<IDownloader> toRemove) {
		if (toRemove.size() != 0) {
			for (IDownloader dl : toRemove) {
				if (dlsHash.containsKey(dl)) {
					downloads.remove(dl);
					dlsHash.remove(dl);
				}
			}
			notifyListeners();
		}
	}

	public void removeDownload(IDownloader dl) {
		if (dlsHash.containsKey(dl)) {
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
