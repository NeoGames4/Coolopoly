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

public class Server extends Thread {
	
	private final Game game;
	
	private final ServerSocket server;
	
	private ArrayList<ClientConnection> clients = new ArrayList<>();

	public Server(Game game) throws IOException {
		this.game = game;
		
		Console.log("Booting up...");
		server = new ServerSocket(Constants.SERVER_PORT);
		Console.log("The server is online with " + Constants.SERVER_ADRESS + " and port " + Constants.SERVER_PORT + ".");
		
		start();
	}
	
	@Override
	public void run() {
		Console.log("Looking for " + game.playersLimit + " clients...");
		
		while(!server.isClosed()) {
			try {
				Socket socket = server.accept();
				InputStream input	= socket.getInputStream();
				OutputStream output	= socket.getOutputStream();
			
				Console.log("Connected to " + socket.getInetAddress() + " with port " + socket.getPort() + " (" + (clients.size()+1) + " of " + game.playersLimit + ")!");
			
				ClientConnection handler = new ClientConnection(game, socket, input, output);
				
				handler.start();
				clients.add(handler);
			} catch (IOException e) {
				e.printStackTrace(); // TODO
			}
		}
	}
	
	public boolean removeClient(ClientConnection client) throws IOException {
		Console.log("Removing " + client.getRemoteAddress() + "... (" + (clients.size()-1) + " clients left.)");
		return clients.remove(client);
	}
	
	public ArrayList<ClientConnection> getClients() {
		return new ArrayList<>(clients);
	}
	
	public void sendGameState() {
		for(ClientConnection c : clients)
			c.sendGameState(true);
		game.state.newNotifications.clear();
	}
	
	public void countUserVotes(String subject) {
		boolean everyoneVoted = true;
		for(ClientConnection c : clients) {
			if(c.isPlayer() && (!c.userVotes.containsKey(subject) || !c.userVotes.get(subject))) {
				everyoneVoted = false;
				break;
			}
		}
		
		if(everyoneVoted) {
			if(subject.equals(Constants.GAME_VOTE_START) && game.state.isWaiting())
				game.state.start();
			else if(subject.equals(Constants.GAME_VOTE_EVALUATE) && game.state.isRunning())
				game.state.evaluate();
		}
		
		sendGameState();
		
		if(game.state.isOver())
			game.reset();
	}
	
	public void close() throws IOException {
		server.close();
	}

}
