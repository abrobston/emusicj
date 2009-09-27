package nz.net.kallisti.emusicj.id3;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This serialises and deserialises {@link IID3Data} instances to XML for
 * storage between DLM sessions. The implementation of this should be compatible
 * with the implementations that created the objects.
 * </p>
 * 
 * @author robin
 */
public interface IID3Serialiser {

	/**
	 * Converts an <code>IID3Data</code> instance into a DOM, writing it to the
	 * provided element.
	 * 
	 * @param e
	 *            the element to write to
	 * @param doc
	 *            the document that this DOM is from
	 * @param data
	 *            the data to convert
	 * @throws IllegalArgumentException
	 *             if the type of <code>data</code> isn't understood by the
	 *             serialiser implementation
	 */
	public void serialise(Element e, Document doc, IID3Data data)
			throws IllegalArgumentException;

	/**
	 * Reads an element and creates and <code>IID3Data</code> from it.
	 * 
	 * @param e
	 *            the element containing the data
	 * @return an IID3Data object
	 */
	public IID3Data deserialise(Element e);

}
