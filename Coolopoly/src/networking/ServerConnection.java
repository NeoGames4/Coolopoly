package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import game.GameIllegalMoveException;
import game.GameState;
import game.uielements.OptionPane;
import misc.Console;
import misc.Constants;

public class ServerConnection extends Thread {
	
	private String username = null;
	private boolean waitingForLogin;
	
	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;
	
	private GameState serverGameState;
	
	private boolean isRunning = true;

	public ServerConnection(String username, String address, int port) throws UnknownHostException, IOException {
		connect(username, address, port);
	}
	
	public ServerConnection connect(String username, String address, int port) throws UnknownHostException, IOException {
		if(!isConnected()) {
			socket = new Socket(address, port);
			input	= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output	= new PrintWriter(socket.getOutputStream(), true);
			
			start();
			
			JSONObject r = new JSONObject().put("username", username);
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
					case Constants.MESSAGE_INTENT_MISC:
						// TODO
						break;
					default:
						throw new Exception("Invalid intent \"" + o.getString("intent") + "\".");
				}
			} catch(GameIllegalMoveException e) {
				Console.err(getRemoteAdress() + " tried to move, but it is somebody else’s turn.");
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
			// TODO
		
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
	
	private void handleException(JSONObject e) {
		String title = e.getString("title");
		String message = e.getString("message");
		String causingIntent = e.optString("intent");
				
		OptionPane.sendExceptionPanel(title + "(" + causingIntent.toUpperCase() +  ")", message);
		
		if(causingIntent.equals(Constants.MESSAGE_INTENT_LOG_IN) && waitingForLogin) {
			waitingForLogin = false;
		}
	}
	
	private void handleGameState(JSONObject o) {
		serverGameState = GameState.fromJSON(o, this);
	}
	
	public void send(String intent, JSONObject content) {
		JSONObject m = new JSONObject()
				.put("intent", intent)
				.put("content", content);
		
		output.println(m.toString());
		Console.log("Sent " + intent + " packet to client " + socket.getLocalAddress() + " with content " + m.toString() + ".");
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
	
	public String getRemoteAdress() {
		return socket.getRemoteSocketAddress().toString();
	}

}
