package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.controller.Preferences;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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
		dialog.setLayout (new GridLayout(2,false));
		dialog.setText("New Version of "+Constants.APPNAME);
		Label textLabel = new Label(dialog, SWT.WRAP);
		textLabel.setText("A new version of "+Constants.APPNAME+" is available.\n" +
				"Version "+newVersion+" has been released.\n"+
				"It can be downloaded from "+Constants.APPURL);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		textLabel.setLayoutData(gd);
		againButton = new Button(dialog, SWT.CHECK);
		againButton.setText("Don't check for updates automatically");
		gd = new GridData();
        gd.horizontalSpan = 2;
        againButton.setLayoutData(gd);
//		Label againLabel = new Label(dialog, SWT.NONE);
//		againLabel.setText("Don't check for updates automatically");
		
        Button copy = new Button(dialog, SWT.PUSH);
        gd = new GridData();
        gd.horizontalAlignment=SWT.RIGHT;
        copy.setLayoutData(gd);
        copy.setText("Copy URL to clipboard");
        copy.addSelectionListener(new SelectionListener(){
            public void widgetSelected(SelectionEvent e) {
                copyToClip();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                copyToClip();
            }           
        });
        
        Button close = new Button(dialog, SWT.PUSH);
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
		dialog.layout();
		dialog.open();
	}

	/**
     * Copies the application URL to the clipboard
     */
    protected void copyToClip() {
        String textData = Constants.APPURL;
        TextTransfer textTransfer = TextTransfer.getInstance();
        SWTView.getClipboard().setContents(new Object[]{textData}, new Transfer[]{textTransfer});
    }

    public void close() {
		final Preferences prefs = Preferences.getInstance();
		prefs.setCheckForUpdates(!againButton.getSelection());
		dialog.dispose();
		new Thread() { public void run() { prefs.save(); } }.run();
	}
	
}
