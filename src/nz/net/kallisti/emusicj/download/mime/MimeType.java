package nz.net.kallisti.emusicj.download.mime;

import java.awt.datatransfer.MimeTypeParseException;

/**
 * This class represents a MIME type such as "text/plain" or "application/*"
 * 
 * $Id:$
 *
 * @author robin
 */
public class MimeType implements IMimeType {

	private String type;
	private String subtype;

	/**
	 * Initialises the instance with a MIME type. This must be in the form
	 * "type/subtype".
	 * @param type the MIME type to create the representation of
	 */
	public MimeType(String type) throws MimeTypeParseException {
		super();
		String[] parts = type.split("/");
		if (parts.length != 2) {
			throw new MimeTypeParseException("A MIME type must have exactly two parts, seperated by a '/'");
		}
		this.type = parts[0];
		this.subtype = parts[1];
	}

	public String getMimeType() {
		return type;
	}

	public String getMimeSubtype() {
		return subtype;
	}

	/**
	 * Compares the provided MIME type string to see if it is compatible with
	 * this. This usually means that they are the same, however basic wildcard
	 * support is allowed, so "text/*" matches "text/plain". This works both
	 * ways, i.e. it doesn't matter which one has the wildcard. Note that a
	 * '*' in the type part is treated in the same way. 
	 * @param m the MIME type string to compare to
	 * @return true if they match, otherwise false
	 * @throws MimeTypeParseException if the provided string isn't a valid mime type
	 */
	public boolean matches(String m) throws MimeTypeParseException {
		String[] parts = m.split("/");
		if (parts.length != 2) {
			throw new MimeTypeParseException("A MIME type must have exactly two " +
					"parts, seperated by a '/'");			
		}
		// Compare the first part
		if (!(parts[0].equals(type) || parts[0].equals("*") || 
				type.equals("*"))) {
			return false;
		}
		// Compare the second part
		if (!(parts[1].equals(subtype) || parts[1].equals("*") || 
				subtype.equals("*"))) {
			return false;
		}
		return true;
	}

	public boolean typeMatches(String m) throws MimeTypeParseException {
		String[] parts = m.split("/");
		if (parts.length != 2) {
			throw new MimeTypeParseException("A MIME type must have exactly two " +
					"parts, seperated by a '/'");			
		}
		// Compare the first part
		if (!(parts[0].equals(type) || parts[0].equals("*") || 
				type.equals("*"))) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return type+"/"+subtype;
	}

}
