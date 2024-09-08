package game;

public class Constants {

	// GAME
	public static final int STARTING_MONEY = 1500;
	
	public static final String MONEY_UNIT = "MB";
	
	public static final int PRISON_DEFAULT_TURNS	= 3,
							PRISON_FIELD_POSITION	= 10;
	
	// NOTIFICATIONS
	public static final long DEFAULT_NOTIFICATION_DURATION = 3500;
	
	// RESTRICTIONS
	public static final int USERNAME_MAX_LENGTH = 12;
	
	public static final String USERNAME_ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789äöüÄÖÜáàéèíìóòúùÁÀÉÈÍÌÓÒÚÙãõÃÕñÑâêîôûÂÊÎÔÛ-_+*/()&!?.,<>@#$:;^~ ";
	
	// NETWORKING
	public static final int SERVER_PORT = 1337;
	
	public static final String SERVER_ADRESS = "localhost";
	
	public static final String	MESSAGE_INTENT_MISC			= "misc",
								MESSAGE_INTENT_LOG_IN		= "log_in",		// First connection
								MESSAGE_INTENT_PROPERTIES	= "properties",	// Load properties
								MESSAGE_INTENT_ACTION		= "action",		// Game action
								MESSAGE_INTENT_GAME_STATE	= "game_state",	// Game state
								MESSAGE_INTENT_VOTE			= "vote",		// Vote for action
								MESSAGE_INTENT_EXCEPTION	= "exception";	// Error message
	
	public static final String	GAME_ACTION_DICE	= "dice",
								GAME_ACTION_BUY		= "buy",
								GAME_ACTION_MORTGAGE= "mortgage",
								GAME_ACTION_NEXT	= "next";
	
	public static final String	GAME_SUBJECT_BUY_PROPERTY	= "property",
								GAME_SUBJECT_BUY_HOUSE		= "buy_house",
								GAME_SUBJECT_MORTGAGE		= "mortgage",
								GAME_SUBJECT_UNMORTGAGE		= "unmortgage",
								GAME_SUBJECT_SELL_HOUSE		= "sell_house";
	
	public static final String	GAME_VOTE_START		= "start",
								GAME_VOTE_EVALUATE	= "evaluate";
	
	// MISC
	public static final String[] PLAYER_NAMES = {
			"Solo dev", "Indie dev", "Nerd"
	};

}
