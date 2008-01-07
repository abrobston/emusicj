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
package nz.net.kallisti.emusicj.misc;

import java.io.File;
import java.io.FilenameFilter;

/**
 * <p>
 * Used to recognise emusic metafiles by name. Looks for '.rcm' and '.RCM'
 * extensions.
 * </p>
 * 
 * <p>
 * $Id: EMPFilenameFilter.java 129 2006-06-21 13:06:54Z robin $
 * </p>
 * 
 * @author robin
 */
public class ReggaeCountryFilenameFilter implements FilenameFilter {

	/**
	 * Default constructor
	 */
	public ReggaeCountryFilenameFilter() {
		super();
	}

	/**
	 * Returns true if the name matches the scheme of a Reggae Country metafile.
	 */
	public boolean accept(File dir, String name) {
		return name.endsWith(".rcm") || name.endsWith(".RCM");
	}

}
