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
	
	private boolean isRunning = false;

	public GameState(ArrayList<Player> players, int currentTurn) {
		this.players = players;
		this.currentTurn = currentTurn;
		
		random = new Random();
	}
	
	public DiceState dice() throws GameIllegalMoveException {
		if(!isRunning)
			throw new GameIllegalMoveException("The game is paused.");
		
		if(playerDiced != null)
			throw new GameIllegalMoveException("You already rolled the dice.");
		
		playerDiced = new DiceState(random.nextInt(6)+1, random.nextInt(6)+1);
		currentPlayer().changePosition(playerDiced.sum());
		return playerDiced;
	}
	
	public void buy() throws GameIllegalMoveException {
		if(!isRunning)
			throw new GameIllegalMoveException("The game is paused.");
		
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
		if(!isRunning)
			throw new GameIllegalMoveException("The game is paused.");
		
		if(!playerDone())
			throw new GameIllegalMoveException("Please finish your turn first.");
		
		playerDiced = null;
		currentTurn++;
		if(currentTurn >= players.size())
			currentTurn = 0;
	}
	
	public Player currentPlayer() {
		return players.get(currentTurn);
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public boolean removePlayer(String username) {
		boolean r = players.removeIf(p -> p.name.equals(username));
		if(players.size() > 0) {
			currentTurn %= players.size();
		}
		playerDiced = null;
		return r;
	}
	
	public ArrayList<Player> getPlayers() {
		return new ArrayList<>(players);
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
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void start() {
		isRunning = true;
	}
	
	public JSONObject toJSON() {
		JSONArray p = new JSONArray();
		for(Player player : players) {
			p.put(player.toJSON());
		}
		
		JSONObject o = new JSONObject()
				.put("players", p)
				.put("current_turn", currentTurn)
				.put("is_running", isRunning);
		
		if(playerDiced != null)
			o.put("player_diced", playerDiced.toJSON());
		
		return o;
	}

}
