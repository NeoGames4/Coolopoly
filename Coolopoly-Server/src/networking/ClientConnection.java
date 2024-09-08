package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import game.Constants;
import game.Game;
import game.GameIllegalMoveException;
import game.Notification;
import game.Player;
import game.properties.Properties;
import launcher.Console;

public class ClientConnection extends Thread {
	
	public String username;
	private String image;
	
	private final Game game;

	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;
	
	public final HashMap<String, Boolean> userVotes = new HashMap<>();

	public ClientConnection(Game game, Socket socket, InputStream input, OutputStream output) {
		this.game = game;
		this.socket = socket;
		this.input = new BufferedReader(new InputStreamReader(input));
		this.output = new PrintWriter(output, true);
	}
	
	@Override
	public void run() {
		while(socket != null && !socket.isClosed()) {
			try {
				JSONObject o = null;
				try {
					o = new JSONObject(input.readLine());
				} catch(NullPointerException | SocketException e) {
					disconnect();
					break;
				}
				
				Console.log("Catching " + o.toString());
				
				switch(o.getString("intent")) {
					case Constants.MESSAGE_INTENT_ACTION:
						handleAction(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_LOG_IN:
						handleLogIn(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_VOTE:
						handleVote(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_GAME_STATE:
						handleGameStateRequest();
						break;
					case Constants.MESSAGE_INTENT_MISC:
						break;
					default:
						throw new Exception("Invalid intent \"" + o.getString("intent") + "\".");
				}
			} catch(GameIllegalMoveException e) {
				send(Constants.MESSAGE_INTENT_EXCEPTION, e.asJSON());
			} catch(IOException e) {
				e.printStackTrace();
			} catch(JSONException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleAction(JSONObject o) throws GameIllegalMoveException, JSONException, Exception {
		if(isWatcher())
			throw new GameIllegalMoveException("As a watcher you may not interfere.", Constants.MESSAGE_INTENT_ACTION);
		
		if(!game.state.currentPlayer().name.equals(username))
			throw new GameIllegalMoveException("It is not your turn.", Constants.MESSAGE_INTENT_ACTION);
		
		switch(o.getString("action")) {
			case Constants.GAME_ACTION_DICE:
				game.state.dice();
				game.server.sendGameState();
				break;
				
			case Constants.GAME_ACTION_BUY:
				if(!game.state.playerDiced())
					throw new GameIllegalMoveException("You need to roll the dices first.", Constants.MESSAGE_INTENT_ACTION);
				
				switch(o.getString("subject")) {
					case Constants.GAME_SUBJECT_BUY_PROPERTY:
						game.state.buyProperty();
						break;
					case Constants.GAME_SUBJECT_BUY_HOUSE:
						game.state.buyHouse(game.state.getProperty(o.getInt("property")));
						break;
					case Constants.GAME_SUBJECT_SELL_HOUSE:
						game.state.sellHouse(game.state.getProperty(o.getInt("property")));
						break;
				}
				game.server.sendGameState();
				
				break;
				
			case Constants.GAME_ACTION_NEXT:
				game.state.nextPlayer();
				game.server.sendGameState();
				break;
				
			case Constants.GAME_ACTION_MORTGAGE:
				if(!game.state.playerDiced())
					throw new GameIllegalMoveException("You need to roll the dices first.", Constants.MESSAGE_INTENT_ACTION);
				
				switch(o.getString("subject")) {
					case Constants.GAME_SUBJECT_MORTGAGE:
						game.state.mortgageProperty(game.state.getProperty(o.getInt("property")));
						break;
					case Constants.GAME_SUBJECT_UNMORTGAGE:
						game.state.unmortgageProperty(game.state.getProperty(o.getInt("property")));
						break;
				}
				game.server.sendGameState();
				break;
				
			default:
				throw new Exception("Invalid intent \"" + o.optString("intent") + "\".");
		}
	}
	
	private void handleLogIn(JSONObject o) throws GameIllegalMoveException, JSONException, Exception {
		String newName = o.getString("username");
		boolean watching = o.optBoolean("just_watching", false);
		Console.log("Catched client login request for username \"" + newName + "\", just watching: " + (watching ? "yes" : "no") + ".");
		
		// GAME RUNNING
		if(!watching && (game.state.isRunning() || game.state.isOver())) {
			sendException("Already running", "The game is already running.", Constants.MESSAGE_INTENT_LOG_IN);
			disconnect();
		}
		
		// PLAYERS MAXIMUM
		if(!watching && game.state.getPlayers().size() >= game.playersLimit) {
			sendException("Full house", "The server reached the maximum number of players.", Constants.MESSAGE_INTENT_LOG_IN);
			disconnect();
			return;
		}
		
		// USERNAME LENGTH RESTRICTIONS
		if(newName.length() < 1 || newName.length() > Constants.USERNAME_MAX_LENGTH) {
			sendException("Invalid username", "The length of an username is required to be between 1 and " + Constants.USERNAME_MAX_LENGTH + " characters. Please try again.", Constants.MESSAGE_INTENT_LOG_IN);
			disconnect();
			return;
		}
		
		// USERNAME ALLOWED CHARS
		for(char c : newName.toCharArray()) {
			if(!Constants.USERNAME_ALLOWED_CHARS.contains(c + "")) {
				sendException("Invalid username", "The character '" + c + "' is not allowed. Please try again.", Constants.MESSAGE_INTENT_LOG_IN);
				disconnect();
				return;
			}
		}
		
		// USERNAME ALREADY TAKEN
		for(ClientConnection c : game.server.getClients()) {
			if(newName.equalsIgnoreCase(c.username)) {
				sendException("Username taken", "This username is already taken. Please try again.", Constants.MESSAGE_INTENT_LOG_IN);
				disconnect();
				return;
			}
		}
		
		Console.log("Granting client login request...");
		username = newName;
		send(Constants.MESSAGE_INTENT_LOG_IN, o);
		
		send(Constants.MESSAGE_INTENT_PROPERTIES, Properties.propertiesAsJSON);
		
		if(!watching) {
			int playerImageIndex = (int) (Math.random() * game.playerImages.size());
			game.state.addPlayer(new Player(username, getRemoteAddress(), image = game.playerImages.get(playerImageIndex)));
			game.playerImages.remove(playerImageIndex);
		}
		
		game.server.sendGameState();
	}
	
	public void handleVote(JSONObject o) throws GameIllegalMoveException {
		if(isWatcher())
			throw new GameIllegalMoveException("As a watcher you may not vote.", Constants.MESSAGE_INTENT_ACTION);
		
		userVotes.put(o.getString("subject"), o.getBoolean("state"));
		
		game.state.newNotifications.add(
			new Notification(username + " voted " + (o.getBoolean("state") ? "for" : "against") + " game " + o.getString("subject") + "!")
		);
		
		game.server.countUserVotes(o.getString("subject"));
	}
	
	public void handleGameStateRequest() {
		sendGameState(true);
	}
	
	/**
	 * Sends the game state to the client via {@link #send(String, JSONObject)}.
	 */
	public void sendGameState(boolean includeNotifications) {
		send(Constants.MESSAGE_INTENT_GAME_STATE, game.state.toJSON(includeNotifications));
	}
	
	/**
	 * Sends an exception to the client via {@link #send(String, JSONObject)}.
	 * @param title the exception title
	 * @param message the exception message
	 * @param causingIntent the intent that was causing this exception
	 */
	public void sendException(String title, String message, String causingIntent) {
		JSONObject e = new JSONObject()
				.put("title", title)
				.put("message", message);
		
		if(causingIntent != null)
			e.put("intent", causingIntent);
		
		send(Constants.MESSAGE_INTENT_EXCEPTION, e);
	}
	
	/**
	 * Sends `content` to the client.
	 * @param intent the intent of the package
	 * @param content the content of the package
	 */
	public void send(String intent, JSONObject content) {
		JSONObject m = new JSONObject()
				.put("intent", intent)
				.put("content", content);
		
		output.println(m.toString());
		if(socket != null)
			Console.log("Sent " + intent + " packet to client " + socket.getLocalAddress() + " with content " + m.toString() + ".");
	}
	
	public String getRemoteAddress() {
		if(socket == null)
			return null;
		return socket.getRemoteSocketAddress().toString();
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected() && !socket.isClosed();
	}
	
	public boolean isPlayer() {
		for(Player p : game.state.getPlayers())
			if(p.name.equals(username))
				return true;
		
		return false;
	}
	
	public boolean isWatcher() {
		return !isPlayer();
	}
	
	public void disconnect() throws IOException {
		if(isConnected()) {
			Console.log("Disconnecting client \"" + username + "\" with address " + getRemoteAddress() + "...");
			socket.close();
			interrupt();
			socket = null;
			if(!game.server.removeClient(this))
				Console.err("Could not find the client in the clients array.");
			if(!game.state.removePlayer(username))
				Console.err("Could not find the player in the players array.");
			game.playerImages.add(image);
			
			if(game.state.getPlayers().isEmpty())
				game.reset();
			
			game.server.sendGameState();
		}
	}

}
