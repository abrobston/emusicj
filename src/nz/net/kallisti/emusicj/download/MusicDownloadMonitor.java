package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 *
 * 
 * $Id: MusicDownloadMonitor.java 101 2006-01-10 11:07:33Z robin $
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
		String[][] res = new String[5][2];
		int i=0;
		res[i][0] = "Title"; res[i++][1] = getTrackName();
		res[i][0] = "Album"; res[i++][1] = getAlbumName();
		res[i][0] = "Artist"; res[i++][1] = getArtistName();
		String genre = ((IMusicDownloader)downloader).getGenre();
		if (genre != null)
			res[i][0] = "Genre"; res[i++][1] = genre;
		int dur = ((IMusicDownloader)downloader).getDuration();
		if (dur != -1) {
            int secs=dur%60;
			res[i][0] = "Duration"; res[i++][1] = (dur/60)+":"+(secs<10?"0":"")+secs;
        }
		return res;
	}
	
}
