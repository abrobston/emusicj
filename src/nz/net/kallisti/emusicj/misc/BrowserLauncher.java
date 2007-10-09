package nz.net.kallisti.emusicj.misc;

import java.net.URL;

/**
 * <p>Opens the default browser on the system that it's running on. Taken from:
 * {@link http://www.centerkey.com/java/browser/}.</p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class BrowserLauncher {

	@SuppressWarnings("unchecked")
	public static void openURL(URL url) throws BrowserException {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				// This way doesn't seem to work so good, it does something
				// strange
/*				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url.toString() });
*/
				Runtime.getRuntime().exec("open "+url.toString());
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url.toString());
			} else { // assume Unix or Linux
				String[] browsers = { "gnome-www-browser", "sensible-browser",
						"firefox", "opera", "konqueror", "epiphany", "mozilla",
						"netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(
							new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null)
					throw new Exception("Could not find web browser");
				Runtime.getRuntime().exec(new String[] { browser, url.toString() });
			}
		} catch (Exception e) {
			throw new BrowserException();
		}
	}

}