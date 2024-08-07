package networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import game.Constants;
import game.Game;
import launcher.Console;

public class ServerCommunicator {
	
	private final Game game;
	
	private final ServerSocket server;
	
	private ArrayList<ClientHandler> clients = new ArrayList<>();

	public ServerCommunicator(Game game) throws IOException {
		this.game = game;
		
		Console.log("Booting up...");
		server = new ServerSocket(Constants.SERVER_PORT);
		Console.log("The server is online with " + Constants.SERVER_ADRESS + " and port " + Constants.SERVER_PORT + ".");
	}
	
	public void acceptClients(int clientsAmount) throws IOException {
		Console.log("Looking for " + clientsAmount + " clients...");
		
		while(clients.size() < clientsAmount) {
			Socket socket = server.accept();
			InputStream input	= socket.getInputStream();
			OutputStream output	= socket.getOutputStream();
			
			Console.log("Connected to " + socket.getInetAddress() + " with port " + socket.getPort() + " (" + (clients.size()+1) + " of " + clientsAmount + ")!");
			
			ClientHandler handler = new ClientHandler(game, socket, input, output);
			handler.start();
			clients.add(handler);
		}
		
		Console.log("Found " + clientsAmount + " clients.");
	}
	
	public ArrayList<ClientHandler> getClients() {
		return clients;
	}
	
	public void close() throws IOException {
		server.close();
	}

}
