package game;

import org.json.JSONObject;

public class Notification {
	
	public final String message;
	public final long displayDuration;
	
	public Notification(String message) {
		this(message, Constants.DEFAULT_NOTIFICATION_DURATION);
	}

	public Notification(String message, long displayDuration) {
		this.message = message;
		this.displayDuration = displayDuration;
	}
	
	public JSONObject toJSON() {
		return new JSONObject()
				.put("message", message)
				.put("duration", displayDuration);
	}

}
