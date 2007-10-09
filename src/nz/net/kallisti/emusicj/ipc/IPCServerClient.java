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
package nz.net.kallisti.emusicj.ipc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import nz.net.kallisti.emusicj.controller.Preferences;

/**
 * <p>This is a simple server/client (in one class for easiness) that
 * tries to form a connection to another instance of this server over a 
 * network socket. If it can't, it turns into a listening server instead.</p>
 * <p>To work out the port to listen to, it uses a file called 'port' in the
 * stateDir (defined in {@link Preferences}). If that file doesn't exist, 
 * or there is no response on the port, then the server is started and that
 * file is created.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class IPCServerClient {

	public static final int CONNECTED = 1;
	public static final int FAILED = 2;
	public static final int LISTENING = 3;
	private IIPCListener listener;
	private Socket connection;
	private int state;
	private ServerThread serverThread;
	private File portFile; 

	/**
	 * @param listener
	 */
	public IPCServerClient(IIPCListener listener, File portFile) {
		this.listener = listener;
		this.portFile = portFile;
		if (!portFile.exists()) {		
			startServer();
			return;
		}
		try {
			BufferedReader in = new BufferedReader(new FileReader(portFile));
			String port = in.readLine();
            try { in.close(); } catch (IOException e) {}
			String loopback = null;
			connection = new Socket(loopback,Integer.parseInt(port));
			Thread.sleep(500);
			if (!connection.isConnected()) {
				connection.close();
				startServer();
				return;
			}
		} catch (Exception e) {
			startServer();            
			return;
		}
		state = CONNECTED;
	}
	
	/**
	 * If you don't want to start the server, only send data, then use this
	 * static method.
	 * @param portFile the file that contains the port number to use.
	 * @param data the data to be sent
	 * @throws IOException if a connection can't be made, either the file is
	 * unreadable or a connection to the other end can't be made.
	 */
	public static void sendOnly(File portFile, String[] data) throws IOException {
		// Form the connection
		BufferedReader in = new BufferedReader(new FileReader(portFile));
		String port = in.readLine();
        try { in.close(); } catch (IOException e) {}
		String loopback = null;
		Socket connection = new Socket(loopback,Integer.parseInt(port));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// Mostly ignore this
			e.printStackTrace();
		}
		if (!connection.isConnected()) {
			connection.close();
			throw new IOException("Unable to form a stable connection to the client");
		}
		// Send the data
		OutputStream out;
		out = connection.getOutputStream();
		for (String d : data) {
			out.write(d.getBytes());
			out.write('\r');
		}
		out.close();
		connection.close();
	}
	
	public void startServer() {
		ServerSocket socket = null;
		// find a free port
		boolean foundPort = false;
		int port=-1;
		Exception err=null;
		for (port=20000; port <=25000; port+=7) {
			try {
				socket = new ServerSocket(port,0, 
						InetAddress.getByName("127.0.0.1"));
			} catch (IOException e) {
				err = e;
				continue;
			}
			foundPort = true;
			break;
		}
		if (!foundPort || socket == null) {
			state = FAILED;
			System.err.println("Failed to start the server for listening");
			if (err != null)
				err.printStackTrace();
			return;
		}
		serverThread = new ServerThread(socket);
		serverThread.start();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(portFile));
			out.write(port+"\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			state = FAILED;
		}		
		state = LISTENING;
	}

	public int getState() {
		return state;
	}
	
	public void stopServer() {
		if (serverThread != null)
			serverThread.finish();
		portFile.delete();
	}
	
	public void sendData(String[] data) {
		if (state != CONNECTED)
			return;
		if (connection == null || connection.isClosed())
			return;
		OutputStream out;
		try {
			out = connection.getOutputStream();
			for (String d : data) {
				out.write(d.getBytes());
				out.write('\r');
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}		
	}
	
	/**
	 * <p>This thread monitors the connection, and if a connection occurrs,
	 * reads the lines from it and passes them to the controller</p> 
	 * 
	 * @author robin
	 */
	public class ServerThread extends Thread {

		private ServerSocket ssocket;
		private boolean done = false;
		
		/**
		 * @param socket
		 */
		public ServerThread(ServerSocket ssocket) {
			this.ssocket = ssocket;
		}
		
		public void run() {
			setName("Server Thread");
			try {
				ssocket.setSoTimeout(5000);
				while (!done) {
					try {
						Socket s = ssocket.accept();
						BufferedInputStream in = 
							new BufferedInputStream(s.getInputStream());
						boolean eof = false;
						ArrayList<String> lines = new ArrayList<String>();
						while (!eof) {
							StringBuffer line = new StringBuffer();
							int c = in.read();
							while (c != -1 && c != '\n' && c != '\r') {
								line.append((char)c);
								c = in.read();
							}
							if (c == -1) eof = true;
							if (line.length() != 0)
								lines.add(line.toString());
						}
						in.close();
						listener.ipcData(lines.toArray(new String[] {}));
					} catch (InterruptedIOException e) {}
				}
			} catch (IOException e) {
				state = FAILED;
				e.printStackTrace();
			}
		}
		
		public void finish() {
			done = true;
			this.interrupt();
		}

	}

	
}
