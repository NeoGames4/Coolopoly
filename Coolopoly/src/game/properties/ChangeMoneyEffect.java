package game.properties;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Display;
import misc.Constants;

public class ChangeMoneyEffect implements PropertyEffect {
	
	public static final String ACTION = "change_money";
	
	public final int amount;
	private String[] messages = {""};	// TODO add message support

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
	public void run(Display display) {
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
