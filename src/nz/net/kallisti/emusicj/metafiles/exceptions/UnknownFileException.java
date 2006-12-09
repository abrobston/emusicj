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
package nz.net.kallisti.emusicj.metafiles.exceptions;


/**
 * <p>This exception indicates that the provided file was not a known metafile,
 * or that some kind of parse error occurred while reading it.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class UnknownFileException extends RuntimeException {

	public UnknownFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownFileException(String message) {
        super(message);
    }

	public UnknownFileException(Throwable e) {
		super(e);
	}

}
