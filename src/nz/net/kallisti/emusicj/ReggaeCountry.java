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
package nz.net.kallisti.emusicj;

import nz.net.kallisti.emusicj.bindings.Bindings;
import nz.net.kallisti.emusicj.bindings.ReggaeCountryBindings;
import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.view.IEMusicView;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

/**
 * <p>
 * This is the main class for the eMusic/J downloader. It doesn't do a whole lot
 * except for start the other parts of the system going. This involves creating
 * an instance of the controller, and giving it a view to use.
 * </p>
 * 
 * <p>
 * $Id: EMusicJ.java 156 2007-03-17 05:30:24Z robin $
 * </p>
 * 
 * @author robin
 */
public class ReggaeCountry {

	/**
	 * Initialises the components of the system.
	 * 
	 * @param args
	 *            command line parameters.
	 */
	public ReggaeCountry(final String[] args) {
		Injector injector = Guice.createInjector(Stage.PRODUCTION,
				new Bindings(), new ReggaeCountryBindings());
		IEMusicView view = injector.getInstance(IEMusicView.class);
		view.setState(IEMusicView.ViewState.STARTUP);
		IEMusicController controller = injector
				.getInstance(IEMusicController.class);
		startApp(controller, args);
	}

	/**
	 * This may be overridden to provide custom starters for other platforms
	 * 
	 * @param controller
	 *            the application controller
	 * @param args
	 *            the command line arguments
	 */
	public void startApp(IEMusicController controller, String[] args) {
		controller.run(args);
	}

	public static void main(String[] args) {
		new ReggaeCountry(args);
	}

}
