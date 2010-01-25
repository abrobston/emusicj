package nz.net.kallisti.emusicj.tagging.jid;

import nz.net.kallisti.emusicj.tagging.ITagFrame;

import org.blinkenlights.jid3.v2.ID3V2Frame;

/**
 * <p>
 * This class provides a JID3-specific implementation for IID3Frame.
 * </p>
 * 
 * @author robin
 */
class JID3Frame implements ITagFrame {

	private final ID3V2Frame frame;

	public JID3Frame(ID3V2Frame frame) {
		this.frame = frame;
	}

	/**
	 * @return Returns the frame.
	 */
	public ID3V2Frame getFrame() {
		return frame;
	}

}
