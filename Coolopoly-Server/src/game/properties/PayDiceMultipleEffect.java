package game.properties;

import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONObject;

import game.Constants;
import game.GameIllegalMoveException;
import game.GameState;
import game.Player;

public class PayDiceMultipleEffect implements PropertyEffect {
	
	public static final String ACTION = "pay_dice_multiple";

	public PayDiceMultipleEffect() {}
	
	@Override
	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException {
		if(!gameState.playerDiced())
			throw new GameIllegalMoveException("Cannot apply dice dependend taxes without a dice state.", Constants.GAME_ACTION_NEXT);
		
		Player cPlayer = gameState.currentPlayer();
		Player oPlayer = gameState.getPropertyOwner(property);
		
		if(oPlayer == null || oPlayer.nameEquals(cPlayer))
			return true;
		
		int propAmount = 0;
		
		Iterator<Entry<Property, Integer>> properties = oPlayer.properties.entrySet().iterator();
		while(properties.hasNext()) {
			Property p = properties.next().getKey();
			if(p.effect != null && p.effect instanceof PayDiceMultipleEffect)
				propAmount++;
		}
		
		if(propAmount == 0)
			throw new GameIllegalMoveException("Something went wrong when counting dice dependend properties.", Constants.GAME_ACTION_NEXT);
		
		int factor = propAmount == 1 ? 4 : 10;
		int taxes = factor*gameState.getDiceState().sum();
		
		cPlayer.changeMoney(-taxes, gameState);
		oPlayer.changeMoney(taxes, gameState);
		
		return true;
	}

	public static PayDiceMultipleEffect fromJSON(JSONObject o) {
		return new PayDiceMultipleEffect();
	}

}
