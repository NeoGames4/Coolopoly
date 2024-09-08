package game.properties;

import org.json.JSONObject;

import game.Display;

public class PayDiceMultipleEffect implements PropertyEffect {
	
	public static final String ACTION = "pay_dice_multiple";	// TODO strip

	public PayDiceMultipleEffect() {}
	
	@Override
	public void run(Display display) {}

	public static PayDiceMultipleEffect fromJSON(JSONObject o) {
		return new PayDiceMultipleEffect();
	}

}
