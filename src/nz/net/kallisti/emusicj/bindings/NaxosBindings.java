package nz.net.kallisti.emusicj.bindings;

import java.io.FilenameFilter;

import nz.net.kallisti.emusicj.bindingtypes.WatchFiles;
import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.controller.NaxosPreferences;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitor;
import nz.net.kallisti.emusicj.misc.NaxosFilenameFilter;
import nz.net.kallisti.emusicj.providers.WatchFilesDirectoryMonitorProvider;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.strings.NaxosStrings;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.urls.naxos.NaxosURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.images.naxos.NaxosImageFactory;

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
public class NaxosBindings extends AbstractModule {

	@Override
	public void configure() {
		bind(FilenameFilter.class).annotatedWith(WatchFiles.class).to(
				NaxosFilenameFilter.class);
		bind(IDirectoryMonitor.class).annotatedWith(WatchFiles.class)
				.toProvider(WatchFilesDirectoryMonitorProvider.class);
		bind(IStrings.class).to(NaxosStrings.class).in(Scopes.SINGLETON);
		bind(IImageFactory.class).to(NaxosImageFactory.class).in(
				Scopes.SINGLETON);
		bind(IURLFactory.class).to(NaxosURLFactory.class).in(Scopes.SINGLETON);
		bind(IPreferences.class).to(NaxosPreferences.class).asEagerSingleton();
	}

}
