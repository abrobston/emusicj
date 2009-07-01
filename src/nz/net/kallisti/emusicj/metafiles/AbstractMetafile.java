package nz.net.kallisti.emusicj.metafiles;

import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.urls.IDynamicURL;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;

/**
 * <p>
 * This provides some basic functions, and centralises some more complex ones,
 * that are used when processing metafiles
 * </p>
 * 
 * @author robin
 */
public abstract class AbstractMetafile implements IMetafile {

	private final IImageFactory images;
	private final Logger logger;
	private final IURLFactory urls;

	public AbstractMetafile(IImageFactory images, IURLFactory urls) {
		// This is needed so we can update the logo if the .col file tells us
		// it's changed. This is annoyingly tightly coupled to the SWT view
		// implementation, and so the design may need to be revisited in the
		// future.
		this.images = images;
		this.urls = urls;
		logger = LogUtils.getLogger(this);
	}

	/**
	 * This sets the logo to that of the provided URL.
	 * 
	 * @param url
	 *            the URL containing the new logo
	 */
	protected void setLogo(URL url) {
		IDynamicImageProvider logoProvider = images
				.getApplicationLogoProvider();
		if (logoProvider instanceof IURLDynamicImageProvider) {
			((IURLDynamicImageProvider) logoProvider).changeURL(url);
		} else {
			logger
					.warning("Logo change requested on non-URL-based dynamic image (logoProvider.getClass()="
							+ logoProvider.getClass());
		}

	}

	/**
	 * This processes the date that is contained in the string, and turns it
	 * into a real date. It expects RFC 3339 dates.
	 * 
	 * @param dateStr
	 *            the string form of the date
	 * @return the parsed date, or <code>null</code> if it can't be parsed.
	 */
	protected Date parseDate(String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		ParsePosition pos = new ParsePosition(0);
		if (dateStr == null || "".equals(dateStr))
			return null;
		// because SimpleDateFormat is stupid we need to remove the ':' from the
		// timezone
		dateStr = dateStr.replaceFirst("([+-]\\d\\d):(\\d\\d)$", "$1$2");
		Date date = df.parse(dateStr, pos);
		if (date == null) {
			logger.warning("Unable to parse date (" + dateStr
					+ "), error in pos " + pos.getErrorIndex());
			return null;
		}
		return date;
	}

	/**
	 * Sets the banner, along with its clickability.
	 * 
	 * @param imgUrl
	 *            the URL to source the image from. If <code>null</code> the
	 *            entire call is ignored.
	 * @param clickUrl
	 *            the URL that the user is taken to when they click on the
	 *            banner. If <code>null</code>, it won't be clickable.
	 */
	protected void setBanner(URL imgUrl, URL clickUrl) {
		if (imgUrl == null)
			return;
		IURLDynamicImageProvider bannerImage = images.getBannerProvider();
		bannerImage.changeURL(imgUrl);
		IDynamicURL bannerClick = urls.getBannerClickURL();
		bannerClick.setURL(clickUrl);
	}

}
