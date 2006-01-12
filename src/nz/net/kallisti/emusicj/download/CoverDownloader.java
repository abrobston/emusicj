package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Element;

/**
 *
 * 
 * $Id: CoverDownloader.java 100 2006-01-08 11:11:54Z robin $
 *
 * @author robin
 */
public class CoverDownloader extends HTTPDownloader {

	/**
	 * @param url
	 * @param outputFile
	 */
	public CoverDownloader(URL url, File outputFile) {
		super(url, outputFile);
		// TODO Auto-generated constructor stub
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
