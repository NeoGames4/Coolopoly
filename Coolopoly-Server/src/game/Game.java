package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import launcher.Console;
import networking.ClientConnection;
import networking.Server;

public class Game {
	
	public Server server;
	public GameState state;
	
	public ArrayList<String> playerImages = new ArrayList<>(Arrays.asList("player-computer.png", "player-keyboard.png", "player-laptop.png", "player-mouse.png", "player-pager.png", "player-phone.png", "player-radio.png", "player-tv.png"));
	
	public final int playersLimit;

	public Game(int playersLimit) {
		this.playersLimit = Math.min(playersLimit, playerImages.size());
		
		state = new GameState(new ArrayList<>(), 0);

		try {
			server = new Server(this);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void reset() {
		Console.log("Resetting the game...");
		
		ArrayList<Player> players = state.getPlayers();
		for(int i = 0; i<players.size(); i++) {
			Player p = players.get(i);
			players.set(i, new Player(p.name, p.remoteAddress, p.image));
		}
		
		for(ClientConnection c : server.getClients())
			c.userVotes.clear();
		
		state = new GameState(players, (int) (Math.random() * players.size()));
	}

}
