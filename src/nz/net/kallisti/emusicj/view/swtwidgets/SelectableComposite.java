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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.net.kallisti.emusicj.view.swtwidgets.selection.ISelectableControl;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.ISelectionEvent;
import nz.net.kallisti.emusicj.view.swtwidgets.selection.ISelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * This is a Composite that allows it's children to be selected.
 * 
 * $Id$
 * 
 * @author robin
 */
public class SelectableComposite extends Composite implements
		ISelectionListener {

	private ISelectableControl lastSelected;
	private final List<ISelectableControl> selected = new ArrayList<ISelectableControl>();
	/**
	 * This tracks the last clicked or control-clicked item, this is used when
	 * working out the range to select with shift-clicking
	 */
	private ISelectableControl markSelected;
	/**
	 * When shift-clicking, this records the last one of a range that was
	 * selected. If another shift-click occurs, then this is used to deselect
	 * previously selected ones.
	 */
	private ISelectableControl markSelected2;
	private final List<ISelectableControl> controls = Collections
			.synchronizedList(new ArrayList<ISelectableControl>());

	public SelectableComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void addSelectableControl(ISelectableControl c) {
		c.addSelectionListener(this);
		controls.add(c);
	}

	public void removeSelectableControl(ISelectableControl c) {
		c.removeSelectionListener(this);
		if (c == lastSelected || selected.contains(c)) {
			selected.remove(c);
			if (lastSelected == c) {
				if (selected.size() != 0) {
					lastSelected = selected.get(selected.size() - 1);
				} else {
					lastSelected = null;
				}
				markSelected = null;
				markSelected2 = null;
			}
			notifyListeners(SWT.Selection, new Event());
		}
	}

	public void widgetSelected(ISelectionEvent e) {
		Object o = e.getSource();
		if (o instanceof ISelectableControl) {
			synchronized (selected) {
				ISelectableControl src = (ISelectableControl) o;
				if ((SWT.SHIFT & e.getKeyboardState()) != 0) {
					// select the range between markSelected and the one
					// just clicked, unless markSelected is null, in which case
					// it's like a regular click except never clearing
					if (markSelected == null) {
						if (!selected.contains(src))
							selected.add(src);
						markSelected = src;
						lastSelected = src;
						markSelected2 = null;
						src.select();
					} else {
						if (markSelected != null
								&& ((Widget) markSelected).isDisposed())
							markSelected = null;
						if (markSelected2 != null
								&& ((Widget) markSelected2).isDisposed())
							markSelected2 = null;

						// We're either creating a new range, or changing the
						// current one
						if (markSelected2 == null) {
							// find the controls that are between the clicked
							// one
							// and the last marked one
							int pos1 = controls.indexOf(src);
							int pos2 = controls.indexOf(markSelected);
							if (pos1 > pos2) {
								int t = pos1;
								pos1 = pos2;
								pos2 = t;
							}
							for (int i = pos1; i <= pos2; i++) {
								ISelectableControl cont = controls.get(i);
								if (!selected.contains(cont)) {
									cont.select();
									selected.add(cont);
								}
							}
							markSelected2 = src;
						} else {
							// We're altering a range
							// First, deselect the range (this should be
							// optimised to just do the correct set. Later)
							int pos1 = controls.indexOf(markSelected);
							int pos2 = controls.indexOf(markSelected2);
							if (pos1 > pos2) {
								int t = pos1;
								pos1 = pos2;
								pos2 = t;
							}
							for (int i = pos1; i <= pos2; i++) {
								ISelectableControl cont = controls.get(i);
								cont.unselect();
								selected.remove(cont);
							}
							// Now select the new range
							pos1 = controls.indexOf(markSelected);
							pos2 = controls.indexOf(src);
							if (pos1 > pos2) {
								int t = pos1;
								pos1 = pos2;
								pos2 = t;
							}
							for (int i = pos1; i <= pos2; i++) {
								ISelectableControl cont = controls.get(i);
								if (!selected.contains(cont)) {
									cont.select();
									selected.add(cont);
								}
							}
							markSelected2 = src;
						}
					}
				} else if ((SWT.CONTROL & e.getKeyboardState()) != 0) {
					if (selected.contains(src)) {
						// Deselect just this one
						src.unselect();
						selected.remove(src);
						if (selected.size() != 0) {
							lastSelected = selected.get(selected.size() - 1);
						} else {
							lastSelected = null;
						}
					} else {
						// add it to the selection
						src.select();
						selected.add(src);
						lastSelected = src;
					}
					markSelected = lastSelected;
					markSelected2 = null;

				} else {
					for (ISelectableControl control : selected) {
						if (control != src)
							control.unselect();
					}
					selected.clear();
					selected.add(src);
					src.select();
					lastSelected = src;
					markSelected = src;
					markSelected2 = null;
				}
				notifyListeners(SWT.Selection, new Event());
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public ISelectableControl getLastSelectedControl() {
		return lastSelected;
	}

	/**
	 * This provides all the selected tracks. Note that order is largely
	 * irrelevant here.
	 * 
	 * @return a list containing the tracks the user has currently selected.
	 *         This list will be zero-length if none are selected.
	 */
	public List<ISelectableControl> getSelected() {
		return Collections.unmodifiableList(selected);
	}

	public void addSelectionListener(SelectionListener listener) {
		addListener(SWT.Selection, new TypedListener(listener));
	}

	public void removeSelectionListener(SelectionListener listener) {
		removeListener(SWT.Selection, listener);
	}

}
