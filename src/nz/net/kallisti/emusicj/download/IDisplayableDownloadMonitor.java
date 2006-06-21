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
package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 * An instance of this interface will provide information that allows it to be
 * displayed.
 * 
 * $Id$
 *
 * @author robin
 */
public interface IDisplayableDownloadMonitor {
	
	/**
	 * Returns a file corresponding to this download, e.g. cover art. 
	 */
	public File getImageFile();

	/**
	 * Returns text to be displayed corresponding to this download. This text 
	 * is in the form of pairs of strings to allow nice layout. The first of the
	 * pair is the name of the value, e.g. "Artist", and the second one is the 
	 * actual value, e.g. "Nirvana".
	 */
	public String[][] getText();
	
}
