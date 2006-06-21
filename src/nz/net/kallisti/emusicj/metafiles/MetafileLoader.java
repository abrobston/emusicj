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
package nz.net.kallisti.emusicj.metafiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;

/**
 * <p>This is a class with a single static method that loads a metafile, and
 * invokes and appropriate parser for it.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class MetafileLoader {

    /**
     * Calls the handlers for the metafile formats that it knows about, asking
     * them if they understand the format. If one returns true, then it creates
     * a downloaders for the files in the metafile, and notifies the controller
     * of them.
     * @param controller the controller to notify of the download
     * @param filename the metafile to get the download information from
     * @return 
     * @throws FileNotFoundException if the file doesn't exist or is unreadable
     */
    public static List<IDownloader> load(IEMusicController controller, File filename)
        throws FileNotFoundException, IOException, UnknownFileException {
        if (!filename.exists() || !filename.canRead())
            throw new FileNotFoundException(filename+" not found or not readable.");
        IMetafile meta = null;
        if (EMPMetafile.canParse(filename)) {
        		meta = new EMPMetafile(filename);
        } else if (PlainTextMetafile.canParse(filename)) { 
            meta = new PlainTextMetafile(filename); 
        }
        if (meta == null)
            throw new UnknownFileException("Failed to find a handler for "+
                    filename);
        return meta.getDownloaders();
    }

}
