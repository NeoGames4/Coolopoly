package game;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import networking.ServerHandler;

public class GameState {
	
	private final ServerHandler serverHandler;
	
	private final ArrayList<Player> players;
	
	private int currentTurn = 0;
	
	private DiceState playerDiced = null;
	
	private final Random random;

	private GameState(ServerHandler serverHandler, ArrayList<Player> players, int currentTurn) {
		this.serverHandler = serverHandler;
		this.players = players;
		this.currentTurn = currentTurn;
		
		random = new Random();
	}
	
	public DiceState dice() throws GameIllegalMoveException {
		if(playerDiced == null) {
			playerDiced = new DiceState(random.nextInt(6)+1, random.nextInt(6)+1);
			currentPlayer().changePosition(playerDiced.sum());
			return playerDiced;
		} else throw new GameIllegalMoveException("You already rolled the dice.");
	}
	
	public void buy() throws GameIllegalMoveException {
		Player cPlayer = currentPlayer();
		Property p = getProperty(cPlayer.getPosition());
		
		if(p == null || !p.canBeBought())
			throw new GameIllegalMoveException("This property cannot be bought.");
		
		Player pOwner = getPropertyOwner(p);
		if(cPlayer.equals(pOwner))
			throw new GameIllegalMoveException("This property is already yours.");
		if(pOwner != null)
			throw new GameIllegalMoveException("This property already belongs to " + pOwner.name + ".");
		
		if(cPlayer.getMoney() < p.price)
			throw new GameIllegalMoveException("You do not have enough money.");
		
		cPlayer.changeMoney(-p.price);
		cPlayer.properties.put(p, 0);
	}
	
	public boolean playerDiced() {
		return playerDiced != null;
	}
	
	public boolean playerDone() {	// TODO
		return playerDiced != null;
	}
	
	public void nextPlayer() throws GameIllegalMoveException {
		if(!playerDone())
			throw new GameIllegalMoveException("Please finish your turn first.");
		
		playerDiced = null;
		currentTurn++;
		if(currentTurn >= players.size())
			currentTurn = 0;
	}
	
	public Player thisPlayer() {
		for(Player p : players)
			if(p.remoteAdress.equals(p))
				return p;
		return null;
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
	
	public static GameState fromJSON(JSONObject o, ServerHandler serverHandler) {
		ArrayList<Player> players = new ArrayList<>();
		JSONArray p = o.getJSONArray("players");
		for(int i = 0; i<p.length(); i++) {
			players.add(Player.fromJSON(p.getJSONObject(i)));
		}
		
		GameState state = new GameState(serverHandler, players, o.getInt("current_turn"));
		
		state.playerDiced = DiceState.fromJSON(o.getJSONObject("player_diced"));
		
		return state;
	}

}
