package game;

public class Constants {

	// GAME
	public static final int STARTING_MONEY = 1500;
	
	// RESTRICTIONS
	public static final int USERNAME_MAX_LENGTH = 12;
	
	public static final String USERNAME_ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789äöüÄÖÜáàéèíìóòúùÁÀÉÈÍÌÓÒÚÙãõÃÕñÑâêîôûÂÊÎÔÛ-_+*/()&!?.,<>@#$:;^~ ";
	
	// NETWORKING
	public static final int SERVER_PORT = 1337;
	
	public static final String SERVER_ADRESS = "localhost";
	
	public static final String	MESSAGE_INTENT_MISC			= "misc",
								MESSAGE_INTENT_LOG_IN		= "log_in",		// First connection
								MESSAGE_INTENT_ACTION		= "action",		// Game action
								MESSAGE_INTENT_GAME_STATE	= "game_state",	// Game state
								MESSAGE_INTENT_EXCEPTION	= "exception";	// Error message
	
	public static final String	GAME_ACTION_DICE	= "dice",
								GAME_ACTION_BUY		= "buy",
								GAME_ACTION_NEXT	= "next";
	
	// MISC
	public static final String[] PLAYER_NAMES = {
			"Solo dev", "Indie dev", "Nerd"
	};

}
