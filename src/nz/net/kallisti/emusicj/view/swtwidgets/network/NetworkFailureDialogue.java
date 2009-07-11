package nz.net.kallisti.emusicj.view.swtwidgets.network;

import nz.net.kallisti.emusicj.strings.IStrings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * This shows a dialogue to the user explaining to them that their network is
 * acting up.
 * </p>
 * 
 * @author robin
 */
public class NetworkFailureDialogue {

	private final Shell shell;
	private Shell dialog;
	private final IStrings strings;

	/**
	 * Instantiate the dialogue. Use {@link #open()} to show it
	 * 
	 * @param shell
	 *            the SWT shell to create the dialogue in
	 */
	public NetworkFailureDialogue(Shell shell, IStrings strings) {
		this.shell = shell;
		this.strings = strings;
	}

	/**
	 * Displays the dialogue
	 */
	public void open() {
		dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(2, false));
		dialog.setText("Network Problem");
		Label text = new Label(dialog, SWT.WRAP);
		text.setText(strings.networkFailureMessage());
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false,
				2, 1));
		// TODO complete
	}

}
