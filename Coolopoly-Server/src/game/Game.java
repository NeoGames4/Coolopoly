package game;

import java.io.IOException;
import java.util.ArrayList;

import networking.Server;

public class Game {
	
	public Server server;
	public GameState state;
	
	private boolean isRunning = true;

	public Game(int playersAmount) {
		ArrayList<Player> players = new ArrayList<>();
		state = new GameState(players, 0);
		
		try {
			server = new Server(this, playersAmount);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(0);
		}		
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void start() {
		isRunning = true;
	}

}
