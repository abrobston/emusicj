package nz.net.kallisti.emusicj.id3.jid;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.id3.IID3Data;
import nz.net.kallisti.emusicj.id3.IID3Frame;
import nz.net.kallisti.emusicj.id3.IID3ToMP3;
import nz.net.kallisti.emusicj.misc.LogUtils;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v1.ID3V1_1Tag;
import org.blinkenlights.jid3.v2.ID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

/**
 * <p>
 * An MP3 tag writer that uses JID3 to do the writing.
 * </p>
 * 
 * @author robin
 */
public class JID3ToMP3 implements IID3ToMP3 {

	private final Logger logger;

	public JID3ToMP3() {
		logger = LogUtils.getLogger(this);
	}

	public void writeMP3(IID3Data id3Data, File file) throws RuntimeException,
			IOException {
		if (!(id3Data instanceof JID3Data)) {
			throw new RuntimeException("id3Data is of type "
					+ id3Data.getClass() + ", when it should be JID3Data");
		}
		MediaFile media = new MP3File(file);
		ID3V2_3_0Tag currentTag = null;
		try {
			currentTag = (ID3V2_3_0Tag) media.getID3V2Tag();
		} catch (ID3Exception e) {
			logger.log(Level.WARNING, "Unable to read tags from " + file, e);
		}
		if (currentTag == null) {
			currentTag = new ID3V2_3_0Tag();
		}
		// Add the frames to the tag
		for (String type : id3Data.getFrameTypes()) {
			for (IID3Frame fr : id3Data.getFramesForType(type)) {
				if (!(fr instanceof JID3Frame)) {
					throw new RuntimeException(
							"Got frame object of unknown type " + fr.getClass());
				}
				JID3Frame frameHolder = (JID3Frame) fr;
				ID3V2Frame frame = frameHolder.getFrame();
				try {
					currentTag.addFrame(frame);
				} catch (ID3Exception e) {
					logger.log(Level.SEVERE, "Error adding frame to tag, type="
							+ type + ", frame type=" + frame.getClass() + " ["
							+ frame + "]");
				}
			}
		}
		media.setID3Tag(currentTag);
		v2ToV1(currentTag, media);
		try {
			media.sync();
		} catch (ID3Exception e) {
			throw new IOException("Unable to save tags on MP3 file: "
					+ e.getMessage());
		}
	}

	/**
	 * This takes the details in the supplied ID3v2 tag, and updates the media's
	 * v1 tag to match
	 * 
	 * @param v2Tag
	 *            the ID3v2 tag with the information in it
	 * @param media
	 *            the media file to update
	 */
	private void v2ToV1(ID3V2_3_0Tag v2Tag, MediaFile media) {
		ID3V1Tag v1Tag = null;
		try {
			v1Tag = media.getID3V1Tag();
		} catch (ID3Exception e) {
			logger.log(Level.WARNING,
					"Error reading ID3v1 tags, starting fresh", e);
		}
		if (v1Tag == null)
			v1Tag = new ID3V1_1Tag();
		String album = v2Tag.getAlbum();
		String artist = v2Tag.getArtist();
		String comment = v2Tag.getComment();
		String title = v2Tag.getTitle();
		Integer trackNumber = null;
		try {
			v2Tag.getTrackNumber();
		} catch (ID3Exception e) {
			// It was unset
		}
		Integer year = null;
		try {
			v2Tag.getYear();
		} catch (ID3Exception e) {
			// It was unset
		}
		if (album != null)
			v1Tag.setAlbum(album);
		if (artist != null)
			v1Tag.setArtist(artist);
		if (comment != null)
			v1Tag.setComment(comment);
		if (title != null)
			v1Tag.setTitle(title);
		if (year != null)
			v1Tag.setYear(String.valueOf(year));
		if (v1Tag instanceof ID3V1_1Tag) {
			ID3V1_1Tag v11Tag = (ID3V1_1Tag) v1Tag;
			if (trackNumber != null)
				try {
					v11Tag.setAlbumTrack(trackNumber);
				} catch (ID3Exception e) {
					logger.log(Level.WARNING,
							"Unable to set ID3v1 track number to "
									+ trackNumber);
				}
		}
		media.setID3Tag(v1Tag);
	}

}
