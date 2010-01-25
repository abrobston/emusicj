package nz.net.kallisti.emusicj.tagging.vorbiscomments;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.tagging.ITagData;
import nz.net.kallisti.emusicj.tagging.ITagFrame;
import nz.net.kallisti.emusicj.tagging.ITagWriter;
import adamb.vorbis.CommentUpdater;
import adamb.vorbis.VorbisCommentHeader;
import adamb.vorbis.VorbisIO;

import com.google.inject.Inject;

/**
 * <p>
 * This writes the tag data to a Vorbis-type file (.ogg, .flac, etc)
 * </p>
 * 
 * @author robin
 */
public class VorbisWriter implements ITagWriter {

	private final IStrings strings;

	@Inject
	public VorbisWriter(IStrings strings) {
		this.strings = strings;
	}

	public boolean supportedFile(File file) {
		String name = file.getName().toLowerCase();
		return (name.endsWith(".flac") || name.endsWith(".ogg") || name
				.endsWith(".oga"));
	}

	public void writeTag(ITagData tagData, File file) throws RuntimeException,
			IOException {
		if (!(tagData instanceof VorbisData)) {
			throw new RuntimeException("tagData is of type "
					+ tagData.getClass() + ", when it should be VorbisData");
		}
		final VorbisData tag = (VorbisData) tagData;
		VorbisIO.writeComments(file, new CommentUpdater() {

			public boolean updateComments(VorbisCommentHeader comments) {
				comments.vendor = strings.getAppName() + " "
						+ strings.getVersion();
				Set<String> types = tag.getFrameTypes();
				for (String type : types) {
					Set<ITagFrame> frames = tag.getFramesForType(type);
					for (ITagFrame frame : frames) {
						VorbisFrame vf = (VorbisFrame) frame;
						comments.fields.add(vf.getFrame());
					}
				}
				return true;
			}

		});
	}

}
