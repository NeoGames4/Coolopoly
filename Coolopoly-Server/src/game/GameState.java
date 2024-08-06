package game;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameState {
	
	private final ArrayList<Player> players;
	
	private int currentTurn = 0;
	
	private DiceState playerDiced = null;
	
	private final Random random;

	public GameState(ArrayList<Player> players, int currentTurn) {
		this.players = players;
		this.currentTurn = currentTurn;
		
		random = new Random();
	}
	
	public DiceState dice() throws GameIllegalMoveException {
		if(playerDiced == null) {
			return playerDiced = new DiceState(random.nextInt(6)+1, random.nextInt(6)+1);
		} else throw new GameIllegalMoveException("You already rolled the dice.");
	}
	
	public boolean playerDone() {
		return playerDiced != null;
	}
	
	public void nextPlayer() {
		if(playerDone()) {
			playerDiced = null;
			currentTurn++;
			if(currentTurn >= players.size())
				currentTurn = 0;
		}
	}
	
	public Player currentPlayer() {
		return players.get(currentTurn);
	}
	
	public Property getProperty(int fieldPosition) {
		return Properties.properties[fieldPosition];
	}
	
	public Player getPropertyOwner(Property property) {
		for(Player p : players) {
			if(p.properties.containsKey(property))
				return p;
		} return null;
	}
	
	public JSONObject toJSON() {
		JSONArray p = new JSONArray();
		for(Player player : players) {
			p.put(player.toJSON());
		}
		
		return new JSONObject()
				.put("players", p)
				.put("current_turn", currentTurn)
				.put("player_diced", playerDiced.toJSON());
	}
	
	public static GameState fromJSON(JSONObject o) {
		ArrayList<Player> players = new ArrayList<>();
		JSONArray p = o.getJSONArray("players");
		for(int i = 0; i<p.length(); i++) {
			players.add(Player.fromJSON(p.getJSONObject(i)));
		}
		
		GameState state = new GameState(players, o.getInt("current_turn"));
		
		state.playerDiced = DiceState.fromJSON(o.getJSONObject("player_diced"));
		
		return state;
	}

}
