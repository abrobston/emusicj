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
package nz.net.kallisti.emusicj.controller;

/**
 *
 * 
 * $Id$
 *
 * @author robin
 */
public interface IPreferenceChangeListener {

	public enum Pref { DROP_DIR, PROXY_PORT, PROXY_HOST, CHECK_FOR_UPDATES, 
		MIN_DOWNLOADS, FILE_PATTERN, SAVE_PATH };
	
	public void preferenceChanged(Pref pref);
	
}
