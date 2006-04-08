package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeTypes;

import org.w3c.dom.Element;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public class CoverDownloader extends HTTPDownloader {

	/**
	 * @param url
	 * @param outputFile
	 */
	public CoverDownloader(URL url, File outputFile) {
		super(url, outputFile, new IMimeType[] {MimeTypes.IMAGES});
	}

	/**
	 * @param el
	 * @throws MalformedURLException
	 */
	public CoverDownloader(Element el) throws MalformedURLException {
		super(el);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void createMonitor() {
        monitor = new CoverDownloadMonitor(this);
    }
    
}
