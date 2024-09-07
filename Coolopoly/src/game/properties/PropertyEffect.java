package game.properties;

import org.json.JSONObject;

import game.Display;

public interface PropertyEffect {

	public void run(Display display);
	
	public static PropertyEffect fromJSON(JSONObject o) {
		switch(o.optString("action", "")) {
			case ChangeMoneyEffect.ACTION:
				return ChangeMoneyEffect.fromJSON(o);
			case GoToEffect.ACTION:
				return GoToEffect.fromJSON(o);
			case GoToPrisonEffect.ACTION:
				return GoToPrisonEffect.fromJSON(o);
			case PayDiceMultipleEffect.ACTION:
				return PayDiceMultipleEffect.fromJSON(o);
			case DrawCardEffect.ACTION:
				return DrawCardEffect.fromJSON(o);
		} return null;
	}

}
