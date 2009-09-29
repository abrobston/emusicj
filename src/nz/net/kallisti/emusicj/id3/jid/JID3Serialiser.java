package nz.net.kallisti.emusicj.id3.jid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3Serialiser;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Serialises/deserialises a JID3-compatible ID3 data object
 * </p>
 * 
 * @author robin
 */
public class JID3Serialiser implements IID3Serialiser {

	private final Logger logger;
	private final JID3Utils utils;

	@Inject
	public JID3Serialiser(Provider<ISimpleDownloader> dlProv) {
		logger = LogUtils.getLogger(this);
		utils = new JID3Utils(dlProv);
	}

	public IID3Data deserialise(Element el) {
		NodeList childlings = el.getChildNodes();
		JID3Data id3 = new JID3Data(utils);
		for (int i = 0; i < childlings.getLength(); i++) {
			Node typeNode = childlings.item(i);
			String type;
			try {
				type = typeNode.getAttributes().getNamedItem("code")
						.getNodeValue();
			} catch (NullPointerException e) {
				logger.warning("Unknown data in ID3 description, skipping");
				continue;
			}
			// Now build the list from the children of this
			NodeList dataNl = typeNode.getChildNodes();
			List<String> values = new ArrayList<String>(dataNl.getLength());
			for (int d = 0; d < dataNl.getLength(); d++) {
				Node datum = dataNl.item(d);
				if (!datum.getNodeName().equals("datum"))
					continue;
				values.add(datum.getTextContent());
			}
			id3.addFrame(type, values);
		}
		return id3;
	}

	public void serialise(Element e, Document doc, IID3Data data)
			throws IllegalArgumentException {
		if (!(data instanceof JID3Data))
			throw new IllegalArgumentException(
					"Supplied data object is not of type JID3Data, it is "
							+ data.getClass());
		JID3Data id3 = (JID3Data) data;
		Element id3El = doc.createElement("id3");
		e.appendChild(id3El);
		Map<String, Set<List<String>>> vals = id3.frames;
		for (String t : vals.keySet()) {
			Set<List<String>> set = vals.get(t);
			for (List<String> list : set) {
				Element typeEl = doc.createElement("type");
				id3El.appendChild(typeEl);
				typeEl.setAttribute("code", t);
				for (String datum : list) {
					Element d = doc.createElement("datum");
					typeEl.appendChild(d);
					d.setTextContent(datum);
				}
			}
		}
	}

}
