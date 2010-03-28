package nz.net.kallisti.emusicj.tagging.general;

import nz.net.kallisti.emusicj.tagging.ITagData;

import org.w3c.dom.Node;

/**
 * <p></p>
 *
 * @author robin
 */
public interface IGeneralTagFromXML {

	/**
	 * Given an XML node, this produces an object with all the tag data in it.
	 * 
	 * @param tagNode
	 *            the data source
	 * @param extension
	 *            the extension of the file that this is for
	 * @return the tag data
	 */
	public abstract ITagData getData(Node tagNode, String extension);

}