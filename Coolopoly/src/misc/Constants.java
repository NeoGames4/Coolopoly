
package misc;

import java.util.HashMap;

import game.properties.Properties;

public class Constants {
	
	// TECHNICAL
	public static final String	GAME_TITLE		= "Coolopoly",
								GAME_VERSION	= "1.0.0",
								GAME_AUTHOR		= "Mika Thein",
								GAME_LICENSE	= "TBD",		// TODO
								GAME_DISCLAIMER	= "The sound effects were taken from freesound.org and other royalty-free providers.",
								GAME_GITHUB		= "https://github.com/NeoGames4/Coolopoly/";
	
	public static final int		FPS = 30;
	
	public static final long	LOGIN_TIMEOUT	= 10000;
	
	public static final float	MAX_CAMERA_ZOOM = 2.5f,
								MIN_CAMERA_ZOOM = -0.5f;
	
	public final static float	FREE_CAMERA_MOVEMENT_SPEED = 200.0f,
								FREE_CAMERA_ROTATION_SPEED = 60.0f,
								FREE_CAMERA_ZOOM_SPEED = 1.0f;
	
	public static final boolean DEBUG = true;
	
	// BOARD
	public static final int		BOARD_FIELDS_AMOUNT = Properties.getProperties().length;
	
	public static final float	BOARD_PADDING_SIZE_RATIO = 20/3000f,
								BOARD_CORNER_FIELD_SIZE_RATIO = 410/3000f,
								BOARD_FIELD_FOCUS_ZOOM = 0.6f;
	
	public static final String	BOARD_HIGHLIGHTING_PROPERTIES = "properties";
	
	// GAME
	public static final String	MONEY_UNIT = "MB";
	
	private static final HashMap<String, String> CARD_NAMES;
	static {
		CARD_NAMES = new HashMap<>();
		CARD_NAMES.put("community", "Git Issue");
		CARD_NAMES.put("chance", "Stack Overflow");
	}
	
	public static String getCardName(String type) {
		return CARD_NAMES.get(type);
	}
	
	// NETWORKING
	public static final int		INIT_SERVER_PORT = 1337;
	
	public static final String	INIT_SERVER_ADDRESS = "localhost";
	
	public static final String	MESSAGE_INTENT_MISC			= "misc",
								MESSAGE_INTENT_LOG_IN		= "log_in",		// First connection
								MESSAGE_INTENT_PROPERTIES	= "properties",	// Load properties
								MESSAGE_INTENT_ACTION		= "action",		// Game action
								MESSAGE_INTENT_GAME_STATE	= "game_state",	// Game state
								MESSAGE_INTENT_VOTE			= "vote",		// Vote for action
								MESSAGE_INTENT_EXCEPTION	= "exception";	// Error message

	public static final String	GAME_ACTION_DICE	= "dice",
								GAME_ACTION_BUY		= "buy",
								GAME_ACTION_NEXT	= "next";
	
	public static final String	GAME_SUBJECT_BUY_PROPERTY	= "property",
								GAME_SUBJECT_BUY_HOUSE		= "buy_house",
								GAME_SUBJECT_SELL_HOUSE		= "sell_house";
	
	public static final String	GAME_VOTE_START		= "start",
								GAME_VOTE_EVALUATE	= "evaluate";

}
