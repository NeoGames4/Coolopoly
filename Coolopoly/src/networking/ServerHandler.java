package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import game.GameIllegalMoveException;
import game.GameState;
import misc.Console;
import misc.Constants;

public class ServerHandler extends Thread {
	
	private GameState gameState;

	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;

	public ServerHandler(Socket socket, InputStream input, OutputStream output) {
		this.socket = socket;
		this.input = new BufferedReader(new InputStreamReader(input));
		this.output = new PrintWriter(output, true);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				JSONObject o = new JSONObject(input.readLine());
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
	
	private void handleException(JSONObject e) {	// TODO
		String message = e.getString("message");
	}
	
	private void handleGameState(JSONObject o) {
		gameState = GameState.fromJSON(o, this);
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public void send(String intent, JSONObject content) {
		JSONObject m = new JSONObject()
				.put("intent", intent)
				.put("content", content);
		
		output.println(m.toString());
		Console.log("Sent " + intent + " packet to client " + socket.getLocalAddress() + " with content " + m.toString() + ".");
	}
	
	public String getRemoteAdress() {
		return socket.getRemoteSocketAddress().toString();
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	public void disconnect() throws IOException {
		if(isConnected()) {
			socket.close();
			socket = null;
		}
	}

}
