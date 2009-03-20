/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat, Curtis Cooley

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
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * Displays a preferences dialogue, and updates the supplied Preferences object as things change.
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class PreferencesDialogue {

	private IPreferences prefs;
	private Shell shell;
	private Shell dialog;
	protected String filePattern;
	protected String filePath;
	private int minDL;
	private boolean checkForUpdates;
	private Button updatesButton;
	private Button noProxy;
	private Text proxyHost;
	private Text proxyPort;
	protected boolean proxyModified = false;
	private Text dropDir;
	private boolean dropDirModified = false;
	private boolean removeCompletedDownloads;
	private Button autoCleanup;
	private final IStrings strings;

	/**
	 * @param display
	 * @param instance
	 */
	public PreferencesDialogue(Shell shell, IPreferences prefs, IStrings strings) {
		this.shell = shell;
		this.prefs = prefs;
		this.strings = strings;
		filePath = prefs.getSavePath();
		filePattern = prefs.getFilePattern();
		minDL = prefs.getMinDownloads();
		checkForUpdates = prefs.checkForUpdates();
		removeCompletedDownloads = prefs.removeCompletedDownloads();
	}

	/**
	 * 
	 */
	public void open() {
		dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(1, false));
		dialog.setText("Preferences");
		Group files = new Group(dialog, SWT.NONE);
		files.setLayout(new GridLayout(3, false));
		files.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		files.setText("Files");

		Label pathLabel = new Label(files, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		pathLabel.setLayoutData(gd);
		pathLabel.setText("Save files to:");
		final Text savePath = new Text(files, SWT.READ_ONLY);
		savePath.setText(prefs.getSavePath());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		savePath.setLayoutData(gd);
		savePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filePath = ((Text) e.getSource()).getText();
			}
		});
		Button browseSavePath = new Button(files, SWT.PUSH);
		browseSavePath.setText("Browse...");
		browseSavePath.addSelectionListener(new SelectionAdapter(){
			public void action(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog (shell);
				dialog.setFilterPath(filePath);
				String path = dialog.open();
				if (path != null) savePath.setText(path);
			}
		});

		if (prefs.allowSaveFileAs()) {
			Label fileName = new Label(files, SWT.NONE);
			fileName.setText("Save files as:");
			gd = new GridData();
			gd.horizontalSpan = 3;
			pathLabel.setLayoutData(gd);
			final Text savePattern = new Text(files, SWT.BORDER);
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 3;
			savePattern.setLayoutData(gd);
			savePattern.setText(prefs.getFilePattern());
			savePattern.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					filePattern = ((Text) e.getSource()).getText();
				}
			});
			Label savePatternKey = new Label(files, SWT.NONE);
			savePatternKey.setText(strings.getFileNamingDetails());
			gd = new GridData();
			gd.horizontalSpan = 3;
			savePatternKey.setLayoutData(gd);
		}
		gd = new GridData();
		gd.horizontalSpan = 3;
		Label dropDirLabel = new Label(files, SWT.NONE);
		dropDirLabel.setLayoutData(gd);
		dropDirLabel.setText(strings.getAutoLoadDescription());
		dropDir = new Text(files, SWT.READ_ONLY);
		dropDir.setText(prefs.getProperty("dropDir", ""));
		dropDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Button clearDropDir = new Button(files, SWT.PUSH);
		clearDropDir.setText("Clear");
		clearDropDir.addSelectionListener(new SelectionAdapter(){
			public void action(SelectionEvent e) {
				dropDir.setText("");
				dropDirModified = true;
			}
		});

		Button browseDropDir = new Button(files, SWT.PUSH);
		browseDropDir.setText("Browse...");
		browseDropDir.addSelectionListener(new SelectionAdapter(){
			public void action(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog (shell);
				dialog.setFilterPath(dropDir.getText());
				String path = dialog.open();
				if (path != null) dropDir.setText(path);
				dropDirModified = true;
			}
		});

		Group downloads = new Group(dialog, SWT.NONE);
		downloads.setLayout(new GridLayout(2, false));
		downloads.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		downloads.setText("Downloads");

		final Spinner minDLSpin = new Spinner(downloads, SWT.BORDER);
		minDLSpin.setMinimum(0);
		minDLSpin.setMaximum(10);
		minDLSpin.setSelection(minDL);
		minDLSpin.setIncrement(1);
		minDLSpin.setPageIncrement(1);
		minDLSpin.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				minDL = minDLSpin.getSelection();
			}
		});
		minDLSpin.pack();
		Label minDLLabel = new Label(downloads, SWT.NONE);
		minDLLabel.setText("Preferred number of downloads at once");
		
		autoCleanup = new Button(downloads, SWT.CHECK);
		autoCleanup.setSelection(removeCompletedDownloads);
		autoCleanup.setText("Automatically remove completed downloads from list");
		gd = new GridData();
		gd.horizontalSpan = 2;
		autoCleanup.setLayoutData(gd);
		
		Group updates = new Group(dialog, SWT.NONE);
		updates.setLayout(new GridLayout(2, false));
		updates.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		updates.setText("Updates");

		updatesButton = new Button(updates, SWT.CHECK);
		updatesButton.setSelection(checkForUpdates);
		updatesButton.setText("Automatically check for updates to the program");

		createNetworkPanel();

		final Button close = new Button(dialog, SWT.PUSH);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		close.setLayoutData(gd);
		close.setText("Close");
		close.addSelectionListener(new SelectionAdapter(){
			public void action(SelectionEvent e) {
				close();
			}			
		});

		dialog.pack();
		// dialog.setSize(200,200);
		dialog.layout();
		dialog.open();
	}

	private void createNetworkPanel() {
		Group network = new Group(dialog, SWT.NONE);
		network.setLayout(new GridLayout(3, false));
		network.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		network.setText("Proxy Settings");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		noProxy = new Button(network, SWT.RADIO);
		noProxy.setText("No Proxy");
		noProxy.setLayoutData(gd);

		final Button manualProxy = new Button(network, SWT.RADIO);
		manualProxy.setText("Manual Settings");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		manualProxy.setLayoutData(gd);

		Label hostLabel = new Label(network, SWT.NONE);
		hostLabel.setText("Proxy host:");
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		hostLabel.setLayoutData(gd);
		new Label(network, SWT.NONE).setText("Proxy port:");
		proxyHost = new Text(network, SWT.BORDER);
		proxyHost.setText(prefs.getProxyHost());
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		proxyHost.setLayoutData(gd);
		proxyHost.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				proxyModified = true;
			}
		});
		proxyPort = new Text(network, SWT.BORDER);
		noProxy.addSelectionListener(new SelectionAdapter() {
			public void action(SelectionEvent ev) {
				enableDisableProxyFields(manualProxy.getSelection());
			}

		});
		if (prefs.getProxyPort() != 0) proxyPort.setText(prefs.getProxyPort() + "");
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		proxyPort.setLayoutData(gd);
		if (prefs.usingProxy()) {
			manualProxy.setSelection(true);
			enableDisableProxyFields(true);
		} else {
			noProxy.setSelection(true);
			enableDisableProxyFields(false);
		}
	}

	private void enableDisableProxyFields(boolean enabled) {
		proxyHost.setEnabled(enabled);
		proxyPort.setEnabled(enabled);
		proxyModified = true;
	}

	/**
	 * Close the dialog and save the preferences
	 */
	public void close() {
		if (prefs.allowSaveFileAs())
			prefs.setFilePattern(filePattern);
		prefs.setSavePath(filePath);
		prefs.setMinDownloads(minDL);
		prefs.setCheckForUpdates(updatesButton.getSelection());
		prefs.setRemoveCompletedDownloads(autoCleanup.getSelection());
		if (proxyModified) {
			prefs.setProxy(noProxy.getSelection(), proxyHost.getText(), proxyPort.getText());
		}
		if (dropDirModified) {
			prefs.setDropDir(dropDir.getText());
		}
		dialog.dispose();
		new Thread() {
			public void run() {
				prefs.save();
			}
		}.start();
	}

}
