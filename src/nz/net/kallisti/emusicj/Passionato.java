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
import nz.net.kallisti.emusicj.bindings.PassionatoBindings;
import nz.net.kallisti.emusicj.controller.IEmusicjController;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

/**
 * <p>
 * This is the main class for the Passionato downloader. It doesn't do a whole
 * lot except for start the other parts of the system going. This involves
 * creating an instance of the controller, and giving it a view to use.
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class Passionato {

	/**
	 * Initialises the components of the system.
	 * 
	 * @param args
	 *            command line parameters.
	 */
	public Passionato(String[] args) {
		Injector injector = Guice.createInjector(Stage.PRODUCTION,
				new Bindings(), new PassionatoBindings());
		IEmusicjView view = injector.getInstance(IEmusicjView.class);
		view.setState(IEmusicjView.ViewState.STARTUP);
		IEmusicjController controller = injector
				.getInstance(IEmusicjController.class);
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
	public void startApp(IEmusicjController controller, String[] args) {
		controller.run(args);
	}

	public static void main(String[] args) {
		new Passionato(args);
	}

}
