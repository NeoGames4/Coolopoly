package game;

import org.json.JSONObject;

import game.properties.Properties;
import game.properties.Property;
import game.uielements.DialogWindow;
import game.uielements.DialogWindowOption;
import misc.Constants;

public class CardEffect {
	
	public static final int GO_TO_PRISON = -2;
	
	private final String	message,
							cardType;
	
	private int	earn = 0,
				field = -1;

	private CardEffect(String message, String cardType) {
		this.message = message;
		this.cardType = cardType;
	}
	
	private CardEffect earn(int amount) {
		this.earn = amount;
		return this;
	}
	
	private CardEffect goTo(int field) {
		this.field = field;
		return this;
	}
	
	public void openDialog(Display display) {
		String summery = message + "";
		
		if(earn > 0)
			summery += "\nFREE " + earn + " " + Constants.MONEY_UNIT;
		else if(earn < 0)
			summery += "\nMALLOC " + earn + " " + Constants.MONEY_UNIT + " (debited as you finish your turn)";
		
		Property pTarget = field >= 0 ? Properties.getProperties()[field%Properties.getProperties().length] : null;
		
		if(pTarget != null)
			summery += "\nGO TO " + pTarget.title;
		
		if(field == GO_TO_PRISON)
			summery += "\nGO TO PRISON";
		
		display.dialogs.add(
			new DialogWindow(
				Constants.getCardName(cardType) + "",
				summery,
				new DialogWindowOption[] {
						DialogWindowOption.getDiscardOption("OK", display.dialogs)
				}
			)
		);
	}
	
	public static CardEffect fromJSON(JSONObject o) {
		if(o == null)
			return null;
		return new CardEffect(o.getString("message"), o.getString("card_type"))
					.earn(o.optInt("earn", 0))
					.goTo(o.optInt("go_to", -1));
	}

}
