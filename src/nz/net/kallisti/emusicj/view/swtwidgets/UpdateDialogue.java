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
package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.misc.BrowserLauncher;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * $Id$
 * 
 * @author robin
 */
public class UpdateDialogue {

	private final String newVersion;
	private final Shell shell;
	private Shell dialog;
	private Button againButton;
	private final SWTView view;
	private final IPreferences prefs;
	private final IStrings strings;
	private final IURLFactory urlFactory;

	public UpdateDialogue(Shell shell, SWTView view, String newVersion,
			IPreferences prefs, IStrings strings, IURLFactory urlFactory) {
		this.shell = shell;
		this.view = view;
		this.newVersion = newVersion;
		this.prefs = prefs;
		this.strings = strings;
		this.urlFactory = urlFactory;
	}

	public void open() {
		dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(3, false));
		dialog.setText("New Version Available");
		Label textLabel = new Label(dialog, SWT.WRAP);
		textLabel.setText("A new version of " + strings.getAppName()
				+ " is available.\n" + "Version " + newVersion
				+ " has been released.\n" + "It can be downloaded from:\n"
				+ urlFactory.getAppURL() + "\n\nYou must close the download "
				+ "manager before installing the new version.");
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		textLabel.setLayoutData(gd);
		againButton = new Button(dialog, SWT.CHECK);
		againButton.setText("Don't check for updates automatically");
		gd = new GridData();
		gd.horizontalSpan = 3;
		againButton.setLayoutData(gd);
		// Label againLabel = new Label(dialog, SWT.NONE);
		// againLabel.setText("Don't check for updates automatically");

		Button openInBrowser = new Button(dialog, SWT.PUSH);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		openInBrowser.setLayoutData(gd);
		openInBrowser.setText("Open in browser");
		openInBrowser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent e) {
				openPageInBrowser();
			}
		});

		Button copy = new Button(dialog, SWT.PUSH);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		copy.setLayoutData(gd);
		copy.setText("Copy URL to clipboard");
		copy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent e) {
				copyToClip();
			}
		});

		Button close = new Button(dialog, SWT.PUSH);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		close.setLayoutData(gd);
		close.setText("Close");
		close.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent e) {
				close();
			}
		});

		dialog.pack();
		dialog.layout();
		dialog.open();
	}

	/**
	 * Copies the application URL to the clipboard
	 */
	protected void copyToClip() {
		String textData = urlFactory.getAppURL().toString();
		TextTransfer textTransfer = TextTransfer.getInstance();
		SWTView.getClipboard().setContents(new Object[] { textData },
				new Transfer[] { textTransfer });
	}

	public void close() {
		prefs.setCheckForUpdates(!againButton.getSelection());
		dialog.dispose();
		new Thread() {
			@Override
			public void run() {
				prefs.save();
			}
		}.start();
	}

	public void openPageInBrowser() {
		new Thread() {
			@Override
			public void run() {
				try {
					BrowserLauncher.openURL(urlFactory.getAppURL());
				} catch (Exception e) {
					view
							.error(
									"Error launching browser",
									"There seemed to be a "
											+ "problem launching the browser. The user manual can"
											+ "be found at "
											+ urlFactory.getAppURL());
				}
			}
		}.start();
	}

}
