/*
 * Created on Jul 21, 2006 3:19:03 PM
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 * 
 * Modified (minorly) to work with eMusic/J by Robin Sheat <robin@kallisti.net.nz>
 */
package org.gudy.azureus2.platform.macosx.access.jnilib;

import java.io.File;

/**
 * @author TuxPaper
 * @created Jul 21, 2006
 * 
 *          javah -d . -classpath ../../../../../../../../bin
 *          org.gudy.azureus2.platform.macosx.access.jnilib.OSXAccess
 */
public class OSXAccess {
	private static boolean bLoaded = false;

	public static final boolean isOSX_10_5_OrHigher;
	public static final boolean isOSX_10_6_OrHigher;

	static {
		int first_digit = 0;
		int second_digit = 0;

		try {
			String os_version = System.getProperty("os.version");

			String[] bits = os_version.split("\\.");

			first_digit = Integer.parseInt(bits[0]);

			if (bits.length > 1) {

				second_digit = Integer.parseInt(bits[1]);
			}
		} catch (Throwable e) {

		}

		isOSX_10_5_OrHigher = first_digit > 10
				|| (first_digit == 10 && second_digit >= 5);
		isOSX_10_6_OrHigher = first_digit > 10
				|| (first_digit == 10 && second_digit >= 6);

		if (!isOSX_10_5_OrHigher || !loadLibrary("OSXAccess_10.5")) {
			loadLibrary("OSXAccess");
		}
	}

	private static boolean loadLibrary(String lib) {
		try {
			SystemLoadLibrary(lib);
			System.out.println(lib + " v" + getVersion() + " Load complete!");
			bLoaded = true;
		} catch (Throwable e1) {
			System.err.println("Could not find lib" + lib + ".jnilib; "
					+ e1.toString());
		}

		return bLoaded;
	}

	private static void SystemLoadLibrary(String lib) throws Throwable {
		try {
			System.loadLibrary(lib);
		} catch (Throwable t) {
			// if launched from eclipse, updates will put it into
			// ./Azureus.app/Contents/Resources/Java/dll
			// TODO make this more general so it doesn't rely on COL
			try {
				File f = new File(
						"ClassicsOnline.app/Contents/Resources/Java/dll/lib"
								+ lib + ".jnilib");
				System.load(f.getAbsolutePath());
			} catch (Throwable t2) {
				throw t;
			}
		}
	}

	public static final native int AEGetParamDesc(int theAppleEvent,
			int theAEKeyword, int desiredType, Object result); // AEDesc result

	public static final native String getVersion();

	// 1.02
	public static final native String getDocDir();

	// 1.03
	public static final native void memmove(byte[] dest, int src, int size);

	public static boolean isLoaded() {
		return bLoaded;
	}
}
