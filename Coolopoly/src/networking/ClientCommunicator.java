package networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import misc.Constants;

public class ClientCommunicator {
	
	private Socket socket;
	
	private InputStream input;
	private OutputStream output;
	
	private ServerHandler handler;

	public ClientCommunicator() throws UnknownHostException, IOException {
		connect();
	}
	
	public ClientCommunicator connect() throws UnknownHostException, IOException {
		if(!isConnected()) {
			socket = new Socket(Constants.SERVER_ADRESS, Constants.SERVER_PORT);
			input	= socket.getInputStream();
			output	= socket.getOutputStream();
			
			handler = new ServerHandler(socket, input, output);
			handler.start();
		} else throw new RuntimeException("This client is already connected. Please run disconnect() first.");
		return this;
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	public void disconnect() throws IOException {
		if(isConnected()) {
			handler.interrupt();
			socket.close();
			socket = null;
			handler = null;
		}
	}
	
	public ServerHandler getServerHandler() {
		return handler;
	}

}
