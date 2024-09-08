package game;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import game.properties.Properties;
import game.properties.Property;
import misc.Constants;
import misc.Notification;
import networking.ServerConnection;

public class GameState {
	
	private final ServerConnection serverConnection;
	
	private final ArrayList<Player> players;
	
	private int currentTurn = 0;
	
	private DiceState playerDiced = null;
	
	public CardEffect cardEffect = null;
	
	public int toPayEstimate = 0;
	
	public final static int	GAME_STATE_WAITING	= 0,
			GAME_STATE_RUNNING	= 1,
			GAME_STATE_OVER		= 2;

	private int currentState = GAME_STATE_WAITING;
	
	public final String identifier;

	private GameState(ServerConnection serverConnection, ArrayList<Player> players, int currentTurn) {
		this.serverConnection = serverConnection;
		this.players = players;
		this.currentTurn = currentTurn;
		
		identifier = System.currentTimeMillis() + "" + Math.round(Math.random()*1000000);
	}
	
	public void dice() {
		JSONObject o = new JSONObject()
						.put("action", Constants.GAME_ACTION_DICE);
		
		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public void buyProperty() {
		JSONObject o = new JSONObject()
				.put("action", Constants.GAME_ACTION_BUY)
				.put("subject", Constants.GAME_SUBJECT_BUY_PROPERTY);

		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public void buyHouse(Property prop) {
		JSONObject o = new JSONObject()
				.put("action", Constants.GAME_ACTION_BUY)
				.put("subject", Constants.GAME_SUBJECT_BUY_HOUSE)
				.put("property", prop.position);

		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public void sellHouse(Property prop) {
		JSONObject o = new JSONObject()
				.put("action", Constants.GAME_ACTION_BUY)
				.put("subject", Constants.GAME_SUBJECT_SELL_HOUSE)
				.put("property", prop.position);

		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public boolean playerCanBuy() {
		Player p = thisPlayer();
		Property prop = currentProperty();
		
		/*Console.log("Current " + currentPlayer().nameEquals(p));
		Console.log("Diced " + playerDiced());
		Console.log("Buyable " + prop.canBeBought());
		Console.log("Owner " + (getPropertyOwner(prop) == null));
		Console.log("Money " + (p.getMoney() <= prop.price) + "\n");*/
		
		return currentPlayer().nameEquals(p)
				&& playerDiced()
				&& prop.canBeBought()
				&& getPropertyOwner(prop) == null
				&& p.getMoney() >= prop.price;
	}
	
	public void mortgageProperty(Property prop) {
		JSONObject o = new JSONObject()
				.put("action", Constants.GAME_ACTION_MORTGAGE)
				.put("subject", Constants.GAME_SUBJECT_MORTGAGE)
				.put("property", prop.position);

		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public void unmortgageProperty(Property prop) {
		JSONObject o = new JSONObject()
				.put("action", Constants.GAME_ACTION_MORTGAGE)
				.put("subject", Constants.GAME_SUBJECT_UNMORTGAGE)
				.put("property", prop.position);

		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public boolean playerDiced() {
		return playerDiced != null;
	}
	
	public DiceState getDiceState() {
		return playerDiced;
	}
	
	public void nextPlayer() {
		JSONObject o = new JSONObject()
				.put("action", Constants.GAME_ACTION_NEXT);

		serverConnection.send(Constants.MESSAGE_INTENT_ACTION, o);
	}
	
	public Player thisPlayer() {
		for(Player p : players)
			if(p.name.equals(serverConnection.getUsername()))
				return p;
		return null;
	}
	
	public Player getPlayer(String username) {
		for(Player p : new ArrayList<>(players))
			if(p.name.equals(username))
				return p;
		return null;
	}
	
	public ArrayList<Player> getPlayers() {
		return new ArrayList<>(players);
	}
	
	public Player currentPlayer() {
		return players.get(currentTurn);
	}
	
	public Property currentProperty() {
		return Properties.getProperties()[currentPlayer().getPosition()];
	}
	
	public Player getPropertyOwner(Property property) {
		for(Player p : players) {
			if(p.properties.containsKey(property))
				return p;
		} return null;
	}
	
	public HashMap<Property, Player> getPropertiesWithOwners() {
		HashMap<Property, Player> r = new HashMap<>();
		
		for(Property prop : Properties.getProperties()) {
			if(!prop.canBeBought())
				continue;
			
			Player p = getPropertyOwner(prop);
			r.put(prop, p);
		} return r;
	}
	
	public boolean isWaiting() {
		return currentState == GAME_STATE_WAITING;
	}
	
	public boolean isRunning() {
		return currentState == GAME_STATE_RUNNING;
	}
	
	public boolean isOver() {
		return currentState == GAME_STATE_OVER;
	}
	
	public int getCurrentState() {
		return currentState;
	}
	
	public void start() {
		currentState = GAME_STATE_RUNNING;
	}
	
	public static GameState fromJSON(JSONObject o, ServerConnection connection) {
		ArrayList<Player> players = new ArrayList<>();
		JSONArray p = o.getJSONArray("players");
		for(int i = 0; i<p.length(); i++) {
			Player player = Player.fromJSON(p.getJSONObject(i));
			
			if(connection.getLatestGameState() != null) {
				Player prev = connection.getLatestGameState().getPlayer(player.name);
				if(prev != null) {
					player.xTransition = prev.xTransition;
					player.yTransition = prev.yTransition;
					player.aTransition = prev.aTransition;
				}
			}
			
			players.add(player);
		}
		
		GameState state = new GameState(connection, players, o.getInt("current_turn"));
		
		state.playerDiced = DiceState.fromJSON(o.optJSONObject("player_diced"));
		
		state.cardEffect = CardEffect.fromJSON(o.optJSONObject("current_card_effect"));
		
		state.toPayEstimate = o.optInt("to_pay_est", 0);
		
		state.currentState = o.getInt("current_state");
		
		if(connection.display != null) {
			if(o.has("notifications")) {
				JSONObject n = o.getJSONObject("notifications");
				int amount = n.getInt("amount");
				for(int i = 0; i<amount; i++) {
					connection.display.notifications.add(
						Notification.fromJSON(n.getJSONObject(i + ""))
					);
				}
			} connection.display.notifications.removeIf(n -> n.timestamp+n.displayDuration < System.currentTimeMillis() && n.aTransition != null && n.aTransition.isDone());
		}
		
		return state;
	}

}
