package nz.net.kallisti.emusicj;

/**
 * <p>Defines global constants for the program.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class Constants {

    public final static String VERSION = "0.17-svn";
    
    public final static String APPNAME = "eMusic/J";

	public static final String STATE_DIR = ".emusicj";

	public static final String APPURL = "http://www.kallisti.net.nz/EMusicJ";

	public static final String ABOUT_BOX_TEXT = 
		"This program was written by Robin Sheat <robin@kallisti.net.nz> "+
		"[eMusic.com username: Eythian]\n\n" +
		"Thanks to:\n" +
		"Curtis Cooley (code)\n"+
		"Liron Tocker <http://lironbot.com> [eMusic: Liron] (artwork)\n"+
		"James Elwood [eMusic: jelwood01] (artwork)\n"+
		"\nCheck "+APPURL+" for updates and "+
		"information.\n"+
		"\nThe program may be freely distributed under the terms of the GNU GPL.\n"+
		"\nNote that this program is not affiliated in any way with eMusic.com\n";

	public static final String UPDATE_URL = "http://www.kallisti.net.nz/~robin/emusicj-version.txt";

	/**
	 * Maximum amount of failures allowed before we give up automatically
	 * starting the download
	 */
	public static final int MAX_FAILURES = 5;

}
