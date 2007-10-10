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

import nz.net.kallisti.emusicj.bindingtypes.Emusic;
import nz.net.kallisti.emusicj.bindingtypes.EmusicEmx;
import nz.net.kallisti.emusicj.bindingtypes.Naxos;
import nz.net.kallisti.emusicj.bindingtypes.PlainText;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>This is a class with a single static method that loads a metafile, and
 * invokes and appropriate parser for it.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class MetafileLoader implements IMetafileLoader {
	
	private final Provider<IMetafile> emusicProvider;
	private final Provider<IMetafile> naxosProvider;
	private final Provider<IMetafile> plainTextProvider;
	private final Provider<IMetafile> emxEmusicProvider;

	@Inject
	public MetafileLoader(@Emusic Provider<IMetafile> emusicProvider,
			@Naxos Provider<IMetafile> naxosProvider,
			@PlainText Provider<IMetafile> plainTextProvider,
			@EmusicEmx Provider<IMetafile> emxEmusicProvider) {
		this.emusicProvider = emusicProvider;
		this.naxosProvider = naxosProvider;
		this.plainTextProvider = plainTextProvider;
		this.emxEmusicProvider = emxEmusicProvider;
	}

    /* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.metafiles.IMetafileLoader#load(nz.net.kallisti.emusicj.controller.IEMusicController, java.io.File)
	 */
    public List<IDownloader> load(IEMusicController controller, File filename)
        throws FileNotFoundException, IOException, UnknownFileException {
        if (!filename.exists() || !filename.canRead())
            throw new FileNotFoundException(filename+" not found or not readable.");
        IMetafile meta = null;
        // The static methods here should be replaces with something better
        if (EMPMetafile.canParse(filename)) {
        	meta = emusicProvider.get();
        } else if (NaxosMetafile.canParse(filename)) {
        	meta = naxosProvider.get();
        } else if (EMXMetaFile.canParse(filename)) {
        	meta = emxEmusicProvider.get();
        } else if (PlainTextMetafile.canParse(filename)) { 
            meta = plainTextProvider.get();
        }
        if (meta == null)
            throw new UnknownFileException("Failed to find a handler for "+
                    filename);
        meta.setMetafile(filename);
        return meta.getDownloaders();
    }

}
