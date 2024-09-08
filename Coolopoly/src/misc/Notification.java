package misc;

import org.json.JSONObject;

import game.Transition;
import misc.design.Design;

public class Notification {
	
	public final String message;
	public final long timestamp;
	
	public final long displayDuration;
	
	public Transition aTransition;
	public Transition yTransition;

	public Notification(String message, long timestamp, long displayDuration) {
		this.message = message;
		this.timestamp = timestamp;
		this.displayDuration = displayDuration;
		aTransition = new Transition(0, 1, timestamp, timestamp+Design.INFO_PANEL_NOTIFICATIONS_FADE_DURATION);
		yTransition = new Transition(0, 0, 10);
	}
	
	public static Notification fromJSON(JSONObject o) {
		return new Notification(
			o.getString("message"),
			System.currentTimeMillis(),
			o.getLong("duration")
		);
	}

}
