package nz.net.kallisti.emusicj.id3;

import org.w3c.dom.Node;

/**
 * <p>
 * Implementations of this will take an XML document node and produce an
 * IID3Data instance from it. Note that different implementations may have
 * different fields that they require, hence the abstraction here.
 * </p>
 * 
 * @author robin
 */
public interface IID3FromXML {

	/**
	 * Given an XML node, this produces an object with all the ID3 data in it.
	 * 
	 * @param id3Node
	 *            the data source
	 * @return the ID3 data
	 */
	public IID3Data getData(Node id3Node);

}
