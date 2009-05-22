package nz.net.kallisti.emusicj.misc;

/**
 * <p>
 * Called if the attempt to open the folder fails for some reason.
 * </p>
 * 
 * @author robin
 */
public class FolderOpenerException extends Exception {

	public FolderOpenerException() {
		super();
	}

	public FolderOpenerException(String message, Throwable cause) {
		super(message, cause);
	}

	public FolderOpenerException(String message) {
		super(message);
	}

	public FolderOpenerException(Throwable cause) {
		super(cause);
	}

}
