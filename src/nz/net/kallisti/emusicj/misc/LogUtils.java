package nz.net.kallisti.emusicj.misc;

import java.util.logging.Logger;

/**
 * <p>
 * Provides a simple, static interface to the logging API.
 * </p>
 * 
 * @author robin
 */
public class LogUtils {

	/**
	 * Gets an instance of the logger. Loggers get grouped according to their
	 * package.
	 * 
	 * @param obj
	 *            the object that wants the logger (usually 'this')
	 * @return a logger you can use to log
	 */
	public static Logger getLogger(final Object obj) {
		return Logger.getLogger(obj.getClass().getPackage().getName());
	}

}
