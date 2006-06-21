/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat

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

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.view.SWTView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Displays some information about the program</p>
 * 
 * <p>$Id$</p>
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
		dialog.setLayout (new GridLayout(2,false));
		dialog.setText("About "+Constants.APPNAME);
		
		/* left bit is where the logo goes */
		Composite leftBit = new Composite(dialog, SWT.NONE);
		leftBit.setLayout(new GridLayout(1, false));
		final Image aboutLogoImg = new Image(SWTView.getDisplay(), 
				AboutDialogue.class.getResourceAsStream("emusicj-about.png"));
		Label logoLabel = new Label(leftBit, SWT.NONE);
		logoLabel.setImage(aboutLogoImg);
		
		/* right bit is for the text stuff */
		Composite rightBit = new Composite(dialog, SWT.NONE);
		rightBit.setLayout(new GridLayout(1, false));
 		rightBit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
 		
		Label version = new Label(rightBit, SWT.NONE);
		version.setText(Constants.APPNAME+" v"+Constants.VERSION);
		/* This magic makes the text field bold */
		Font initialFont = version.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(SWT.BOLD);
		}
		Font newFont = new Font(SWTView.getDisplay(), fontData);
		version.setFont(newFont);
		
		Text textField = new Text(rightBit, SWT.MULTI | SWT.WRAP | 
				SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
		textField.setText(Constants.ABOUT_BOX_TEXT);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Button closeButton = new Button(dialog, SWT.PUSH);
		closeButton.setText("Close");
		GridData gd = new GridData();
		gd.horizontalAlignment=SWT.RIGHT;
		gd.horizontalSpan=2;
		closeButton.setLayoutData(gd);
		closeButton.addSelectionListener(new SelectionAdapter(){
			public void action(SelectionEvent e) {
				close();
			}
		});
		leftBit.layout();
		rightBit.layout();
		dialog.pack();
		dialog.setSize(500,300);
		dialog.layout();
		dialog.open();
	}
	
	public void close() {
		dialog.dispose();
	}

}
