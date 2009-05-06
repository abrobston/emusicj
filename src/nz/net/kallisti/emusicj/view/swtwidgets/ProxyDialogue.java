package nz.net.kallisti.emusicj.view.swtwidgets;

import nz.net.kallisti.emusicj.network.http.ProxyCredentialsProvider.CredsCallback;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.apache.commons.httpclient.auth.AuthScheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * This presents a simple username/password box to the user to allow them to
 * fill out the proxy credentials.
 * </p>
 * 
 * @author robin
 */
public class ProxyDialogue {

	private final String host;
	private final int port;
	private final CredsCallback credsCallback;
	private final Shell shell;
	private Shell dialog;
	private Text usernameField;
	private Text passwordField;
	private boolean isClosing = false;

	/**
	 * Initialises the proxy credentials dialogue
	 * 
	 * @param shell
	 *            the shell that this will be a child of
	 * @param credsCallback
	 *            the callback that will be notified when the user is done
	 * @param port
	 *            the port that the proxy is on
	 * @param host
	 *            the host that the proxy is on
	 * @param authScheme
	 *            the authentication scheme
	 */
	public ProxyDialogue(Shell shell, AuthScheme authScheme, String host,
			int port, CredsCallback credsCallback) {
		this.shell = shell;
		this.host = host;
		this.port = port;
		this.credsCallback = credsCallback;
	}

	/**
	 * Displays the dialogue
	 */
	public void open() {
		dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		GridLayout dialogLayout = new GridLayout(2, false);
		dialog.setLayout(dialogLayout);

		// First is the text saying what this is about
		new Composite(dialog, SWT.NONE); // spacer
		Label lbl = new Label(dialog, SWT.WRAP);
		lbl.setText("Log in to the proxy at " + this.host + ":" + this.port);

		// Second is the username box
		Label usernameLabel = new Label(dialog, SWT.NONE);
		usernameLabel.setText("Username:");
		usernameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		usernameField = new Text(dialog, SWT.BORDER);
		usernameField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
				false));
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR)
					usernameField.traverse(SWT.TRAVERSE_TAB_NEXT);
				else if (e.character == SWT.ESC)
					cancelClicked();
			}
		});

		// Third is the password box
		Label passwordLabel = new Label(dialog, SWT.NONE);
		passwordLabel.setText("Password:");
		passwordLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		passwordField = new Text(dialog, SWT.BORDER | SWT.PASSWORD);
		passwordField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
				false));
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR)
					okClicked();
				else if (e.character == SWT.ESC)
					cancelClicked();
			}
		});

		// Fourth is the OK/Cancel buttons, in their own composite
		Composite buttons = new Composite(dialog, SWT.NONE);
		GridData buttonsLayoutData = new GridData(SWT.END, SWT.NONE, false,
				false);
		buttons.setLayoutData(buttonsLayoutData);

		GridLayout buttonsLayout = new GridLayout(2, true);
		buttons.setLayout(buttonsLayout);
		buttonsLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
		buttonsLayoutData.horizontalSpan = 2;
		buttons.setLayoutData(buttonsLayoutData);

		Button okButton = new Button(buttons, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				okClicked();
			}
		});

		Button cancelButton = new Button(buttons, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				cancelClicked();
			}
		});
		Point preferredSize = cancelButton.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, false);
		GridData buttonData = new GridData(SWT.END, SWT.CENTER, false, false);
		buttonData.widthHint = preferredSize.x;
		buttonData.heightHint = preferredSize.y;
		okButton.setLayoutData(buttonData);
		cancelButton.setLayoutData(buttonData);
		// new Composite(dialog, SWT.NONE);
		dialog.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				cancelClicked();
			}
		});
		dialog.pack();
		dialog.setSize(400, dialog.getSize().y);
		dialog.layout();
		dialog.open();
	}

	private void okClicked() {
		if (isClosing)
			return;
		isClosing = true;
		credsCallback.setUsernamePassword(usernameField.getText(),
				passwordField.getText());
		dialog.close();
	}

	private void cancelClicked() {
		// Prevent infinite loop with the close signal
		if (isClosing)
			return;
		isClosing = true;
		credsCallback.userCancelled();
		dialog.close();
	}

}
