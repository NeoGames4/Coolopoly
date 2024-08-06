package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import launcher.Console;

public class ClientHandler extends Thread {

	private Socket socket;
	
	private BufferedReader input;
	
	private PrintWriter output;

	public ClientHandler(Socket socket, InputStream input, OutputStream output) {
		this.socket = socket;
		this.input = new BufferedReader(new InputStreamReader(input));
		this.output = new PrintWriter(output, true);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				JSONObject o = new JSONObject(input.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void send(String intent, JSONObject content) {
		JSONObject m = new JSONObject()
				.put("intent", intent)
				.put("content", content);
		
		output.println(m.toString());
		Console.log("Sent " + intent + " packet to client " + socket.getLocalAddress() + " with content " + m.toString() + ".");
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
