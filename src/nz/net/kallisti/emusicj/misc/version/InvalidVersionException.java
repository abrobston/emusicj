package nz.net.kallisti.emusicj.misc.version;

/**
 * <p>
 * This exception indicates that there was an error parsing a version number
 * </p>
 * 
 * @author robin
 */
public class InvalidVersionException extends Exception {

	public InvalidVersionException() {
		super();
	}

	public InvalidVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidVersionException(String message) {
		super(message);
	}

	public InvalidVersionException(Throwable cause) {
		super(cause);
	}

}
