package nz.net.kallisti.emusicj.bindings;

import java.io.FilenameFilter;

import nz.net.kallisti.emusicj.bindingtypes.WatchFiles;
import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.controller.ReggaeCountryPreferences;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitor;
import nz.net.kallisti.emusicj.misc.ReggaeCountryFilenameFilter;
import nz.net.kallisti.emusicj.providers.WatchFilesDirectoryMonitorProvider;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.strings.ReggaeCountryStrings;
import nz.net.kallisti.emusicj.updater.IUpdateChecker;
import nz.net.kallisti.emusicj.updater.StandardUpdateChecker;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.urls.reggaecountry.ReggaeCountryURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.images.reggaecountry.ReggaeCountryImageFactory;
import nz.net.kallisti.emusicj.view.menu.IMenuBuilder;
import nz.net.kallisti.emusicj.view.menu.StandardMenuBarBuilder;
import nz.net.kallisti.emusicj.view.style.DefaultStyle;
import nz.net.kallisti.emusicj.view.style.IAppStyle;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * <p>
 * Bindings that are specific to the Naxos variant of the program
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ReggaeCountryBindings extends AbstractModule {

	@Override
	public void configure() {
		bind(FilenameFilter.class).annotatedWith(WatchFiles.class).to(
				ReggaeCountryFilenameFilter.class);
		bind(IDirectoryMonitor.class).annotatedWith(WatchFiles.class)
				.toProvider(WatchFilesDirectoryMonitorProvider.class);
		bind(IStrings.class).to(ReggaeCountryStrings.class)
				.in(Scopes.SINGLETON);
		bind(IImageFactory.class).to(ReggaeCountryImageFactory.class).in(
				Scopes.SINGLETON);
		bind(IURLFactory.class).to(ReggaeCountryURLFactory.class).in(
				Scopes.SINGLETON);
		bind(IPreferences.class).to(ReggaeCountryPreferences.class)
				.asEagerSingleton();
		bind(IMenuBuilder.class).to(StandardMenuBarBuilder.class);
		bind(IUpdateChecker.class).to(StandardUpdateChecker.class);
		bind(IAppStyle.class).to(DefaultStyle.class);
	}

}
