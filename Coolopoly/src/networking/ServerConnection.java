package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import game.Display;
import game.GameIllegalMoveException;
import game.GameState;
import game.properties.Properties;
import game.uielements.DialogWindow;
import game.uielements.DialogWindowOption;
import game.uielements.GameOverOverlay;
import game.uielements.OptionPane;
import game.uielements.UIElement;
import misc.Console;
import misc.Constants;

public class ServerConnection extends Thread {
	
	public Display display;
	
	public final String address;
	public final int port;
	
	private String username = null;
	private boolean waitingForLogin;
	
	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;
	
	private GameState serverGameState;
	
	private boolean isRunning = true;

	public ServerConnection(String username, String address, int port, boolean justWatching) throws UnknownHostException, ConnectException, IOException {
		this.address = address;
		this.port = port;
		connect(username, justWatching);
	}
	
	public ServerConnection connect(String username, boolean justWatching) throws UnknownHostException, ConnectException, IOException {
		if(!isConnected()) {
			socket = new Socket(address, port);
			input	= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output	= new PrintWriter(socket.getOutputStream(), true);
			
			start();
			
			JSONObject r = new JSONObject().put("username", username).put("just_watching", justWatching);
			waitingForLogin = true;
			send(Constants.MESSAGE_INTENT_LOG_IN, r);
		} else throw new RuntimeException("This client is already connected. Please run disconnect() first.");
		return this;
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	public void disconnect() throws IOException {
		if(isConnected()) {
			Console.log("Disconnecting from the server...");
			isRunning = false;
			interrupt();
			socket.close();
			socket = null;
		}
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
					case Constants.MESSAGE_INTENT_GAME_STATE:
						handleGameState(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_EXCEPTION:
						handleException(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_ACTION:
						handleAction(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_LOG_IN:
						handleLogin(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_PROPERTIES:
						handleProperties(o.getJSONObject("content"));
						break;
					case Constants.MESSAGE_INTENT_MISC:
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
		switch(o.getString("action")) {
			default:
				throw new Exception("Invalid intent \"" + o.getString("intent") + "\".");
		}
	}
	
	private void handleLogin(JSONObject o) throws GameIllegalMoveException, JSONException, Exception {
		if(!waitingForLogin)
			return;
		
		username = o.getString("username");
		waitingForLogin = false;
	}
	
	private void handleProperties(JSONObject o) throws JSONException {
		Properties.loadProperties(o);
	}
	
	private void handleException(JSONObject e) {
		String title = e.optString("title");
		String message = e.getString("message");
		String causingIntent = e.optString("intent");
		
		if(display != null)
			display.dialogs.add(
				new DialogWindow(
					title != null && !title.isEmpty() ? title + " (" + causingIntent.toUpperCase() +  ")" : "400 Bad Request",
					message,
					new DialogWindowOption[] {DialogWindowOption.getDiscardOption("OK", display.dialogs)}
				)
			);
		else
			OptionPane.sendExceptionPanel(title + "(" + causingIntent.toUpperCase() +  ")", message);
						
		if(causingIntent.equals(Constants.MESSAGE_INTENT_LOG_IN))
			waitingForLogin = false;
	}
	
	private void handleGameState(JSONObject o) {
		boolean wasAlive = serverGameState != null && serverGameState.getPlayer(username) != null;
		
		serverGameState = GameState.fromJSON(o, this);
		
		if(serverGameState.cardEffect != null && serverGameState.currentPlayer().name.equals(username))
			serverGameState.cardEffect.openDialog(display);
		
		if(serverGameState.getPlayer(username) == null && wasAlive) {
			display.dialogs.add(
				new DialogWindow(
					"507 Insufficient Storage",
					"It seems like you ran out of storage. This prevents you from installing any more environments. However, you can still watch. Your environments are released for others.",
					new DialogWindowOption[] {
						DialogWindowOption.getDiscardOption("OK", display.dialogs)
					}
				)
			);
		}
		
		if(serverGameState.isOver()) {
			for(UIElement e : display.overlays) {
				if(e instanceof GameOverOverlay)
					return;
			}
			display.overlays.add(new GameOverOverlay(0, 0, 0, 0));
		}
	}
	
	public void vote(String voteSubject, boolean voteState) {
		JSONObject o = new JSONObject()
						.put("subject", voteSubject)
						.put("state", voteState);
		
		send(Constants.MESSAGE_INTENT_VOTE, o);
	}
	
	public void requestGameState() {
		send(Constants.MESSAGE_INTENT_GAME_STATE, new JSONObject());
	}
	
	public void send(String intent, JSONObject content) {
		if(socket == null) {
			OptionPane.sendExceptionPanel("Server offline", "The server is no longer reachable.");
			System.exit(0);
		}
		
		JSONObject m = new JSONObject()
				.put("intent", intent)
				.put("content", content);
		
		output.println(m.toString());
		Console.log("Sent " + intent + " packet to server with content " + m.toString() + ".");
	}
	
	public GameState getLatestGameState() {
		return serverGameState;
	}
	
	public static final int	LOGIN_SUCCESSFULL	= 0,
							LOGIN_PENDING		= 1,
							LOGIN_FAILED		= 2;
	
	public int getLoginState() {
		return waitingForLogin ? 1 : username == null ? 2 : 0;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getRemoteAddress() {
		return socket.getRemoteSocketAddress().toString();
	}

}
