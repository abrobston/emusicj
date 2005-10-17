package nz.net.kallisti.emusicj.metafiles;

import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloader;

/**
 * <p>The interface for metafile handlers</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public interface IMetafile {
    
    public List<IDownloader> getDownloaders();

}
