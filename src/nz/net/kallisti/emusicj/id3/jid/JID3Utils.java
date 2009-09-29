package nz.net.kallisti.emusicj.id3.jid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloadListener;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ContentType;
import org.blinkenlights.jid3.v2.ID3V2Frame;
import org.blinkenlights.jid3.v2.TALBTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TCOMTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TCONTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TCOPTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TIT1TextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TIT2TextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TOFNTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TPE1TextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TPE2TextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TPE3TextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TPOSTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TRCKTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TSRCTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.TXXXTextInformationID3V2Frame;
import org.blinkenlights.jid3.v2.WCOMUrlLinkID3V2Frame;
import org.blinkenlights.jid3.v2.APICID3V2Frame.PictureType;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This contains utilities useful for working with JID3, and most of the 'type
 * to object' mapping stuff
 * </p>
 * 
 * @author robin
 */
public class JID3Utils {

	public static final String ID3_TITLE = "TIT2";
	public static final String ID3_ALBUM = "TALB";
	public static final String ID3_ARTIST = "TPE1";
	public static final String ID3_COMPOSER = "TCOM";
	public static final String ID3_GENRE = "TCON";
	public static final String ID3_FILENAME = "TOFN";
	public static final String ID3_CONDUCTOR = "TPE3";
	public static final String ID3_ISRC = "TSRC";
	public static final String ID3_DISCNUM = "TPOS";
	public static final String ID3_TRACK = "TRCK";
	public static final String ID3_COPYRIGHT = "TCOP";
	public static final String ID3_WWW = "WCOM";
	public static final String ID3_ALBUM_ARTIST = "TPE2"; // these two are
	public static final String ID3_GROUPING = "TIT1"; // itunes things
	public static final String ID3_COVERART = "APIC";
	public static final String ID3_CUSTOM_TEXT = "TXXX";

	private final Provider<ISimpleDownloader> dlProv;
	private final Logger logger;
	private static Map<String, APICID3V2Frame> cachedCovers = Collections
			.synchronizedMap(new HashMap<String, APICID3V2Frame>());

	@Inject
	public JID3Utils(Provider<ISimpleDownloader> dlProv) {
		this.dlProv = dlProv;
		logger = LogUtils.getLogger(this);
	}

	/**
	 * <p>
	 * Given an ID3v2 frame type, and a list of values, this provides the frame
	 * object. This makes it easier to serialise the JID3Data object. Refer to
	 * the spec and the code for details on the types and values.
	 * </p>
	 * <p>
	 * It doesn't handle all the types, just the ones needed for eMusic/J and
	 * derivatives.
	 * </p>
	 * 
	 * @param type
	 *            the frame type
	 * @param values
	 *            a list of values that the type will be storing, or
	 *            <code>null</code> if an error occurred during processing
	 *            (e.g., a numerical value couldn't be converted.)
	 */
	public ID3V2Frame listToFrame(String type, List<String> values)
			throws ID3Exception {
		if (type.equals(ID3_TITLE)) {
			return new TIT2TextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_ALBUM)) {
			return new TALBTextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_ARTIST)) {
			return new TPE1TextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_COMPOSER)) {
			return new TCOMTextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_GENRE)) {
			ContentType ct = new ContentType();
			ct.setRefinement(values.get(0));
			return new TCONTextInformationID3V2Frame(ct);
		} else if (type.equals(ID3_FILENAME)) {
			return new TOFNTextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_CONDUCTOR)) {
			return new TPE3TextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_ISRC)) {
			return new TSRCTextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_DISCNUM)) {
			String tn = values.get(0);
			String[] parts = tn.split("/");
			try {
				if (parts.length == 0)
					return null;
				else if (parts.length == 1)
					return new TPOSTextInformationID3V2Frame(Integer
							.parseInt(parts[0]));
				else
					return new TPOSTextInformationID3V2Frame(Integer
							.parseInt(parts[0]), Integer.parseInt(parts[1]));
			} catch (NumberFormatException e) {
				return null;
			}
		} else if (type.equals(ID3_TRACK)) {
			String tn = values.get(0);
			String[] parts = tn.split("/");
			try {
				if (parts.length == 0)
					return null;
				else if (parts.length == 1)
					return new TRCKTextInformationID3V2Frame(Integer
							.parseInt(parts[0]));
				else
					return new TRCKTextInformationID3V2Frame(Integer
							.parseInt(parts[0]), Integer.parseInt(parts[1]));
			} catch (NumberFormatException e) {
				return null;
			}
		} else if (type.equals(ID3_COPYRIGHT)) {
			try {
				return new TCOPTextInformationID3V2Frame(Integer
						.parseInt(values.get(0)), values.get(1));
			} catch (NumberFormatException e) {
				return null;
			}
		} else if (type.equals(ID3_ALBUM_ARTIST)) {
			return new TPE2TextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_WWW)) {
			return new WCOMUrlLinkID3V2Frame(values.get(0));
		} else if (type.equals(ID3_GROUPING)) {
			return new TIT1TextInformationID3V2Frame(values.get(0));
		} else if (type.equals(ID3_CUSTOM_TEXT)) {
			return new TXXXTextInformationID3V2Frame(values.get(0), values
					.get(1));
		} else if (type.equals(ID3_COVERART)) {
			return getCoverArt(values.get(0));
		}
		throw new ID3Exception("An unknown type was specified: '" + type + "'");
	}

	/**
	 * This gets the cover art. Note that this is a blocking operation.
	 * 
	 * @param url
	 *            the URL to download from
	 * @return a frame that contains the image, or <code>null</code> if
	 *         something goes wrong
	 */
	private ID3V2Frame getCoverArt(final String url) {
		APICID3V2Frame cached = cachedCovers.get(url);
		if (cached != null)
			return cached;
		ISimpleDownloader dl = dlProv.get();
		try {
			dl.setURL(new URL(url));
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Invalid URL for cover: " + url, e);
			return null;
		}
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		dl.setOutputStream(bytes);
		final ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(
				1);
		ISimpleDownloadListener listener = new ISimpleDownloadListener() {
			public void downloadFailed(ISimpleDownloader downloader) {
				logger.log(Level.WARNING, "Downloading cover from " + url
						+ " failed");
				// this is a hack because we can't offer null
				try {
					queue.put(new Object());
				} catch (InterruptedException e) {
					// do nothing
				}
			}

			public void downloadSucceeded(ISimpleDownloader downloader,
					File file) {
				APICID3V2Frame result = null;
				try {
					result = new APICID3V2Frame("image/jpeg",
							PictureType.FrontCover, null, bytes.toByteArray());
				} catch (ID3Exception e) {
					logger.log(Level.WARNING,
							"Exception creating cover image frame", e);
				}
				try {
					queue.put(result);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		};
		dl.addListener(listener);
		dl.start();
		Object result = null;
		try {
			result = queue.poll(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// do nothing
		}
		if (!(result instanceof APICID3V2Frame))
			return null;
		cachedCovers.put(url, (APICID3V2Frame) result);
		return (ID3V2Frame) result;
	}

}
