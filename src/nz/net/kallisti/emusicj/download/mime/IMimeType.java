package nz.net.kallisti.emusicj.download.mime;

import java.awt.datatransfer.MimeTypeParseException;

/**
 * This provides an interface to a MIME type handler, which represents a MIME
 * type and provides operations to compare it.  
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IMimeType {

	/**
	 * Gets the type part of the MIME type as a string. The type part is the 
	 * part to the left of the "/" character, e.g. for text/plain this with
	 * return "text".
	 * @return the type part of this MIME type
	 */
	public String getMimeType();
	
	/**
	 * Gets the subtype part of this MIME type as a string. The type part is the 
	 * part to the right of the "/" character, e.g. for text/plain this will
	 * return "plain".
	 * @return the subtype of this MIME type
	 */
	public String getMimeSubtype();
	
	/**
	 * Gets the complete MIME type as a string, e.g. for text/plain, will return
	 * "text/plain"
	 * @return the MIME type
	 */
	public String toString();
	
	/**
	 * Compares the provided MIME type string to see if it is compatible with
	 * this. This usually means that they are the same, however basic wildcard
	 * support is allowed, so "text/*" matches "text/plain". This works both
	 * ways, i.e. it doesn't matter which one has the wildcard.
	 * @param m the MIME type string to compare to
	 * @return true if they match, otherwise false
	 * @throws MimeTypeParseException if the provided string isn't a valid MIME type
	 */
	public boolean matches(String m) throws MimeTypeParseException;
	
	/**
	 * Returns true if the type part only matches, ignores the subtype
	 * completely.
	 * @param m the string to compare to
	 * @return true if the types match, false otherwise
	 * @throws MimeTypeParseException if the provided string isn't a valid MIME type
	 */
	public boolean typeMatches(String m) throws MimeTypeParseException;
	
}
