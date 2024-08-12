package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import game.Constants;
import game.Game;
import game.GameIllegalMoveException;
import game.Player;
import launcher.Console;

public class ClientConnection extends Thread {
	
	public String username;
	
	private final Game game;

	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;
	
	private boolean isRunning = true;

	public ClientConnection(Game game, Socket socket, InputStream input, OutputStream output) {
		this.game = game;
		this.socket = socket;
		this.input = new BufferedReader(new InputStreamReader(input));
		this.output = new PrintWriter(output, true);
	}
	
	@Override
	public void run() {
		while(isRunning) {
			try {
				JSONObject o = null;
				try {
					o = new JSONObject(input.readLine());
				} catch(NullPointerException | SocketException e) {
					disconnect();
					break;
				}
				
				switch(o.getString("intent")) {
					case Constants.MESSAGE_INTENT_ACTION:
						handleAction(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_LOG_IN:
						handleLogIn(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_MISC:
						// TODO
						break;
					default:
						throw new Exception("Invalid intent \"" + o.getString("intent") + "\".");
				}
			} catch(GameIllegalMoveException e) {
				Console.err(getRemoteAddress() + " tried to move, but it is somebody else’s turn.");
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
		if(!game.state.currentPlayer().remoteAddress.equals(getRemoteAddress()))
			throw new GameIllegalMoveException("It is not your turn.");
		
		switch(o.getString("action")) {
			case Constants.GAME_ACTION_DICE:
				game.state.dice();
				sendGameState();
				break;
				
			case Constants.GAME_ACTION_BUY:
				if(!game.state.playerDiced())
					throw new GameIllegalMoveException("You need to roll the dices first.");
				
				game.state.buy();
				sendGameState();
				
				break;
				
			case Constants.GAME_ACTION_NEXT:
				game.state.nextPlayer();
				sendGameState();
				break;
				
			default:
				throw new Exception("Invalid intent \"" + o.getString("intent") + "\".");
		}
	}
	
	private void handleLogIn(JSONObject o) throws GameIllegalMoveException, JSONException, Exception {
		String	newName = o.getString("username");
		Console.log("Catched client login request for username \"" + newName + "\".");
		
		if(newName.length() < 1 || newName.length() > Constants.USERNAME_MAX_LENGTH) {
			sendException("Invalid username", "The length of an username is required to be between 1 and " + Constants.USERNAME_MAX_LENGTH + " characters. Please try again.", Constants.MESSAGE_INTENT_LOG_IN);
			disconnect();
			return;
		}
		
		for(char c : newName.toCharArray()) {
			if(!Constants.USERNAME_ALLOWED_CHARS.contains(c + "")) {
				sendException("Invalid username", "The character '" + c + "' is not allowed. Please try again.", Constants.MESSAGE_INTENT_LOG_IN);
				disconnect();
				return;
			}
		}
		
		for(Player p : game.state.getPlayers()) {
			if(newName.equalsIgnoreCase(p.name)) {
				sendException("Username taken", "This username is already taken. Please try again.", Constants.MESSAGE_INTENT_LOG_IN);
				disconnect();
				return;
			}
		}
		
		Console.log("Granting client login request...");
		username = newName;
		send(Constants.MESSAGE_INTENT_LOG_IN, o);
		game.state.addPlayer(new Player(username, getRemoteAddress()));
		sendGameState();
	}
	
	/**
	 * Sends the game state to the client via {@link #send(String, JSONObject)}.
	 */
	public void sendGameState() {
		send(Constants.MESSAGE_INTENT_GAME_STATE, game.state.toJSON());
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
		Console.log("Sent " + intent + " packet to client " + socket.getLocalAddress() + " with content " + m.toString() + ".");
	}
	
	public String getRemoteAddress() {
		if(socket == null)
			return null;
		return socket.getRemoteSocketAddress().toString();
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	public void disconnect() throws IOException {
		if(isConnected()) {
			Console.log("Disconnecting from user \"" + username + "\" with address " + getRemoteAddress() + "...");
			isRunning = false;
			interrupt();
			socket.close();
			socket = null;
			if(!game.server.leaveClient(this))
				Console.err("Cannot leave the server.");
			if(!game.state.removePlayer(username))
				Console.err("Cannot leave the game.");
		}
	}

}
