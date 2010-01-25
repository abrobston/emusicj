package nz.net.kallisti.emusicj.tagging;

import org.w3c.dom.Node;

/**
 * <p>
 * Implementations of this will take an XML document node from the metafile and
 * produce an {@link ITagData} instance from it. Note that different
 * implementations may have different fields that they require, hence the
 * abstraction here. That said, they should all be able to support a common
 * standard set.
 * </p>
 * 
 * @author robin
 */
public interface ITagFromXML {

	/**
	 * Given an XML node, this produces an object with all the tag data in it.
	 * 
	 * @param tagNode
	 *            the data source
	 * @return the tag data
	 */
	public ITagData getData(Node tagNode);

	/**
	 * Specifies if a file with the provided name will be supported. Note that
	 * this file may not really exist, and in fact it may only be the extension
	 * part.
	 * 
	 * @param filename
	 *            the filename to check
	 * @return <code>true</code> if this file type is supported,
	 *         <code>false</code> otherwise.
	 */
	public boolean supportedFile(String filename);

}
