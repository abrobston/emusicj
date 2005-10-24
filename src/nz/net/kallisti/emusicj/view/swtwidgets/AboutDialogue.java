package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.Constants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Displays some information about the program</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class AboutDialogue {

	private Shell shell;
	private Shell dialog;

	/**
	 * @param shell
	 */
	public AboutDialogue(Shell shell) {
		super();
		this.shell = shell;		
	}
	
	public void open() {
		dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout (new GridLayout(1,false));
		dialog.setText("About "+Constants.APPNAME);
		Label version = new Label(dialog, SWT.NONE);
		version.setText(Constants.APPNAME+" v"+Constants.VERSION);
		Text textField = new Text(dialog, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		textField.setText(Constants.ABOUT_BOX_TEXT);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Button closeButton = new Button(dialog, SWT.PUSH);
		closeButton.setText("Close");
		GridData gd = new GridData();
		gd.horizontalAlignment=SWT.RIGHT;
		closeButton.setLayoutData(gd);
		closeButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				close();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				close();
			}			
		});
		dialog.pack();
		dialog.setSize(400,300);
		dialog.layout();
		dialog.open();
	}
	
	public void close() {
		dialog.dispose();
	}

}
