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
package nz.net.kallisti.emusicj.view;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.network.http.proxy.ProxyCredentialsProvider.CredsCallback;

import org.apache.commons.httpclient.auth.AuthScheme;

public interface IEMusicView {

	/**
	 * <p>
	 * These states are used to tell the view the large-scale progress of the
	 * system so that it can report them to the user, and change how it looks
	 * accordingly (e.g. by providing a splash screen)
	 * </p>
	 * <ul>
	 * <li><code>STARTUP</code> indicates the program is initialising</li>
	 * <li><code>RUNNING</code> indicates the program is in its normal
	 * running mode</li>
	 * </ul>
	 */
	enum ViewState {
		STARTUP, RUNNING
	}

	/**
	 * Sets the state that the view should run in. This can be used to provide a
	 * spashscreen or something. If STATE_STARTUP is set, then a startup screen
	 * may be activated. When it is set to STATE_RUNNING, that will be removed,
	 * and the standard interface will be put up.
	 * 
	 * @param s
	 *            the new state for the view, one of the constants with STATE_
	 *            as the prefix.
	 */
	public void setState(ViewState state);

	/**
	 * This runs the event loop of the view. This should recieve events from the
	 * user, and pass the reqests on to the controller. It will only return when
	 * the user requests the application quit.
	 * 
	 * @param controller
	 *            the controller to pass events on to
	 */
	public void processEvents(IEMusicController controller);

	/**
	 * A view has a downloads model to keep track of what downloads are in the
	 * system. This tells the view the model to watch.
	 * 
	 * @param model
	 *            the model that the view will use
	 */
	public void setDownloadsModel(IDownloadsModel model);

	/**
	 * Tells the view to display an error message
	 * 
	 * @param msgTitle
	 *            the title of the error
	 * @param msg
	 *            the content of the error
	 */
	public void error(String msgTitle, String msg);

	/**
	 * Called if a new version of the program is available
	 * 
	 * @param newVersion
	 *            the new version
	 */
	public void updateAvailable(String newVersion);

	/**
	 * Notifies the view of the current number of files in the list, and how
	 * many are currently downloading
	 * 
	 * @param dl
	 *            the number currently downloading
	 * @param finished
	 *            the number of finished downloads
	 * @param total
	 *            the total number of files
	 */
	public void downloadCount(int dl, int finished, int total);

	/**
	 * If the 'all paused' state is changed, this tells the view about it.
	 * 
	 * @param state
	 *            the new all paused state, true if downloads are paused, false
	 *            otherwise.
	 */
	public void pausedStateChanged(boolean state);

	/**
	 * Gets the view to ask for the credentials. When they have been received
	 * from the user, they are sent to the callback. It provides the details on
	 * the proxy so that they can be displayed to the user. If the view is not
	 * yet active, then the request should be deferred until it is.
	 * 
	 * @param authScheme
	 *            the authentication scheme for this proxy
	 * @param host
	 *            the proxy hostname
	 * @param port
	 *            the proxy port
	 * @param credsCallback
	 *            the callback that should be provided with the proxy details
	 */
	public void getProxyCredentials(AuthScheme authScheme, String host,
			int port, CredsCallback credsCallback);

}
