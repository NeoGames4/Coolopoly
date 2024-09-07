package game;

import org.json.JSONObject;

import game.properties.Properties;
import game.properties.Property;

public class CardEffect {
	
	public static final int GO_TO_PRISON = -2;
	
	private int earn = 0;
	private int field = -1;
	
	private final JSONObject asJSONObject;

	public CardEffect(JSONObject asJSONObject, String cardType) {
		this.asJSONObject = asJSONObject;
		earn	= asJSONObject.optInt("earn", 0);
		field	= asJSONObject.optInt("go_to", -1);
		
		asJSONObject.put("card_type", cardType);
	}
	
	public void gotoInfoAndEarn(GameState gameState, Player player) {
		Property pTarget = field >= 0 ? Properties.properties[field % Properties.properties.length] : null;
		
		String summery = player.name;
		// PAY
		if(earn > 0) {
			summery += " freed " + earn + " " + Constants.MONEY_UNIT;
		} else if(earn < 0) {
			summery += " is forced to malloc " + Math.abs(earn) + " " + Constants.MONEY_UNIT;
		}
		// AND
		if(earn != 0 && (pTarget != null || field == GO_TO_PRISON)) {
			summery += " and";
		}
		// GO
		if(pTarget != null) {
			summery += " will be redirected to " + pTarget.title;
			player.setPosition(pTarget.position);
		} else if(field == GO_TO_PRISON) {
			summery += " will be put in sandbox isolation";
			// TODO go to prison
		}
		// DOT
		summery += ".";
		
		if(earn > 0)
			player.changeMoney(earn, gameState);
		
		gameState.newNotifications.add(new Notification(summery, 7000));
	}
	
	public void pay(GameState gameState, Player player) {
		if(earn < 0)
			player.changeMoney(earn, gameState);
	}
	
	public JSONObject toJSON() {
		return asJSONObject;
	}
	
	public static CardEffect fromJSON(JSONObject o, String cardType) {
		return new CardEffect(o, cardType);
	}

}
