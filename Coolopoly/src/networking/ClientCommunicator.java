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

	public ClientCommunicator() {
	}
	
	public ClientCommunicator connect() throws UnknownHostException, IOException {
		if(!isConnected()) {
			socket = new Socket(Constants.SERVER_ADRESS, Constants.SERVER_PORT);
			input	= socket.getInputStream();
			output	= socket.getOutputStream();
			
			try {
				output.write(1);
				Thread.sleep(1000);
				output.write(2);
				Thread.sleep(5000);
				output.write(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else throw new RuntimeException("This client is already connected. Please run disconnect() first.");
		return this;
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	public void disconnect() throws IOException {
		if(isConnected()) {
			socket.close();
			socket = null;
		}
	}

}
