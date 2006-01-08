package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public class MusicDownloadMonitor extends HTTPDownloadMonitor 
	implements IMusicDownloadMonitor {
	
	public MusicDownloadMonitor() {
		super();
	}
	
	public MusicDownloadMonitor(MusicDownloader downloader) {
		super(downloader);
	}
	
	public String getName() {
		return getMusicDownloader().getTrackName()+" - "+
		getMusicDownloader().getAlbumName() + " - "+
		getMusicDownloader().getArtistName();
	}
	
	public IMusicDownloader getMusicDownloader() {
		return (IMusicDownloader)getDownloader();
	}
	
	public String getAlbumName() {
		return getMusicDownloader().getAlbumName();
	}
	
	public String getArtistName() {
		return getMusicDownloader().getArtistName();
	}
	
	public String getTrackName() {
		return getMusicDownloader().getTrackName();
	}
	
	public int getTrackNum() {
		return getMusicDownloader().getTrackNum();
	}

	public File getImageFile() {
		return getMusicDownloader().getCoverArt();
	}
	
//	public File getCoverArt() {
//		return getImageFile();
//	}
	
	public String[][] getText() {
		String[][] res = new String[3][2];
		res[0][0] = "Title"; res[0][1] = getTrackName();
		res[1][0] = "Album"; res[1][1] = getAlbumName();
		res[2][0] = "Artist"; res[2][1] = getArtistName();		
		return res;
	}
	
}
