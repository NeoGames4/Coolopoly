package game.properties;

import org.json.JSONObject;

import game.GameIllegalMoveException;
import game.GameState;

public interface PropertyEffect {

	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException;
	
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
			case PayRailwayStationEffect.ACTION:
				return PayRailwayStationEffect.fromJSON(o);
		} return null;
	}

}
