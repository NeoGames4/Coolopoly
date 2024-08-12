package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import networking.ClientConnection;
import networking.Server;

public class Game {
	
	public Server server;
	public GameState state;
	
	private boolean start = true;
	
	private List<String> freeNames = new ArrayList<>(Arrays.asList(Constants.PLAYER_NAMES));

	public Game(int playersAmount) {
		ArrayList<Player> players = new ArrayList<>();
		
		try {
			server = new Server(this, playersAmount);
			
			for(ClientConnection c : server.getClients()) {
				int i = (int) (Math.random() * freeNames.size());
				players.add(new Player(freeNames.get(i), c.getRemoteAdress()));
				freeNames.remove(i);
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
