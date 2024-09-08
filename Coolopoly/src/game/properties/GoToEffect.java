package game.properties;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Display;

public class GoToEffect implements PropertyEffect {
	
	public static final String ACTION = "go_to";	// TODO strip
	
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
	public void run(Display display) {}

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
