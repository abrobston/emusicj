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
package nz.net.kallisti.emusicj.download.mime;

import java.awt.datatransfer.MimeTypeParseException;

/**
 * This creates a few standard MIME types for easy access and pooling.
 * 
 * $Id$
 * 
 * @author robin
 */
public abstract class MimeTypes {

	public static MimeType IMAGES;
	public static MimeType AUDIO;
	public static MimeType APP_OCTET;
	public static MimeType PDF;
	public static MimeType OGG;
	public static MimeType CUE;
	public static MimeType FLAC;
	public static MimeType FLAC2;

	static {
		try {
			IMAGES = new MimeType("image/*");
			AUDIO = new MimeType("audio/*");
			APP_OCTET = new MimeType("application/octet-stream");
			PDF = new MimeType("application/pdf");
			OGG = new MimeType("application/ogg");
			CUE = new MimeType("application/cue");
			FLAC = new MimeType("application/flac");
			FLAC2 = new MimeType("application/x-flac");
		} catch (MimeTypeParseException e) {
			e.printStackTrace();
		}
	}

}
