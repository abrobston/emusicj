package nz.net.kallisti.emusicj.metafiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.HTTPMusicDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;

/**
 * <p>This is a parser for plaintext metafiles. These metafiles have
 * the format:<br />
 * <tt>URL tracknum songname album artist</tt><br />
 * with any number of lines each containing this information.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class PlainTextMetafile implements IMetafile {

    private File file;
    private IEMusicController controller;

    public PlainTextMetafile(File file, IEMusicController controller) {
        super();
        this.file = file;
        this.controller = controller;
    }

    public List<IMusicDownloader> getMusicDownloaders() {
        ArrayList<IMusicDownloader> downloaders = 
            new ArrayList<IMusicDownloader>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return null;
        }
        boolean done = false;
        while (!done) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e1) {
                return null;
            }
            String[] parts = line.split(" ");
            if (parts.length != 5) {
                return null;
            }
            // Check for the URL being valid
            URL url;
            try {
                url = new URL(parts[0]);
            } catch (MalformedURLException e) {
                return null;
            }
            // check the track number is a valid number
            int tnum;
            try {
                tnum = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return null;
            }
            downloaders.add(new HTTPMusicDownloader(controller, url, 
                    tnum, parts[2], parts[3], parts[4]));
        }
        return downloaders;
    }
    
    @SuppressWarnings("unused")
    public static boolean canParse(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        boolean valid = true;
        boolean done = false;
        while (valid && !done) {
            String line = reader.readLine();
            String[] parts = line.split(" ");
            if (parts.length != 5) {
                valid = false;
                break;
            }
            // Check for the URL being valid
            try {
                URL url = new URL(parts[0]);
            } catch (MalformedURLException e) {
                valid = false;
                break;
            }
            // check the track number is a valid number
            try {
                int tnum = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                valid = false;
                break;
            }
        }
        return valid;
    }

}
