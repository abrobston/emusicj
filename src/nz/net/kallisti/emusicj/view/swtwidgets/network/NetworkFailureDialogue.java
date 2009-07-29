package nz.net.kallisti.emusicj.view.swtwidgets.network;

import nz.net.kallisti.emusicj.misc.NumberUtils;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.view.SWTView;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.SelectionAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
	private final SWTView view;
	private static boolean showing = false;
	private final IImageFactory images;

	/**
	 * Instantiate the dialogue. Use {@link #open()} to show it
	 * 
	 * @param shell
	 *            the SWT shell to create the dialogue in
	 */
	public NetworkFailureDialogue(Shell shell, IStrings strings, SWTView view,
			IImageFactory images) {
		this.shell = shell;
		this.strings = strings;
		this.view = view;
		this.images = images;
	}

	/**
	 * Displays the dialogue
	 */
	public void open() {
		if (showing)
			return;
		showing = true;
		dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(1, false));
		dialog.setText("Network Problem");
		StyledText text = new StyledText(dialog, SWT.WRAP);
		String failureMsg = strings.networkFailureMessage();
		text.setText(failureMsg);
		final int offset = failureMsg.indexOf('\uFFFC');
		if (offset != -1) {
			StyleRange style = new StyleRange();
			style.start = offset;
			style.length = 1;
			final Image icon = images.getStartSmallIcon();
			Rectangle rect = icon.getBounds();
			style.metrics = new GlyphMetrics(rect.height, 0, rect.width);
			text.setStyleRange(style);
			text.addPaintObjectListener(new PaintObjectListener() {
				public void paintObject(PaintObjectEvent event) {
					GC gc = event.gc;
					StyleRange style = event.style;
					int start = style.start;
					if (start == offset) {
						int x = event.x;
						int y = event.y + event.ascent - style.metrics.ascent;
						gc.drawImage(icon, x, y);
					}
				}
			});

		}
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false,
				1, 1));

		Composite buttons = new Composite(dialog, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout(2, true);
		buttons.setLayout(buttonsLayout);
		GridData buttonsLayoutData = new GridData(SWT.END, SWT.CENTER, false,
				false);
		buttons.setLayoutData(buttonsLayoutData);

		Button quitBtn = getQuitButton(buttons);
		Button doNothingBtn = getDoNothingButton(buttons);
		GridData buttonData = new GridData(SWT.END, SWT.CENTER, false, false);
		buttonData.widthHint = NumberUtils.max(quitBtn.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, false).x, doNothingBtn.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, false).x);
		buttonData.heightHint = NumberUtils.max(quitBtn.computeSize(
				SWT.DEFAULT, SWT.DEFAULT, false).y, doNothingBtn.computeSize(
				SWT.DEFAULT, SWT.DEFAULT, false).y);
		quitBtn.setLayoutData(buttonData);
		doNothingBtn.setLayoutData(buttonData);
		dialog.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				super.shellClosed(e);
				showing = false;
			}
		});
		dialog.pack();
		dialog.setSize(dialog.computeSize(400, SWT.DEFAULT));
		dialog.layout();
		dialog.open();
	}

	private Button getQuitButton(Composite parent) {
		Button btn = new Button(parent, SWT.PUSH);
		btn.setText("Quit Download Manager");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				quitProgram();
			}
		});
		return btn;
	}

	private Button getDoNothingButton(Composite parent) {
		Button btn = new Button(parent, SWT.PUSH);
		btn.setText("Dismiss");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void action(SelectionEvent ev) {
				doNothing();
			}
		});
		return btn;
	}

	private void quitProgram() {
		dialog.close();
		view.quitProgram();
	}

	private void doNothing() {
		dialog.close();
	}

}
