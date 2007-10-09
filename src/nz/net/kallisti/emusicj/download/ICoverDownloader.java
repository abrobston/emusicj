package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Element;

/**
 * <p>Interface to the downloader to be used for downloading cover art</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public interface ICoverDownloader extends IDownloader {

	/**
	 * @param url
	 * @param outputFile
	 */
	public void setDownloader(URL url, File outputFile);

	/**
	 * @param el
	 * @throws MalformedURLException
	 */
	public void setDownloader(Element el) throws MalformedURLException;
	

}
