package game;

public class Constants {

	// GAME
	public static final int STARTING_MONEY = 1500;
	
	// NETWORKING
	public static final int SERVER_PORT = 1337;
	
	public static final String SERVER_ADRESS = "localhost";
	
	public static final String	MESSAGE_INTENT_MISC			= "misc",
								MESSAGE_INTENT_ACTION		= "action",
								MESSAGE_INTENT_GAME_STATE	= "game_state",
								MESSAGE_INTENT_EXCEPTION	= "exception";
	
	public static final String	GAME_ACTION_DICE	= "dice",
								GAME_ACTION_BUY		= "buy",
								GAME_ACTION_NEXT	= "next";
	
	// MISC
	public static final String[] PLAYER_NAMES = {
			"Solo dev", "Indie dev", "Nerd"
	};

}
