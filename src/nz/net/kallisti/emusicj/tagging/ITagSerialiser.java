package nz.net.kallisti.emusicj.tagging;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This serialises and deserialises {@link ITagData} instances to XML for
 * storage between DLM sessions. The implementation of this should be compatible
 * with the implementations that created the objects.
 * </p>
 * 
 * @author robin
 */
public interface ITagSerialiser {

	/**
	 * Converts an {@link ITagData} instance into a DOM, writing it to the
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
	public void serialise(Element e, Document doc, ITagData data)
			throws IllegalArgumentException;

	/**
	 * Reads an element and creates an {@link ITagData} from it.
	 * 
	 * @param e
	 *            the element containing the data
	 * @return an {@link ITagData} object
	 */
	public ITagData deserialise(Element e);

}
