package game;

import org.json.JSONObject;

public class GameIllegalMoveException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public final String causingIntent;

	public GameIllegalMoveException(String message, String causingIntent) {
		super(message);
		this.causingIntent = causingIntent;
	}
	
	public JSONObject asJSON() {
		JSONObject o = new JSONObject().put("message", this.getMessage());
		if(causingIntent != null)
			o.put("intent", causingIntent);
		return o;
	}

}
