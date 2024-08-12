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

public class Server {
	
	private final ServerSocket server;
	
	public final int clientsLimit;
	private ArrayList<ClientConnection> clients = new ArrayList<>();

	public Server(Game game, int clientsLimit) throws IOException {
		this.clientsLimit = clientsLimit;
		
		Console.log("Booting up...");
		server = new ServerSocket(Constants.SERVER_PORT);
		Console.log("The server is online with " + Constants.SERVER_ADRESS + " and port " + Constants.SERVER_PORT + ".");
		
		Console.log("Looking for " + clientsLimit + " clients...");
		
		while(true) {
			if(clients.size() < clientsLimit) {
				Socket socket = server.accept();
				InputStream input	= socket.getInputStream();
				OutputStream output	= socket.getOutputStream();
			
				Console.log("Connected to " + socket.getInetAddress() + " with port " + socket.getPort() + " (" + (clients.size()+1) + " of " + clientsLimit + ")!");
			
				ClientConnection handler = new ClientConnection(game, socket, input, output);
				handler.start();
				clients.add(handler);
			} try {
				Thread.sleep(10);
			} catch (InterruptedException e) { }
		}
	}
	
	public boolean leaveClient(ClientConnection client) throws IOException {
		Console.log("Leaving " + client.getRemoteAdress() + " (" + (clients.size()-1) + " left).");
		client.disconnect();
		return clients.remove(client);
	}
	
	public ArrayList<ClientConnection> getClients() {
		return new ArrayList<>(clients);
	}
	
	public void close() throws IOException {
		server.close();
	}

}
