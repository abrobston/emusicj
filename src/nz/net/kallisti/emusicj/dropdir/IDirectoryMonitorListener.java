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
package nz.net.kallisti.emusicj.dropdir;

import java.io.File;

/**
 * <p>Interface for classes wanting to be notified when a new file is discovered 
 * in the drop directory.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public interface IDirectoryMonitorListener {

	/**
	 * Notifies an object of a new file. 
	 * @param mon the monitor that detected the file
	 * @param file the file that it found
	 */
	public void newFile(IDirectoryMonitor mon, File file);
	
}
