package game.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.CardEffect;
import game.Game;
import game.GameIllegalMoveException;
import game.GameState;
import game.Notification;
import launcher.Console;

public class DrawCardEffect implements PropertyEffect {
	
	public static final String	ACTION = "draw_card";
	
	public final static String	COMMUNITY_CARD	= "community",
								CHANCE_CARD		= "chance";
		
	private static final Random RANDOM = new Random();
	
	public final String card;
	
	private static final ArrayList<CardEffect>	communityCards	= new ArrayList<>(),
												chanceCards		= new ArrayList<>();
	static {
		InputStream in = Game.class.getResourceAsStream("cards.json");
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		
		String content = "";
		JSONObject o = null;
		
		try {
			for(String line; (line = r.readLine()) != null; content += line + "\n");
			o = new JSONObject(content);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch(JSONException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		JSONArray	comCards = o.getJSONArray(COMMUNITY_CARD),
					chaCards = o.getJSONArray(CHANCE_CARD);
		
		for(int i = 0; i<comCards.length(); i++)
			communityCards.add(CardEffect.fromJSON(comCards.getJSONObject(i), COMMUNITY_CARD));
		
		for(int i = 0; i<chaCards.length(); i++)
			chanceCards.add(CardEffect.fromJSON(chaCards.getJSONObject(i), CHANCE_CARD));
		
		Console.log("Loaded cards.json (" + comCards.length() + "community, " + chaCards.length() + " chance cards).");
	}

	public DrawCardEffect(String card) {
		this.card = card;
	}

	@Override
	public boolean run(GameState gameState, Property property) throws GameIllegalMoveException {
		gameState.newNotifications.add(new Notification(gameState.currentPlayer().name + " drew a " + card + " card."));
		
		switch(card) {
			case COMMUNITY_CARD:
				gameState.cardEffect = communityCards.get(RANDOM.nextInt(communityCards.size()));
				break;
			case CHANCE_CARD:
				gameState.cardEffect = chanceCards.get(RANDOM.nextInt(chanceCards.size()));
				break;
		}
		
		return true;
	}

	public static DrawCardEffect fromJSON(JSONObject o) {
		return new DrawCardEffect(o.getString("card_type"));
	}

}
