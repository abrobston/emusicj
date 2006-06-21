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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * <p>Handles a status line, which is basically a single label that
 * can be put somewhere. Done in a class on it's own to allow increasing its
 * functionality should that been needed.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class StatusLine extends Composite {

    private Label label;

    /**
     * @param parent
     * @param style
     */
    public StatusLine(Composite parent, int style) {
        super(parent, style);
        label = new Label(this, SWT.NONE);
        unsetText();
    }
    
    public void setText(String text) {
        label.setText(text);
        label.pack();
        pack();
    }
    
    public void unsetText() {
        label.setText("");
        label.pack();
    }

}
