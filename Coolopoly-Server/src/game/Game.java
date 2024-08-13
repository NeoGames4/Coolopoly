package game;

import java.io.IOException;
import java.util.ArrayList;

import networking.Server;

public class Game {
	
	public Server server;
	public GameState state;
	
	public final int playersLimit;

	public Game(int playersLimit) {
		this.playersLimit = playersLimit;
		
		state = new GameState(new ArrayList<>(), 0);

		try {
			server = new Server(this);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
