package nz.net.kallisti.emusicj.controller;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IPreferenceChangeListener {

	public enum Pref { DROP_DIR, PROXY_PORT, PROXY_HOST, CHECK_FOR_UPDATES, 
		MIN_DOWNLOADS, FILE_PATTERN, SAVE_PATH };
	
	public void preferenceChanged(Pref pref);
	
}
