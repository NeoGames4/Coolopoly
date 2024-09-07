package game.properties;

import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONObject;

import game.Constants;
import game.GameIllegalMoveException;
import game.GameState;
import game.Player;

public class PayRailwayStationEffect implements PropertyEffect {
	
	public static final String ACTION = "pay_railway_station";

	public PayRailwayStationEffect() {}
	
	@Override
	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException {
		Player cPlayer = gameState.currentPlayer();
		Player oPlayer = gameState.getPropertyOwner(property);
		
		if(oPlayer == null || oPlayer.nameEquals(cPlayer))
			return true;
		
		int propAmount = 0;
		
		Iterator<Entry<Property, Integer>> properties = oPlayer.properties.entrySet().iterator();
		while(properties.hasNext()) {
			Property p = properties.next().getKey();
			if(p.effect != null && p.effect instanceof PayRailwayStationEffect)
				propAmount++;
		}
		
		if(propAmount == 0)
			throw new GameIllegalMoveException("Something went wrong when counting railway stations.", Constants.GAME_ACTION_NEXT);
		
		int taxes = Properties.railwayRents[Math.min(propAmount, Properties.railwayRents.length)-1];
		
		cPlayer.changeMoney(-taxes, gameState);
		oPlayer.changeMoney(taxes, gameState);
		
		return true;
	}

	public static PayRailwayStationEffect fromJSON(JSONObject o) {
		return new PayRailwayStationEffect();
	}

}
