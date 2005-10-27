package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.controller.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public class UpdateDialogue {

	private String newVersion;
	private Shell shell;
	private Shell dialog;
	private Button againButton;

	public UpdateDialogue(Shell shell, String newVersion) {
		this.shell = shell;
		this.newVersion = newVersion;
	}

	public void open() {
		dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout (new GridLayout(1,false));
		dialog.setText("New Version of "+Constants.APPNAME);
		Label textLabel = new Label(dialog, SWT.WRAP);
		textLabel.setText("A new version of "+Constants.APPNAME+" is available.\n" +
				"Version "+newVersion+" has been released.\n"+
				"It can be downloaded from "+Constants.APPURL);
//		GridData gd = new GridData();
//		gd.horizontalSpan = 2;
//		textLabel.setLayoutData(gd);
		againButton = new Button(dialog, SWT.CHECK);
		againButton.setText("Don't check for updates automatically");
//		Label againLabel = new Label(dialog, SWT.NONE);
//		againLabel.setText("Don't check for updates automatically");
		
		final Button close = new Button(dialog, SWT.PUSH);
		GridData gd = new GridData();
		gd.horizontalAlignment=SWT.RIGHT;
//		gd.horizontalSpan = 2;
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
		dialog.layout();
		dialog.open();
	}

	public void close() {
		final Preferences prefs = Preferences.getInstance();
		prefs.setCheckForUpdates(!againButton.getSelection());
		dialog.dispose();
		new Thread() { public void run() { prefs.save(); } }.run();
	}
	
}
