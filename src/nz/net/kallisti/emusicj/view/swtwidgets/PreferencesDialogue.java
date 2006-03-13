package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.controller.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * <p>Displays a preferences dialogue, and updates the supplied Preferences
 * object as things change.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class PreferencesDialogue {

	private Preferences prefs;
	private Shell shell;
	private Shell dialog;
	protected String filePattern;
	protected String filePath;
	private int minDL;
	private boolean checkForUpdates;
	private Button updatesButton;
	private Button useProxyButton;
	private Text proxyHost;
	private Text proxyPort;
	protected boolean proxyModified=false;
	private Text dropDir;
	private boolean dropDirModified = false;

	/**
	 * @param display
	 * @param instance
	 */
	public PreferencesDialogue(Shell shell, Preferences prefs) {
		this.shell = shell;
		this.prefs = prefs;
		filePath = prefs.getSavePath();
		filePattern = prefs.getFilePattern();
		minDL = prefs.getMinDownloads();
		checkForUpdates = prefs.checkForUpdates();
	}

	/**
	 * 
	 */
	public void open() {
		dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout (new GridLayout(1,false));
		dialog.setText(Constants.APPNAME+" Preferences");
		Group files = new Group(dialog, SWT.NONE);
		files.setLayout(new GridLayout(3,false));
		files.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		files.setText("Files");
		
		Label pathLabel = new Label(files, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan=3;
		pathLabel.setLayoutData(gd);
		pathLabel.setText("Save files to:");
		final Text savePath = new Text(files,SWT.READ_ONLY);
		savePath.setText(prefs.getSavePath());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan=2;
		savePath.setLayoutData(gd);
		savePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filePath=((Text)e.getSource()).getText();		
			}
		});
		Button browseSavePath = new Button(files, SWT.PUSH);
		browseSavePath.setText("Browse...");
		browseSavePath.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				doBrowse();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				doBrowse();
			}
			private void doBrowse() {
				DirectoryDialog dialog = new DirectoryDialog (shell);
				dialog.setFilterPath(filePath);
				String path = dialog.open();
				if (path != null)
					savePath.setText(path);
			}
		});
		
		Label fileName = new Label(files, SWT.NONE);
		fileName.setText("Save files as:");
		gd = new GridData();
		gd.horizontalSpan=3;
		pathLabel.setLayoutData(gd);
		final Text savePattern = new Text(files,SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan=3;
		savePattern.setLayoutData(gd);
		savePattern.setText(prefs.getFilePattern());
		savePattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filePattern = ((Text)e.getSource()).getText();		
			}
		});
		Label savePatternKey = new Label(files, SWT.NONE);
		savePatternKey.setText("%a=album, %b=artist, %n=track number, %t=track name\n" +
				"Note: '.mp3' will be attached to the end of this");
		gd = new GridData();
		gd.horizontalSpan=3;
		savePatternKey.setLayoutData(gd);
		
		gd = new GridData();
		gd.horizontalSpan=3;
		Label dropDirLabel = new Label(files, SWT.NONE);
		dropDirLabel.setLayoutData(gd);
		dropDirLabel.setText("Automatically load .emp files from:");
		dropDir = new Text(files, SWT.READ_ONLY);
		dropDir.setText(prefs.getProperty("dropDir",""));
		dropDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Button clearDropDir = new Button(files, SWT.PUSH);
		clearDropDir.setText("Clear");
		clearDropDir.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				doClear();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				doClear();
			}
			private void doClear() {
				dropDir.setText("");
				dropDirModified = true;
			}
		});
		
		Button browseDropDir = new Button(files, SWT.PUSH);
		browseDropDir.setText("Browse...");
		browseDropDir.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				doBrowse();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				doBrowse();
			}
			private void doBrowse() {
				DirectoryDialog dialog = new DirectoryDialog (shell);
				dialog.setFilterPath(dropDir.getText());
				String path = dialog.open();
				if (path != null)
					dropDir.setText(path);
				dropDirModified = true;
			}
		});
		
		Group downloads = new Group(dialog, SWT.NONE);
		downloads.setLayout(new GridLayout(2,false));
		downloads.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		downloads.setText("Downloads");
		
		final Spinner minDLSpin = new Spinner (downloads, SWT.BORDER);
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
		minDLLabel.setText("Minimum number of downloads at once");
		
		Group updates = new Group(dialog, SWT.NONE);
		updates.setLayout(new GridLayout(2,false));
		updates.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		updates.setText("Updates");
		
		updatesButton = new Button(updates, SWT.CHECK);
		updatesButton.setSelection(checkForUpdates);
		updatesButton.setText("Automatically check for updates to the program");
		
		createNetworkPanel();
		
		final Button close = new Button(dialog, SWT.PUSH);
		gd = new GridData();
		gd.horizontalAlignment=SWT.RIGHT;
		close.setLayoutData(gd);
		close.setText("Close");
		close.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				close();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				close();
			}			
		});

		dialog.pack();
		//dialog.setSize(200,200);
		dialog.layout();
		dialog.open();
	}

	private void createNetworkPanel() {
		Group network = new Group(dialog, SWT.NONE);
		network.setLayout(new GridLayout(3,false));
		network.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		network.setText("Network");
		
		new Label(network, SWT.NONE).setText("Proxy host:");
		new Label(network, SWT.NONE).setText("Proxy port:");
		new Label(network, SWT.NONE);
		proxyHost = new Text(network,SWT.BORDER);
		proxyHost.setText(prefs.getProxyHost());
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		proxyHost.setLayoutData(gd);
		proxyHost.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				proxyModified = true;
			}
		});
		proxyPort = new Text(network,SWT.BORDER);
		useProxyButton = new Button(network, SWT.BUTTON1);
		useProxyButton.setText("Clear");
		useProxyButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				clearProxyFields();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
				clearProxyFields();
			}
			
			private void clearProxyFields() {
				proxyModified = true;
				proxyHost.setText("");
				proxyPort.setText("");
			}
			
		});
		if (prefs.getProxyPort() != 0)
			proxyPort.setText(prefs.getProxyPort()+"");
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		proxyPort.setLayoutData(gd);
	}
	
	/**
	 * Close the dialog and save the preferences
	 */
	public void close() {
		prefs.setFilePattern(filePattern);
		prefs.setSavePath(filePath);
		prefs.setMinDownloads(minDL);
		prefs.setCheckForUpdates(updatesButton.getSelection());
		if (proxyModified) {
			prefs.setProxy(proxyHost.getText(), proxyPort.getText());
		}
		if (dropDirModified) {
			prefs.setDropDir(dropDir.getText());
		}
		dialog.dispose();
		new Thread() { public void run() { prefs.save(); } }.start();
	}
	
}
