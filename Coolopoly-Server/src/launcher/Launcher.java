package launcher;

import game.Constants;
import game.Game;

public class Launcher {

	public static void main(String[] args) {
		int players = 0;
		
		try {
			players = Integer.parseInt(args[0]);
			if(players < 1 || players > Constants.PLAYER_NAMES.length)
				throw new Exception();
		} catch(Exception e) {
			Console.err("Please pass a valid amount of players 0 < p < " + (Constants.PLAYER_NAMES.length+1) + " at the first argument.");
			System.exit(0);
		}
		
		try {
			new Game(players);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
