package networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import game.Constants;
import launcher.Console;

public class ServerCommunicator {
	
	private final ServerSocket server;
	
	private ArrayList<ClientHandler> clients = new ArrayList<>();

	public ServerCommunicator() throws IOException {
		Console.log("Booting up...");
		server = new ServerSocket(Constants.SERVER_PORT);
		Console.log("The server is online with " + Constants.SERVER_ADRESS + " and port " + Constants.SERVER_PORT + ".");
		acceptClients();
	}
	
	public void acceptClients() throws IOException {
		Console.log("Looking for clients...");
		while(true) {
			Socket socket = server.accept();
			InputStream input	= socket.getInputStream();
			OutputStream output	= socket.getOutputStream();
			
			Console.log("Connected to " + socket.getInetAddress() + " with port " + socket.getPort() + "!");
			
			ClientHandler handler = new ClientHandler(socket, input, output);
			handler.run();
			clients.add(handler);
		}
	}
	
	public void close() throws IOException {
		server.close();
	}

}
