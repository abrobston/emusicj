package nz.net.kallisti.emusicj.tagging.jaudiotagger;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFrame;
import nz.net.kallisti.emusicj.tagging.ITagWriter;
import nz.net.kallisti.emusicj.tagging.TagUtils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.reference.PictureTypes;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This writes the tag data to a Vorbis-type file (.ogg, .flac, etc)
 * </p>
 * 
 * @author robin
 */
public class VorbisWriter implements ITagWriter {

	private final Logger logger;
	private final Provider<ISimpleDownloader> dlProv;
	private final IStrings strings;

	@Inject
	public VorbisWriter(Provider<ISimpleDownloader> dlProv, IStrings strings) {
		this.dlProv = dlProv;
		this.strings = strings;
		logger = LogUtils.getLogger(this);
	}

	public static boolean supportedFileInternal(String file) {
		String name = file.toLowerCase();
		return file != null
				&& (name.endsWith("flac") || name.endsWith("ogg") || name
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
		if (fileTag instanceof VorbisCommentTag)
			((VorbisCommentTag) fileTag).setVendor(strings.getAppName() + " "
					+ strings.getVersion());
		else if (fileTag instanceof FlacTag)
			((FlacTag) fileTag).getVorbisCommentTag().setVendor(
					strings.getAppName() + " " + strings.getVersion());
		final VorbisData tag = (VorbisData) tagData;
		Set<String> types = tag.getFrameTypes();
		for (String type : types) {
			Set<ITagFrame> frames = tag.getFramesForType(type);
			for (ITagFrame frame : frames) {
				VorbisFrame vf = (VorbisFrame) frame;
				try {
					if ("COVERART".equals(vf.getKey())) {
						// Special handling of coverart - we need to download
						// the file, and use a special call to set it.
						TagUtils tagUtils = new TagUtils();
						byte[] bytes = tagUtils.downloadCoverArt(vf.getValue(),
								dlProv);
						if (fileTag instanceof VorbisCommentTag) {
							((VorbisCommentTag) fileTag).setArtworkField(bytes,
									"image/jpeg");
						} else if (fileTag instanceof FlacTag) {
							try {
								FlacTag flacTag = (FlacTag) fileTag;
								Dimension d = TagUtils.getJPEGDimension(bytes);
								flacTag.setField(flacTag
										.createArtworkField(bytes,
												PictureTypes.DEFAULT_ID,
												ImageFormats.MIME_TYPE_JPEG,
												"Cover Image", d.width,
												d.height, 24, 0));
							} catch (RuntimeException e) {
								logger
										.log(
												Level.WARNING,
												"An error occurred processing the cover image file",
												e);
							}
						}
					} else {
						if (fileTag instanceof VorbisCommentTag)
							((VorbisCommentTag) fileTag).addField(vf.getKey(),
									vf.getValue());
						else if (fileTag instanceof FlacTag)
							((FlacTag) fileTag).addField(vf.getKey(), vf
									.getValue());
					}
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
