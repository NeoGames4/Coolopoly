package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import networking.ClientHandler;
import networking.ServerCommunicator;

public class Game {
	
	public ServerCommunicator server;
	public GameState state;
	
	private boolean start = true;
	
	private List<String> names = new ArrayList<>(Arrays.asList(Constants.PLAYER_NAMES));

	public Game(int playersAmount) {
		ArrayList<Player> players = new ArrayList<>();
		
		try {
			server = new ServerCommunicator(this);
			server.acceptClients(playersAmount);
			
			for(ClientHandler c : server.getClients()) {
				int i = (int) (Math.random() * names.size());
				players.add(new Player(names.get(i), c.getRemoteAdress()));
				names.remove(i);
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		while(!start);
		
		state = new GameState(players, 0);
	}
	
	public void start() {
		start = true;
	}

}
