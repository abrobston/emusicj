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
package nz.net.kallisti.emusicj.metafiles.streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>This stream decrypts an eMusic data file.</p>
 * <p>The algorithm for this was ported directly from the 
 * <a href="http://frumppyoldwoman.com/emusicdlm/decrypt-emp.tar.gz">decrypt-emp</a>
 * script.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class EMPDecoderStream extends InputStream {
	
	private InputStream input;
	
	// This is the key for decrypting files.
	private int[] key = {
			0x6b, 0xd8, 0x44, 0x87, 0x52, 0x94, 0xfd, 0x6e,
			0x2c, 0x18, 0xe4, 0xc8, 0xde, 0x0b, 0xfa, 0x6d,
			0xb5, 0x06, 0x7b, 0xce, 0x77, 0xf4, 0x67, 0x3f,
			0x93, 0x09, 0x1c, 0x20, 0xf5, 0xbe, 0x27, 0xb1,
			0x02, 0xc9, 0x8f, 0x37, 0x68, 0x5e, 0xc1, 0x91,
			0xb4, 0x57, 0x8d, 0x90, 0x55, 0x8e, 0x45, 0x19,
			0xdb, 0x9c, 0xec, 0xa3, 0x9d, 0x32, 0xf7, 0x81,
			0xc5, 0x61, 0x8b, 0xab, 0x30, 0xa0, 0xbc, 0x31,
			0xdf, 0xf3, 0x4b, 0xa9, 0x2f, 0x3a, 0x4a, 0xbf,
			0x08, 0x66, 0xa7, 0xe2, 0x62, 0x3d, 0x36, 0xb2,
			0x4f, 0x73, 0x6c, 0x9a, 0x56, 0xcf, 0x33, 0xe5,
			0x43, 0x10, 0x17, 0xc2, 0x3e, 0x1e, 0x2b, 0x70,
			0x04, 0x7e, 0xc0, 0x9e, 0xc6, 0x4c, 0x92, 0x5c,
			0x0f, 0x23, 0x35, 0xd2, 0x7a, 0x3b, 0xaf, 0x80,
			0xd6, 0x9f, 0x0e, 0x78, 0x63, 0x76, 0x95, 0x58,
			0x1d, 0x83, 0x22, 0x4d, 0x96, 0xda, 0xc4, 0xae,
			0xca, 0xcb, 0xed, 0xd9, 0x86, 0x98, 0xea, 0xef,
			0xc3, 0xd0, 0x00, 0xba, 0x71, 0x46, 0xa8, 0x42,
			0x72, 0x2a, 0xd1, 0x49, 0xe8, 0xd3, 0xc7, 0xd5,
			0x50, 0xcc, 0x47, 0x21, 0xd7, 0x60, 0x38, 0x3c,
			0xe7, 0xd4, 0x89, 0xb6, 0x8a, 0x0c, 0xb8, 0xac,
			0x0d, 0x82, 0x29, 0x05, 0xe6, 0x5f, 0xfc, 0x5a,
			0x12, 0x74, 0x5d, 0x8c, 0x14, 0x03, 0x2d, 0x59,
			0x6f, 0xdc, 0x28, 0x7c, 0x15, 0xad, 0xa2, 0x26,
			0x11, 0x9b, 0x99, 0x24, 0xfb, 0xf8, 0xa4, 0x07,
			0x7d, 0x64, 0x75, 0x1b, 0xcd, 0xa5, 0x25, 0xfe,
			0xb7, 0xb9, 0xff, 0x5b, 0xb0, 0xe0, 0x13, 0x51,
			0x65, 0x4e, 0xbb, 0xf1, 0xeb, 0x48, 0x39, 0x53,
			0xf0, 0xe9, 0x85, 0xf2, 0x69, 0x0a, 0xaa, 0x34,
			0x84, 0x40, 0x41, 0x54, 0xdd, 0xf6, 0x1f, 0xbd,
			0xa1, 0xe1, 0x1a, 0xe3, 0x01, 0x97, 0x88, 0xa6,
			0xf9, 0x2e, 0x16, 0xb3, 0x6a, 0xee, 0x79, 0x7f       
	};
	
	private byte[] decBuffer = null;
	private int ptr = 0;

	private int bufferLength;
	
	/**
	 * Creates an instance of the stream. Needs to be supplied with an 
	 * InputStream that actually reads the data. 
	 * @param in the stream that provides the encrypted data
	 */
	public EMPDecoderStream(InputStream in) {
		super();
		this.input = in;
	}

    /**
     * Provides a decrypted byte of the file
     */
    @Override
    public int read() throws IOException {    
    		// On the first read, go and decode everything
    		// We use a ByteArrayOutputStream to move everything into a buffer
    		ByteArrayOutputStream bs = new ByteArrayOutputStream();
    		byte[] buff = new byte[1024];
    		int count = 0;
    		while ((count = input.read(buff)) != -1) {
    			bs.write(buff, 0, count);
    		}
    		byte[] encoded = bs.toByteArray();
    		// Now fix the broken Base64 in the file
    		for (int i=0; i<encoded.length; i++) {
    			switch (encoded[i]) {
    			case '.': { encoded[i] = '+'; break; }
    			case '_': { encoded[i] = '/'; break; }
    			case '-': { encoded[i] = '='; break; }            
    			}
    		}
    		byte[] ciphertext;
    		try {
    			ciphertext = Base64.decodeBase64(encoded);
    		} catch (ArrayIndexOutOfBoundsException e) {
    			// Thrown if the decoder gets confused.
    			throw new IOException("Decoding of file stream failed");
    		}
    		// Now do the decryption
    		int carry = 0;
    		for (int keyIdx = 1 ; keyIdx < ciphertext.length; ++keyIdx) {
    			int k1 = key[keyIdx & 0xFF];
    			
    			// update carryover
    			carry += k1;
    			carry &= 0xFF;
    			
    			int k2 = key[carry] ;
    			
    			// exchange key bytes
    			key[keyIdx & 0xFF] = k2 ;
    			key[carry] = k1 ;
    			
    			ciphertext[keyIdx - 1] ^= key[(k1 + k2) & 0xFF] ;
    			decBuffer = ciphertext;
    			bufferLength = decBuffer.length;
    			// For some reason these files seem to have a trailing character, so
    			// we'll go back one step (there seems to always be at least one), then
    			// we'll walk backwards until we see a '>' and set that to the end
    			// of the file
    			bufferLength--;
    			while (bufferLength > 0 && decBuffer[bufferLength-1] != '>')
    				bufferLength--;
    		}
    		if (ptr >= bufferLength)
    			return -1;
    		return decBuffer[ptr++];    
    }

    //TODO make a more efficient version of the above for blocks of bytes

}
