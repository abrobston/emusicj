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

	/**
	 * @param display
	 * @param instance
	 */
	public PreferencesDialogue(Shell shell, Preferences prefs) {
		this.shell = shell;
		this.prefs = prefs;
		filePath = prefs.getPath();
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
		files.setLayout(new GridLayout(2,false));
		files.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		files.setText("Files");
		
		Label pathLabel = new Label(files, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan=2;
		pathLabel.setLayoutData(gd);
		pathLabel.setText("Save files to:");
		final Text savePath = new Text(files,SWT.READ_ONLY);
		savePath.setText(prefs.getPath());
		savePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
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
		gd.horizontalSpan=2;
		pathLabel.setLayoutData(gd);
		final Text savePattern = new Text(files,SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan=2;
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
		gd.horizontalSpan=2;
		savePatternKey.setLayoutData(gd);
		
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
	
	/**
	 * Close the dialog and save the preferences
	 */
	public void close() {
		prefs.setFilePattern(filePattern);
		prefs.setPath(filePath);
		prefs.setMinDownloads(minDL);
		prefs.setCheckForUpdates(updatesButton.getSelection());
		dialog.dispose();
		new Thread() { public void run() { prefs.save(); } }.run();
	}
	
}
