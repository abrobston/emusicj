package nz.net.kallisti.emusicj.mediaplayer;

/**
 * <p>
 * This indicates that an attempt was made to select an invalid player type.
 * </p>
 * 
 * @author robin
 */
public class UnknownPlayerException extends Exception {

	public UnknownPlayerException() {
		super();
	}

	public UnknownPlayerException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownPlayerException(String message) {
		super(message);
	}

	public UnknownPlayerException(Throwable cause) {
		super(cause);
	}

}
