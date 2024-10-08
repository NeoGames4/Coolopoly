package game.properties;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Constants;
import game.GameIllegalMoveException;
import game.GameState;
import game.Notification;

public class ChangeMoneyEffect implements PropertyEffect {
	
	public static final String ACTION = "change_money";
	
	public final int amount;
	private String[] messages = {""};

	public ChangeMoneyEffect(int amount, String[] messages) {
		this.amount = amount;
		if(messages != null && messages.length > 0)
			this.messages = messages;
	}
	
	public int messagesLength() {
		return messages.length;
	}
	
	public String getMessage(int i) {
		return messages[i %= messages.length].replace("&m", amount + " " + Constants.MONEY_UNIT);
	}

	@Override
	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException {
		gameState.newNotifications.add(new Notification(gameState.currentPlayer().name + " must malloc " + amount + " " + Constants.MONEY_UNIT + "."));
		gameState.currentPlayer().changeMoney(amount, gameState);
		return true;
	}

	public static ChangeMoneyEffect fromJSON(JSONObject o) {
		String[] messages;
		if(o.has("messages")) {
			JSONArray jsonMessages = o.getJSONArray("messages");
			messages = new String[jsonMessages.length()];
		
			for(int i = 0; i<messages.length; i++)
				messages[i] = jsonMessages.getString(i);
		} else
			messages = null;
		
		return new ChangeMoneyEffect(o.getInt("amount"), messages);
	}

}
