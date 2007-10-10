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
package nz.net.kallisti.emusicj.bindings;

import nz.net.kallisti.emusicj.bindingtypes.Emusic;
import nz.net.kallisti.emusicj.bindingtypes.EmusicEmx;
import nz.net.kallisti.emusicj.bindingtypes.Naxos;
import nz.net.kallisti.emusicj.bindingtypes.PlainText;
import nz.net.kallisti.emusicj.controller.EMusicController;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.CoverDownloader;
import nz.net.kallisti.emusicj.download.HTTPDownloader;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.metafiles.EMPMetafile;
import nz.net.kallisti.emusicj.metafiles.EMXMetaFile;
import nz.net.kallisti.emusicj.metafiles.IMetafile;
import nz.net.kallisti.emusicj.metafiles.IMetafileLoader;
import nz.net.kallisti.emusicj.metafiles.MetafileLoader;
import nz.net.kallisti.emusicj.metafiles.NaxosMetafile;
import nz.net.kallisti.emusicj.metafiles.PlainTextMetafile;
import nz.net.kallisti.emusicj.models.DownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.updater.IUpdateCheck;
import nz.net.kallisti.emusicj.updater.UpdateCheck;
import nz.net.kallisti.emusicj.view.IEMusicView;
import nz.net.kallisti.emusicj.view.SWTView;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * <p>This contains the dependency injection bindings for Guice. It maps
 * interface to implementations. Branding-specific bindings shouldn't be
 * included in here, they should be added to a seperate module which is 
 * conditionally loaded.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class Bindings extends AbstractModule {

	public void configure() {
		bind(IEMusicView.class).to(SWTView.class).in(Scopes.SINGLETON);
		bind(IEMusicController.class).to(EMusicController.class).in(Scopes.SINGLETON);
		//bind(MacSupport.class).asEagerSingleton();
		
		bind(IDownloadsModel.class).to(DownloadsModel.class);
		bind(IDownloader.class).to(HTTPDownloader.class);
		bind(IMusicDownloader.class).to(MusicDownloader.class);
		bind(ICoverDownloader.class).to(CoverDownloader.class);
		bind(IMetafileLoader.class).to(MetafileLoader.class);
		bind(IMetafile.class).annotatedWith(Emusic.class).to(EMPMetafile.class);
		bind(IMetafile.class).annotatedWith(Naxos.class).to(NaxosMetafile.class);
		bind(IMetafile.class).annotatedWith(PlainText.class).to(PlainTextMetafile.class);
		bind(IMetafile.class).annotatedWith(EmusicEmx.class).to(EMXMetaFile.class);
		bind(IUpdateCheck.class).to(UpdateCheck.class);
	}

}
