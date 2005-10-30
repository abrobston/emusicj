package nz.net.kallisti.emusicj.download;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Element;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public class HTTPMusicDownloader extends HTTPDownloader implements
		IMusicDownloader {
	
	public HTTPMusicDownloader(URL url,
			int trackNum, String songName, String album, String artist) {
		super(url, trackNum, songName, album, artist);		
	}
	
	public HTTPMusicDownloader(Element el) throws MalformedURLException {
		super(el);
	}

}
