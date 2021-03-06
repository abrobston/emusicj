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
import nz.net.kallisti.emusicj.bindingtypes.ID3Tagger;
import nz.net.kallisti.emusicj.bindingtypes.Naxos;
import nz.net.kallisti.emusicj.bindingtypes.PlainText;
import nz.net.kallisti.emusicj.bindingtypes.VorbisTagger;
import nz.net.kallisti.emusicj.controller.EmusicjController;
import nz.net.kallisti.emusicj.controller.IEmusicjController;
import nz.net.kallisti.emusicj.download.CoverDownloader;
import nz.net.kallisti.emusicj.download.HTTPDownloader;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloadHooks;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.MusicDownloader;
import nz.net.kallisti.emusicj.download.hooks.StandardDownloadHooks;
import nz.net.kallisti.emusicj.download.hooks.tagging.ITaggingHook;
import nz.net.kallisti.emusicj.download.hooks.tagging.TaggingHook;
import nz.net.kallisti.emusicj.files.cleanup.CleanupFiles;
import nz.net.kallisti.emusicj.files.cleanup.ICleanupFiles;
import nz.net.kallisti.emusicj.mediaplayer.ConfigureMediaPlayer;
import nz.net.kallisti.emusicj.mediaplayer.IConfigureMediaPlayer;
import nz.net.kallisti.emusicj.mediaplayer.IMediaPlayerSync;
import nz.net.kallisti.emusicj.mediaplayer.NoopMediaPlayerSync;
import nz.net.kallisti.emusicj.mediaplayer.windows.WindowsPlayers;
import nz.net.kallisti.emusicj.metafiles.EMPMetafile;
import nz.net.kallisti.emusicj.metafiles.EMXMetaFile;
import nz.net.kallisti.emusicj.metafiles.IMetafile;
import nz.net.kallisti.emusicj.metafiles.IMetafileLoader;
import nz.net.kallisti.emusicj.metafiles.MetafileLoader;
import nz.net.kallisti.emusicj.metafiles.NaxosMetafile;
import nz.net.kallisti.emusicj.metafiles.PlainTextMetafile;
import nz.net.kallisti.emusicj.misc.PlatformUtils;
import nz.net.kallisti.emusicj.misc.files.FileNameCleaner;
import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.models.DownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.network.failure.INetworkFailure;
import nz.net.kallisti.emusicj.network.failure.NetworkFailure;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;
import nz.net.kallisti.emusicj.network.http.downloader.SimpleDownloader;
import nz.net.kallisti.emusicj.network.http.proxy.HttpClientProvider;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;
import nz.net.kallisti.emusicj.network.http.proxy.ProxyCredentialsProvider;
import nz.net.kallisti.emusicj.tagging.ITagFromXML;
import nz.net.kallisti.emusicj.tagging.ITagSerialiser;
import nz.net.kallisti.emusicj.tagging.ITagWriter;
import nz.net.kallisti.emusicj.tagging.general.GeneralFromXML;
import nz.net.kallisti.emusicj.tagging.general.GeneralTagSerialiser;
import nz.net.kallisti.emusicj.tagging.general.IGeneralTagFromXML;
import nz.net.kallisti.emusicj.tagging.jaudiotagger.VorbisFromXML;
import nz.net.kallisti.emusicj.tagging.jaudiotagger.VorbisSerialiser;
import nz.net.kallisti.emusicj.tagging.jaudiotagger.VorbisWriter;
import nz.net.kallisti.emusicj.tagging.jid.JID3FromXML;
import nz.net.kallisti.emusicj.tagging.jid.JID3Serialiser;
import nz.net.kallisti.emusicj.tagging.jid.JID3Writer;
import nz.net.kallisti.emusicj.updater.IUpdateFetcher;
import nz.net.kallisti.emusicj.updater.URLUpdateFetcher;
import nz.net.kallisti.emusicj.urls.DynamicURL;
import nz.net.kallisti.emusicj.urls.IDynamicURL;
import nz.net.kallisti.emusicj.view.IEmusicjView;
import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamAndURLDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IStreamDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.StreamAndURLDynamicImageProvider;
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
		bind(IEmusicjView.class).to(SWTView.class).in(Scopes.SINGLETON);
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
		bind(IUpdateFetcher.class).to(URLUpdateFetcher.class);
		bind(IFileNameCleaner.class).to(FileNameCleaner.class);
		bind(CredentialsProvider.class).to(ProxyCredentialsProvider.class).in(
				Scopes.SINGLETON);
		bind(IHttpClientProvider.class).to(HttpClientProvider.class).in(
				Scopes.SINGLETON);
		bind(IStreamDynamicImageProvider.class).to(
				StreamDynamicImageProvider.class);
		bind(IURLDynamicImageProvider.class).to(URLDynamicImageProvider.class);
		bind(IStreamAndURLDynamicImageProvider.class).to(
				StreamAndURLDynamicImageProvider.class);
		bind(ISimpleDownloader.class).to(SimpleDownloader.class);
		bind(ICleanupFiles.class).to(CleanupFiles.class).in(Scopes.SINGLETON);
		bind(IDynamicURL.class).to(DynamicURL.class);
		bind(INetworkFailure.class).to(NetworkFailure.class).in(
				Scopes.SINGLETON);
		bind(ITaggingHook.class).to(TaggingHook.class);
		bind(ITagSerialiser.class).to(GeneralTagSerialiser.class);
		bind(IGeneralTagFromXML.class).to(GeneralFromXML.class);
		bind(ITagFromXML.class).annotatedWith(ID3Tagger.class).to(
				JID3FromXML.class);
		bind(ITagWriter.class).annotatedWith(ID3Tagger.class).to(
				JID3Writer.class);
		bind(ITagSerialiser.class).annotatedWith(ID3Tagger.class).to(
				JID3Serialiser.class);
		bind(ITagFromXML.class).annotatedWith(VorbisTagger.class).to(
				VorbisFromXML.class);
		bind(ITagWriter.class).annotatedWith(VorbisTagger.class).to(
				VorbisWriter.class);
		bind(ITagSerialiser.class).annotatedWith(VorbisTagger.class).to(
				VorbisSerialiser.class);
		bind(IMediaPlayerSync.class).to(getMediaPlayerForPlatform()).in(
				Scopes.SINGLETON);
		bind(IConfigureMediaPlayer.class).to(ConfigureMediaPlayer.class)
				.asEagerSingleton();
		bind(IDownloadHooks.class).to(StandardDownloadHooks.class);
	}

	/**
	 * This provides the media player class that applies to the current platform
	 * 
	 * @return the media player class for this platform
	 */
	protected Class<? extends IMediaPlayerSync> getMediaPlayerForPlatform() {
		if (PlatformUtils.isWindows()) {
			return WindowsPlayers.class;
		}
		return NoopMediaPlayerSync.class;
	}
}
