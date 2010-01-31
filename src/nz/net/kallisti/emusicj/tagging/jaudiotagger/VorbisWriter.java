package nz.net.kallisti.emusicj.tagging.jaudiotagger;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFrame;
import nz.net.kallisti.emusicj.tagging.ITagWriter;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import com.google.inject.Inject;

/**
 * <p>
 * This writes the tag data to a Vorbis-type file (.ogg, .flac, etc)
 * </p>
 * 
 * @author robin
 */
public class VorbisWriter implements ITagWriter {

	private final Logger logger;

	@Inject
	public VorbisWriter() {
		logger = LogUtils.getLogger(this);
	}

	public static boolean supportedFileInternal(String file) {
		String name = file.toLowerCase();
		return (name.endsWith("flac") || name.endsWith("ogg") || name
				.endsWith("oga"));
	}

	public boolean supportedFile(String filename) {
		return supportedFileInternal(filename);
	}

	public void writeTag(ITagData tagData, File file) throws RuntimeException,
			IOException {
		if (!(tagData instanceof VorbisData)) {
			throw new RuntimeException("tagData is of type "
					+ tagData.getClass() + ", when it should be VorbisData");
		}

		AudioFile audioFile;
		try {
			audioFile = AudioFileIO.read(file);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unable to read existing tags from "
					+ file, e);
			return;
		}
		Tag fileTag = audioFile.getTag();
		if (!(fileTag instanceof VorbisCommentTag || fileTag instanceof FlacTag)) {
			throw new RuntimeException(
					"File does not appear to be a Vorbis or FLAC file. Got "
							+ fileTag.getClass() + " instead.");
		}
		final VorbisData tag = (VorbisData) tagData;
		Set<String> types = tag.getFrameTypes();
		for (String type : types) {
			Set<ITagFrame> frames = tag.getFramesForType(type);
			for (ITagFrame frame : frames) {
				VorbisFrame vf = (VorbisFrame) frame;
				try {
					if (fileTag instanceof VorbisCommentTag)
						((VorbisCommentTag) fileTag).setField(vf.getKey(), vf
								.getValue());
					else if (fileTag instanceof FlacTag)
						((FlacTag) fileTag)
								.setField(vf.getKey(), vf.getValue());
				} catch (FieldDataInvalidException e) {
					logger.log(Level.WARNING, "Failed to write a tag field to "
							+ file + ". key=[" + vf.getKey() + "] val=["
							+ vf.getValue() + "]");
				}
			}
		}
		try {
			audioFile.commit();
		} catch (CannotWriteException e) {
			logger.log(Level.WARNING, "Unable to write tags to " + file, e);
			return;
		}
	}
}
