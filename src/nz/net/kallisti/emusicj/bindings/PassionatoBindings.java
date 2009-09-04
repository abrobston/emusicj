package nz.net.kallisti.emusicj.bindings;

import java.io.FilenameFilter;

import nz.net.kallisti.emusicj.bindingtypes.WatchFiles;
import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.controller.PassionatoPreferences;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitor;
import nz.net.kallisti.emusicj.misc.PassionatoFilenameFilter;
import nz.net.kallisti.emusicj.providers.WatchFilesDirectoryMonitorProvider;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.strings.PassionatoStrings;
import nz.net.kallisti.emusicj.updater.IUpdateChecker;
import nz.net.kallisti.emusicj.updater.StandardUpdateChecker;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.urls.passionato.PassionatoURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.images.passionato.PassionatoImageFactory;
import nz.net.kallisti.emusicj.view.menu.IMenuBuilder;
import nz.net.kallisti.emusicj.view.menu.StandardMenuBarBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * <p>
 * Bindings that are specific to the emusic variant of the application
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class PassionatoBindings extends AbstractModule {

	@Override
	public void configure() {
		bind(FilenameFilter.class).annotatedWith(WatchFiles.class).to(
				PassionatoFilenameFilter.class);
		bind(IDirectoryMonitor.class).annotatedWith(WatchFiles.class)
				.toProvider(WatchFilesDirectoryMonitorProvider.class);
		bind(IStrings.class).to(PassionatoStrings.class).in(Scopes.SINGLETON);
		bind(IImageFactory.class).to(PassionatoImageFactory.class).in(
				Scopes.SINGLETON);
		bind(IURLFactory.class).to(PassionatoURLFactory.class).in(
				Scopes.SINGLETON);
		bind(IPreferences.class).to(PassionatoPreferences.class)
				.asEagerSingleton();
		bind(IMenuBuilder.class).to(StandardMenuBarBuilder.class);
		bind(IUpdateChecker.class).to(StandardUpdateChecker.class);
	}

}
