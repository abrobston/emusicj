package nz.net.kallisti.emusicj.misc;

/**
 * <p>
 * This is thrown when an attempt to open a browser fails
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class BrowserException extends Exception {

	public BrowserException() {
		super();
	}

	public BrowserException(String message, Throwable cause) {
		super(message, cause);
	}

	public BrowserException(String message) {
		super(message);
	}

	public BrowserException(Throwable cause) {
		super(cause);
	}

}
