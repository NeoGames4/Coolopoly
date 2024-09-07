package game;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import game.properties.DrawCardEffect;
import game.properties.Properties;
import game.properties.Property;

public class GameState {
	
	private final ArrayList<Player> players;
	
	private int currentTurn = 0;
	
	private DiceState diceState = null;
	
	public CardEffect cardEffect = null;
	private boolean informedAboutCardEffect = false;
	
	private final Random random;
	
	public final static int	GAME_STATE_WAITING	= 0,
							GAME_STATE_RUNNING	= 1,
							GAME_STATE_OVER		= 2;
	
	private int currentState = GAME_STATE_WAITING;
	
	public final ArrayList<Notification> newNotifications = new ArrayList<>();

	public GameState(ArrayList<Player> players, int currentTurn) {
		this.players = players;
		this.currentTurn = currentTurn;
		
		random = new Random();
	}
	
	public DiceState dice() throws GameIllegalMoveException {
		if(!isRunning())
			throw new GameIllegalMoveException("The game is not running.", Constants.MESSAGE_INTENT_ACTION);
		
		if(diceState != null)
			throw new GameIllegalMoveException("You already rolled the dice.", Constants.MESSAGE_INTENT_ACTION);
		
		diceState = new DiceState(random.nextInt(6)+1, random.nextInt(6)+1);
		
		if(currentPlayer().getPosition() + diceState.sum() >= Properties.properties.length)
			currentPlayer().changeMoney(200, this);
		
		currentPlayer().changePosition(diceState.sum());
		
		newNotifications.add(new Notification(currentPlayer().name + " rolled the dice: " + diceState.sum() + "."));
		
		Property prop = getProperty(currentPlayer().getPosition());
		
		if(prop.effect != null && (prop.effect instanceof DrawCardEffect))
			prop.effect.run(this, prop);
		
		if(cardEffect != null)
			cardEffect.gotoInfoAndEarn(this, currentPlayer());
		
		return diceState;
	}
	
	public void buyProperty() throws GameIllegalMoveException {
		if(!isRunning())
			throw new GameIllegalMoveException("The game is not running.", Constants.MESSAGE_INTENT_ACTION);		
		
		if(!playerDiced())
			throw new GameIllegalMoveException("You need to dice first.", Constants.MESSAGE_INTENT_ACTION);
		
		Player cPlayer = currentPlayer();
		Property p = getProperty(cPlayer.getPosition());
		
		if(p == null || !p.canBeBought())
			throw new GameIllegalMoveException("This property cannot be bought.", Constants.MESSAGE_INTENT_ACTION);
		
		Player pOwner = getPropertyOwner(p);
		if(cPlayer.equals(pOwner))
			throw new GameIllegalMoveException("This property is already yours.", Constants.MESSAGE_INTENT_ACTION);
		if(pOwner != null)
			throw new GameIllegalMoveException("This property already belongs to " + pOwner.name + ".", Constants.MESSAGE_INTENT_ACTION);
		
		if(cPlayer.getMoney() < p.price)
			throw new GameIllegalMoveException("You do not have enough money.", Constants.MESSAGE_INTENT_ACTION);
		
		cPlayer.changeMoney(-p.price, this);
		cPlayer.properties.put(p, 0);
		
		newNotifications.add(new Notification(currentPlayer().name + " bought " + p.title + " for " + p.price + " " + Constants.MONEY_UNIT + "."));
	}
	
	public boolean playerCanBuyProperty(Player p, Property prop) {
		return currentPlayer().nameEquals(p)
				&& playerDiced()
				&& prop.canBeBought()
				&& getPropertyOwner(prop) == null
				&& p.getMoney() <= prop.price;
	}
	
	public void buyHouse(Property prop) throws GameIllegalMoveException {
		if(!isRunning())
			throw new GameIllegalMoveException("The game is not running.", Constants.MESSAGE_INTENT_ACTION);		
		
		Player cPlayer = currentPlayer();
		
		if(prop.pricePerHouse < 0)
			throw new GameIllegalMoveException("You cannot build a server on " + prop.title + ".", Constants.MESSAGE_INTENT_ACTION);
		
		if(!playerOwnsStreet(cPlayer, prop.street))
			throw new GameIllegalMoveException("This street is not fully yours.", Constants.MESSAGE_INTENT_ACTION);
		
		if(cPlayer.properties.get(prop) >= prop.rent.length-1)
			throw new GameIllegalMoveException("You already reached the maximum of servers you can build here.", Constants.MESSAGE_INTENT_ACTION);
		
		if(cPlayer.getMoney() < prop.pricePerHouse)
			throw new GameIllegalMoveException("You do not have enough money.", Constants.MESSAGE_INTENT_ACTION);
		
		cPlayer.changeMoney(-prop.pricePerHouse, this);
		cPlayer.properties.put(prop, cPlayer.properties.get(prop)+1);
		
		newNotifications.add(new Notification(currentPlayer().name + " built a server on " + prop.title + " for " + prop.pricePerHouse + " " + Constants.MONEY_UNIT + "."));
	}
	
	public void sellHouse(Property prop) throws GameIllegalMoveException {
		if(!isRunning())
			throw new GameIllegalMoveException("The game is not running.", Constants.MESSAGE_INTENT_ACTION);		
		
		Player cPlayer = currentPlayer();
		
		if(!cPlayer.nameEquals(getPropertyOwner(prop)))
			throw new GameIllegalMoveException("This property is not yours.", Constants.MESSAGE_INTENT_ACTION);
		
		if(cPlayer.properties.get(prop) <= 0)
			throw new GameIllegalMoveException("This property does not have any servers built on it.", Constants.MESSAGE_INTENT_ACTION);
		
		cPlayer.changeMoney(prop.pricePerHouse, this);
		cPlayer.properties.put(prop, cPlayer.properties.get(prop)-1);
		
		newNotifications.add(new Notification(currentPlayer().name + " sold a server on " + prop.title + " for " + prop.pricePerHouse + " " + Constants.MONEY_UNIT + "."));
	}
	
	public boolean playerDiced() {
		return diceState != null;
	}
	
	public DiceState getDiceState() {
		return diceState;
	}
	
	public void nextPlayer() throws GameIllegalMoveException {
		if(!isRunning())
			throw new GameIllegalMoveException("The game is not running.", Constants.MESSAGE_INTENT_ACTION);
		
		if(!playerDiced())
			throw new GameIllegalMoveException("Please finish your turn first.", Constants.MESSAGE_INTENT_ACTION);
		
		boolean mayContinue = payRentUseEffects();
		if(!mayContinue)
			return;
		
		boolean dicedDouble = diceState.diceA == diceState.diceB;
		
		diceState = null;
		cardEffect = null;
		informedAboutCardEffect = false;
		if(!dicedDouble)
			currentTurn++;
		if(currentTurn >= players.size())
			currentTurn = 0;
		
		if(!dicedDouble)
			newNotifications.add(new Notification("It is " + currentPlayer().name + "’" + (currentPlayer().name.endsWith("s") ? "" : "s") + " turn."));
		else
			newNotifications.add(new Notification(currentPlayer().name + " rolled a double. It is their turn again."));
	}
	
	private boolean payRentUseEffects() throws GameIllegalMoveException {
		boolean mayContinue = true;
		
		Player cPlayer = currentPlayer();
		Property prop = getProperty(cPlayer.getPosition());
		Player oPlayer = getPropertyOwner(prop);
		
		if(oPlayer != null && !oPlayer.nameEquals(cPlayer)) {
			int taxes = prop.getTaxes(oPlayer.properties.get(prop));
			
			if(taxes > 0) {
				if(oPlayer.properties.get(prop) <= 0 && playerOwnsStreet(oPlayer, prop.street))
					taxes *= 2;
				
				cPlayer.changeMoney(-taxes, this);
				oPlayer.changeMoney(taxes, this);
			}
		}
		
		if(prop.effect != null && !(prop.effect instanceof DrawCardEffect))
			mayContinue = prop.effect.run(this, prop);
		
		if(cardEffect != null)
			cardEffect.pay(this, cPlayer);
		
		return mayContinue;
	}
	
	public Player currentPlayer() {
		return players.get(currentTurn);
	}
	
	public void addPlayer(Player player) {
		players.add(player);
		newNotifications.add(new Notification(player.name + " joined."));
	}
	
	public boolean removePlayer(String username) {
		boolean r = players.removeIf(p -> p.name.equals(username));
		
		if(players.size() > 0) {
			currentTurn %= players.size();
		}
		
		diceState = null;
		
		newNotifications.add(new Notification(username + " left."));
		
		// TODO evaluate if only one player is left
		
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
	
	public boolean playerOwnsStreet(Player player, String street) {
		if(street == null || player == null)
			return false;
		
		for(Property p : Properties.properties) {
			if(street.equals(p.street) && !player.nameEquals(getPropertyOwner(p)))
				return false;
		} return true;
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
	
	public void evaluate() {
		currentState = GAME_STATE_OVER;
	}
	
	public JSONObject toJSON(boolean includeNotifications) {
		JSONArray p = new JSONArray();
		for(Player player : players) {
			p.put(player.toJSON());
		}
		
		JSONObject o = new JSONObject()
				.put("players", p)
				.put("current_turn", currentTurn)
				.put("current_state", currentState);
		
		if(diceState != null)
			o.put("player_diced", diceState.toJSON());
		
		if(cardEffect != null && !informedAboutCardEffect) {
			o.put("current_card_effect", cardEffect.toJSON());
			informedAboutCardEffect = true;
		}
		
		if(includeNotifications && !newNotifications.isEmpty()) {
			JSONObject n = new JSONObject();
			for(int i = 0; i<newNotifications.size(); i++)
				n.put(i + "", newNotifications.get(i).toJSON());
			n.put("amount", newNotifications.size());
			o.put("notifications", n);
		}
		
		return o;
	}

}
