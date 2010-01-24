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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.mediaplayer.IMediaPlayerSync;
import nz.net.kallisti.emusicj.mediaplayer.IPlayer;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * Displays a preferences dialogue, and updates the supplied Preferences object
 * as things change.
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class PreferencesDialogue {

	private final IPreferences prefs;
	private final Shell shell;
	private Shell dialog;
	protected String filePattern;
	protected String filePath;
	private int minDL;
	private final boolean checkForUpdates;
	private Button updatesButton;
	private Button noProxy;
	private Text proxyHost;
	private Text proxyPort;
	protected boolean proxyModified = false;
	private Text dropDir;
	private boolean dropDirModified = false;
	private final boolean removeCompletedDownloads;
	private Button autoCleanup;
	private final IStrings strings;
	private final IMediaPlayerSync mediaSync;
	private IPlayer selectedPlayer = null;

	/**
	 * @param display
	 * @param instance
	 */
	public PreferencesDialogue(Shell shell, IPreferences prefs,
			IStrings strings, IMediaPlayerSync mediaSync) {
		this.shell = shell;
		this.prefs = prefs;
		this.strings = strings;
		this.mediaSync = mediaSync;
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
		Group filesGroup = new Group(dialog, SWT.NONE);
		filesGroup.setLayout(new GridLayout(3, false));
		filesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (prefs.isAutoloadAllowed())
			filesGroup.setText("Files");
		else
			filesGroup.setText("Downloads Folder Location");

		Label pathLabel = new Label(filesGroup, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		pathLabel.setLayoutData(gd);
		pathLabel.setText("Save files to:");
		final Text savePath = new Text(filesGroup, SWT.READ_ONLY);
		savePath.setText(prefs.getSavePath());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		savePath.setLayoutData(gd);
		savePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filePath = ((Text) e.getSource()).getText();
			}
		});
		Button browseSavePath = new Button(filesGroup, SWT.PUSH);
		browseSavePath.setText("Browse...");
		browseSavePath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setFilterPath(filePath);
				String path = dialog.open();
				if (path != null)
					savePath.setText(path);
			}
		});

		Control syncWidget = createMediaPlayerSync(filesGroup);
		if (syncWidget != null) {
			gd = new GridData();
			gd.horizontalSpan = 3;
			syncWidget.setLayoutData(gd);
		}

		if (prefs.allowSaveFileAs()) {
			Label fileName = new Label(filesGroup, SWT.NONE);
			fileName.setText("Save files as:");
			gd = new GridData();
			gd.horizontalSpan = 3;
			pathLabel.setLayoutData(gd);
			final Text savePattern = new Text(filesGroup, SWT.BORDER);
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 3;
			savePattern.setLayoutData(gd);
			savePattern.setText(prefs.getFilePattern());
			savePattern.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					filePattern = ((Text) e.getSource()).getText();
				}
			});
			Label savePatternKey = new Label(filesGroup, SWT.NONE);
			savePatternKey.setText(strings.getFileNamingDetails());
			gd = new GridData();
			gd.horizontalSpan = 3;
			savePatternKey.setLayoutData(gd);
		}

		if (prefs.isAutoloadAllowed()) {
			gd = new GridData();
			gd.horizontalSpan = 3;
			Label dropDirLabel = new Label(filesGroup, SWT.NONE);
			dropDirLabel.setLayoutData(gd);
			dropDirLabel.setText(strings.getPrefsAutoLoadDescription());
			dropDir = new Text(filesGroup, SWT.READ_ONLY);
			dropDir.setText(prefs.getProperty("dropDir", ""));
			dropDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false));
			Button clearDropDir = new Button(filesGroup, SWT.PUSH);
			clearDropDir.setText("Clear");
			clearDropDir.addSelectionListener(new SelectionAdapter() {
				@Override
				public void action(SelectionEvent e) {
					dropDir.setText("");
					dropDirModified = true;
				}
			});

			Button browseDropDir = new Button(filesGroup, SWT.PUSH);
			browseDropDir.setText("Browse...");
			browseDropDir.addSelectionListener(new SelectionAdapter() {
				@Override
				public void action(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(shell);
					dialog.setFilterPath(dropDir.getText());
					String path = dialog.open();
					if (path != null)
						dropDir.setText(path);
					dropDirModified = true;
				}
			});
		}

		Group downloads = new Group(dialog, SWT.NONE);
		downloads.setLayout(new GridLayout(2, false));
		downloads.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		downloads.setText("Downloads Control");

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
		minDLLabel.setText(strings.prefsConcurrentDownloads());

		autoCleanup = new Button(downloads, SWT.CHECK);
		autoCleanup.setSelection(removeCompletedDownloads);
		autoCleanup
				.setText("Automatically remove completed downloads from list");
		gd = new GridData();
		gd.horizontalSpan = 2;
		autoCleanup.setLayoutData(gd);

		Group updates = new Group(dialog, SWT.NONE);
		updates.setLayout(new GridLayout(2, false));
		updates.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		updates.setText("Updates");

		updatesButton = new Button(updates, SWT.CHECK);
		updatesButton.setSelection(checkForUpdates);
		updatesButton.setText(strings.prefsAutomaticallyCheck());

		createNetworkPanel();

		final Button close = new Button(dialog, SWT.PUSH);
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
		// dialog.setSize(200,200);
		dialog.layout();
		dialog.open();
	}

	/**
	 * This will add the media player sync stuff to the group, if it is
	 * available. As a side-effect, it sets {@link #selectedPlayer} when the
	 * selection is updated.
	 * 
	 * @return the widget that was created, or <code>null</code> if none was
	 */
	private Composite createMediaPlayerSync(Composite composite) {
		Set<IPlayer> playerSet = mediaSync.supportedPlayers();
		if (playerSet.size() == 0)
			return null;

		Composite comp = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		final List<IPlayer> players = new ArrayList<IPlayer>(playerSet);
		// sort by name
		Collections.sort(players, new Comparator<IPlayer>() {
			public int compare(IPlayer o1, IPlayer o2) {
				return o1.playerName().compareTo(o2.playerName());
			}
		});
		final Combo dropDown = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY
				| SWT.BORDER);
		GridData gd = new GridData();
		gd.verticalAlignment = SWT.CENTER;
		dropDown.setLayoutData(gd);
		// First add a 'nothing' option
		dropDown.add("None");
		int selectIndex = 0;
		int count = 0;
		String selectedKey = prefs.getMediaPlayerSync();
		for (IPlayer player : players) {
			count++; // the index starts at 1, 0 is 'none'
			dropDown.add(player.playerName());
			if (player.key().equals(selectedKey)) {
				selectIndex = count;
			}
		}
		dropDown.select(selectIndex);
		// Add a null at the start of the list so that we can use this to easily
		// work out what was selected later
		players.add(0, null);

		dropDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				selectedPlayer = players.get(dropDown.getSelectionIndex());
			}
		});
		Label label = new Label(comp, SWT.NONE);
		label.setText("Media player to synchronise with");
		gd = new GridData();
		gd.verticalAlignment = SWT.CENTER;
		label.setLayoutData(gd);
		comp.pack();
		comp.layout();
		comp.setSize(0, dropDown.getSize().y + 10);
		return comp;
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
			@Override
			public void action(SelectionEvent ev) {
				enableDisableProxyFields(manualProxy.getSelection());
			}

		});
		if (prefs.getProxyPort() != 0)
			proxyPort.setText(prefs.getProxyPort() + "");
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
			prefs.setProxy(noProxy.getSelection(), proxyHost.getText(),
					proxyPort.getText());
		}
		if (dropDirModified) {
			prefs.setDropDir(dropDir.getText());
		}
		prefs.setMediaPlayerSync(selectedPlayer != null ? selectedPlayer.key()
				: null);
		dialog.dispose();
		new Thread() {
			@Override
			public void run() {
				prefs.save();
			}
		}.start();
	}

}
