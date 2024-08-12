
package misc;

public class Constants {
	
	// TECHNICAL
	public static final String GAME_TITLE = "Coolopoly",
			GAME_VERSION = "0.0.0";
	
	public static final int FPS = 30;
	
	public static final float MAX_CAMERA_ZOOM = 2.5f,
			MIN_CAMERA_ZOOM = -0.5f;
	
	public final static float CAMERA_MOVEMENT_SPEED = 200.0f,
			CAMERA_ROTATION_SPEED = 60.0f,
			CAMERA_ZOOM_SPEED = 1.0f;
	
	public static final boolean DEBUG = true;
	
	// BOARD
	public static final int BOARD_FIELDS_AMOUNT = 40;
	
	public static final float BOARD_FIELDS_WIDTH_FACTOR = 0.2f,
			BOARD_FIELD_FOCUS_ZOOM = 0.6f;
	
	// NETWORKING
	public static final int INIT_SERVER_PORT = 1337;
	
	public static final String INIT_SERVER_ADDRESS = "localhost";
	
	public static final String	MESSAGE_INTENT_MISC			= "misc",
								MESSAGE_INTENT_LOG_IN		= "log_in",		// First connection
								MESSAGE_INTENT_ACTION		= "action",		// Game action
								MESSAGE_INTENT_GAME_STATE	= "game_state",	// Game state
								MESSAGE_INTENT_EXCEPTION	= "exception";	// Error message

	public static final String	GAME_ACTION_DICE	= "dice",
								GAME_ACTION_BUY		= "buy",
								GAME_ACTION_NEXT	= "next";

}
