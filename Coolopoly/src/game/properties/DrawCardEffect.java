package game.properties;

import org.json.JSONObject;

import game.Display;

public class DrawCardEffect implements PropertyEffect {
	
	public static final String	ACTION = "draw_card";
	
	public final static String	COMMUNITY_CARD	= "community",
								RISK_CARD		= "chance";
	
	public final String card;

	public DrawCardEffect(String card) {
		this.card = card;
	}

	@Override
	public void run(Display display) {
		// TODO Auto-generated method stub

	}

	public static DrawCardEffect fromJSON(JSONObject o) {
		return new DrawCardEffect(o.getString("card_type"));
	}

}
