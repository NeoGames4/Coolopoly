package game;

import org.json.JSONObject;

public class GameIllegalMoveException extends Exception {

	private static final long serialVersionUID = 1L;

	public GameIllegalMoveException(String message) {
		super(message);
	}
	
	public JSONObject asJSON() {
		return new JSONObject()
				.put("message", this.getMessage());
	}

}
