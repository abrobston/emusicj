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
import nz.net.kallisti.emusicj.controller.EmusicjController;
import nz.net.kallisti.emusicj.controller.IEmusicjController;
import nz.net.kallisti.emusicj.download.CoverDownloader;
import nz.net.kallisti.emusicj.download.HTTPDownloader;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.files.cleanup.CleanupFiles;
import nz.net.kallisti.emusicj.files.cleanup.ICleanupFiles;
import nz.net.kallisti.emusicj.metafiles.EMPMetafile;
import nz.net.kallisti.emusicj.metafiles.EMXMetaFile;
import nz.net.kallisti.emusicj.metafiles.IMetafile;
import nz.net.kallisti.emusicj.metafiles.IMetafileLoader;
import nz.net.kallisti.emusicj.metafiles.MetafileLoader;
import nz.net.kallisti.emusicj.metafiles.NaxosMetafile;
import nz.net.kallisti.emusicj.metafiles.PlainTextMetafile;
import nz.net.kallisti.emusicj.misc.files.FileNameCleaner;
import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.models.DownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;
import nz.net.kallisti.emusicj.network.http.downloader.SimpleDownloader;
import nz.net.kallisti.emusicj.network.http.proxy.HttpClientProvider;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;
import nz.net.kallisti.emusicj.network.http.proxy.ProxyCredentialsProvider;
import nz.net.kallisti.emusicj.updater.IUpdateCheck;
import nz.net.kallisti.emusicj.updater.UpdateCheck;
import nz.net.kallisti.emusicj.view.IEMusicView;
import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.StreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.URLDynamicImageProvider;

import org.apache.commons.httpclient.auth.CredentialsProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * <p>
 * This contains the dependency injection bindings for Guice. It maps interface
 * to implementations. Branding-specific bindings shouldn't be included in here,
 * they should be added to a separate module which is conditionally loaded.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class Bindings extends AbstractModule {

	@Override
	public void configure() {
		bind(IEMusicView.class).to(SWTView.class).in(Scopes.SINGLETON);
		bind(IEmusicjController.class).to(EmusicjController.class).in(
				Scopes.SINGLETON);
		// bind(MacSupport.class).asEagerSingleton();

		bind(IDownloadsModel.class).to(DownloadsModel.class);
		bind(IDownloader.class).to(HTTPDownloader.class);
		bind(IMusicDownloader.class).to(MusicDownloader.class);
		bind(ICoverDownloader.class).to(CoverDownloader.class);
		bind(IMetafileLoader.class).to(MetafileLoader.class);
		bind(IMetafile.class).annotatedWith(Emusic.class).to(EMPMetafile.class);
		bind(IMetafile.class).annotatedWith(Naxos.class)
				.to(NaxosMetafile.class);
		bind(IMetafile.class).annotatedWith(PlainText.class).to(
				PlainTextMetafile.class);
		bind(IMetafile.class).annotatedWith(EmusicEmx.class).to(
				EMXMetaFile.class);
		bind(IUpdateCheck.class).to(UpdateCheck.class);
		bind(IFileNameCleaner.class).to(FileNameCleaner.class);
		bind(CredentialsProvider.class).to(ProxyCredentialsProvider.class).in(
				Scopes.SINGLETON);
		bind(IHttpClientProvider.class).to(HttpClientProvider.class).in(
				Scopes.SINGLETON);
		bind(IStreamDynamicImageProvider.class).to(
				StreamDynamicImageProvider.class);
		bind(IURLDynamicImageProvider.class).to(URLDynamicImageProvider.class);
		bind(ISimpleDownloader.class).to(SimpleDownloader.class);
		bind(ICleanupFiles.class).to(CleanupFiles.class).in(Scopes.SINGLETON);
	}
}
