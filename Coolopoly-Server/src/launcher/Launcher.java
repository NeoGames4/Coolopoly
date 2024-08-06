package launcher;

import java.util.ArrayList;

import game.GameState;
import game.Player;
import networking.ServerCommunicator;

public class Launcher {

	public static void main(String[] args) {
		ArrayList<Player> players = new ArrayList<>();
		GameState game = new GameState(players, 0);
		try {
			ServerCommunicator communicator = new ServerCommunicator();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
