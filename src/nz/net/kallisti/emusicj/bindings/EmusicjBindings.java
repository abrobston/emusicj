package nz.net.kallisti.emusicj.bindings;

import java.io.FilenameFilter;

import nz.net.kallisti.emusicj.bindingtypes.WatchFiles;
import nz.net.kallisti.emusicj.controller.EmusicjPreferences;
import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitor;
import nz.net.kallisti.emusicj.misc.EMPFilenameFilter;
import nz.net.kallisti.emusicj.providers.WatchFilesDirectoryMonitorProvider;
import nz.net.kallisti.emusicj.strings.EmusicjStrings;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.urls.emusicj.EmusicjURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.images.emusicj.EmusicjImageFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
/**
 * <p>Bindings that are specific to the emusic variant of the application</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class EmusicjBindings extends AbstractModule {

	public void configure() {
		bind(FilenameFilter.class).annotatedWith(WatchFiles.class).to(EMPFilenameFilter.class);
		bind(IDirectoryMonitor.class).annotatedWith(WatchFiles.class).toProvider(WatchFilesDirectoryMonitorProvider.class);
		bind(IStrings.class).to(EmusicjStrings.class).in(Scopes.SINGLETON);
		bind(IImageFactory.class).to(EmusicjImageFactory.class).in(Scopes.SINGLETON);
		bind(IURLFactory.class).to(EmusicjURLFactory.class).in(Scopes.SINGLETON);
		bind(IPreferences.class).to(EmusicjPreferences.class).in(Scopes.SINGLETON);
	}

}
