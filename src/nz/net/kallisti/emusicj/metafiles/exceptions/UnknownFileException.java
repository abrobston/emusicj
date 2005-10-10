package nz.net.kallisti.emusicj.metafiles.exceptions;

/**
 * <p>This exception indicates that the provided file was not a known metafile.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class UnknownFileException extends RuntimeException {

    public UnknownFileException(String message) {
        super(message);
    }

}
