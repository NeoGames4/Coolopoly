package game.properties;

import org.json.JSONArray;
import org.json.JSONObject;

import game.GameIllegalMoveException;
import game.GameState;
import game.Notification;

public class GoToPrisonEffect implements PropertyEffect {
	
	public static final String ACTION = "go_to_prison";
	
	private String[] messages = {""};	// TODO add message system

	public GoToPrisonEffect(String[] messages) {
		if(messages != null && messages.length > 0)
			this.messages = messages;
	}
	
	public int messagesLength() {
		return messages.length;
	}
	
	public String getMessage(int i) {
		return messages[i %= messages.length];
	}

	@Override
	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException {
		gameState.newNotifications.add(new Notification(gameState.currentPlayer().name + " went into sandbox isolation."));
		gameState.currentPlayer().goToPrison();
		return true;
	}

	public static GoToPrisonEffect fromJSON(JSONObject o) {
		String[] messages;
		if(o.has("messages")) {
			JSONArray jsonMessages = o.getJSONArray("messages");
			messages = new String[jsonMessages.length()];
		
			for(int i = 0; i<messages.length; i++)
				messages[i] = jsonMessages.getString(i);
		} else
			messages = null;
		
		return new GoToPrisonEffect(messages);
	}

}
