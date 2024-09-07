package game.properties;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Display;

public class GoToPrisonEffect implements PropertyEffect {
	
	public static final String ACTION = "go_to_prison";
	
	private String[] messages = {""};

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
	public void run(Display display) {
		// TODO Auto-generated method stub

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
