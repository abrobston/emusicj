package nz.net.kallisti.emusicj.download.mime;

import java.awt.datatransfer.MimeTypeParseException;

/**
 * This creates a few standard MIME types for easy access and pooling.
 * 
 * $Id:$
 *
 * @author robin
 */
public abstract class MimeTypes {

	public static MimeType IMAGES;
	public static MimeType AUDIO;
	public static MimeType APP_OCTET;

	static {
		try {
			IMAGES = new MimeType("image/*");
			AUDIO = new MimeType("audio/*");
			APP_OCTET = new MimeType("application/octet-stream");
		} catch (MimeTypeParseException e) {
			e.printStackTrace();
		}
	}
	
}
