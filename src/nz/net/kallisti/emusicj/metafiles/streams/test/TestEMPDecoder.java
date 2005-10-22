package nz.net.kallisti.emusicj.metafiles.streams.test;

import java.io.FileInputStream;
import java.io.IOException;

import nz.net.kallisti.emusicj.metafiles.streams.EMPDecoderStream;

/**
 * <p>This tests the EMPDecoderStream class. An encrypted EMP file should be
 * provided on the command line, and a decrypted version is output to STDOUT</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class TestEMPDecoder {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("An EMP file must be given on the command line.");
			System.exit(-1);
		}
		EMPDecoderStream stream = new EMPDecoderStream(new FileInputStream(args[0]));
		StringBuffer res = new StringBuffer();
		byte[] buff = new byte[1024];
		int count=0;
		while ((count = stream.read(buff)) != -1) {
			char[] cbuff = new char[count];
			for (int i=0; i<count; i++)
				cbuff[i] = (char)buff[i];
			res.append(cbuff, 0, count);
		}
		System.out.println(res);
	}

}
