package nz.net.kallisti.emusicj.tagging;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloadListener;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This contains functions that are generally useful for tag operations.
 * </p>
 * 
 * @author robin
 */
public class TagUtils {

	private final Logger logger;
	private final Map<String, byte[]> cachedCovers;

	@Inject
	public TagUtils() {
		logger = LogUtils.getLogger(this);
		cachedCovers = Collections
				.synchronizedMap(new HashMap<String, byte[]>());
	}

	/**
	 * This downloads cover art. It handles caching for the duration of the
	 * program running to avoid repeated downloads of the same thing.
	 * 
	 * @param url
	 *            the URL of the cover
	 * @param dlProv
	 *            the provider for the downloader to use
	 * @return an array of bytes containing the image data
	 */
	public byte[] downloadCoverArt(final String url,
			Provider<ISimpleDownloader> dlProv) {
		byte[] cached = cachedCovers.get(url);
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
		final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(
				1);
		ISimpleDownloadListener listener = new ISimpleDownloadListener() {
			public void downloadFailed(ISimpleDownloader downloader) {
				logger.log(Level.WARNING, "Downloading cover from " + url
						+ " failed");
				// this is a hack because we can't offer null
				try {
					queue.put(new byte[] {});
				} catch (InterruptedException e) {
					// do nothing
				}
			}

			public void downloadSucceeded(ISimpleDownloader downloader,
					File file) {
				byte[] result = null;
				result = bytes.toByteArray();
				try {
					queue.put(result);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		};
		dl.addListener(listener);
		dl.start();
		byte[] result = null;
		try {
			result = queue.poll(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// do nothing
		}
		if (result == null || result.length == 0)
			return null;
		cachedCovers.put(url, result);
		return result;
	}

	/**
	 * This works out the dimensions of a JPEG image. Taken from {@link http
	 * ://stackoverflow
	 * .com/questions/672916/how-to-get-image-height-and-width-using-java} and
	 * adapted. To get more advanced than this (e.g. different image types) it
	 * may be worth investigating ImageIO.
	 * 
	 * @param b
	 *            the bytes that comprise the image
	 * @return a dimension object that describes the JPEG
	 * @throws RuntimeException
	 *             if the image doesn't appear to be a JPEG image
	 */
	public static Dimension getJPEGDimension(byte[] b) {
		ByteArrayInputStream bis = new ByteArrayInputStream(b);

		// check for SOI marker
		if (bis.read() != 255 || bis.read() != 216)
			throw new RuntimeException(
					"SOI (Start Of Image) marker 0xff 0xd8 missing");

		Dimension d = null;

		while (bis.read() == 255) {
			int marker = bis.read();
			int len = bis.read() << 8 | bis.read();

			if (marker == 192) {
				bis.skip(1);

				int height = bis.read() << 8 | bis.read();
				int width = bis.read() << 8 | bis.read();

				d = new Dimension(width, height);
				break;
			}

			bis.skip(len - 2);
		}

		return d;
	}

}
