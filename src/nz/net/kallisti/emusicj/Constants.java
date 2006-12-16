/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */
package nz.net.kallisti.emusicj;

/**
 * <p>Defines global constants for the program.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class Constants {

    public final static String VERSION = "0.20-svn";
    
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

	public static final String USER_MANUAL_URL = "http://www.kallisti.net.nz/EMusicJ/UserManual";

}
