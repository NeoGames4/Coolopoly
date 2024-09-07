package game.properties;

import org.json.JSONArray;
import org.json.JSONObject;

import game.GameIllegalMoveException;
import game.GameState;
import game.Notification;

public class GoToEffect implements PropertyEffect {
	
	public static final String ACTION = "go_to";
	
	public final int field;
	private String[] messages = {""};

	public GoToEffect(int field, String[] messages) {
		this.field = field;
		if(messages != null && messages.length > 0)
			this.messages = messages;
	}
	
	public int messagesLength() {
		return messages.length;
	}
	
	public String getMessage(int i) {
		return messages[i %= messages.length].replace("&f", field + " ");
	}

	@Override
	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException {
		gameState.currentPlayer().setPosition(field);
		gameState.newNotifications.add(new Notification(gameState.currentPlayer().name + " was sent to " + gameState.getProperty(gameState.currentPlayer().getPosition()).title + "."));
		return true;
	}

	public static GoToEffect fromJSON(JSONObject o) {
		String[] messages;
		if(o.has("messages")) {
			JSONArray jsonMessages = o.getJSONArray("messages");
			messages = new String[jsonMessages.length()];
		
			for(int i = 0; i<messages.length; i++)
				messages[i] = jsonMessages.getString(i);
		} else
			messages = null;
		
		return new GoToEffect(o.getInt("field"), messages);
	}

}
