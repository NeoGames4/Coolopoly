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

import game.Constants;
import game.Game;
import game.GameIllegalMoveException;
import launcher.Console;

public class ClientHandler extends Thread {
	
	private final Game game;

	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;

	public ClientHandler(Game game, Socket socket, InputStream input, OutputStream output) {
		this.game = game;
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
		if(!game.state.currentPlayer().remoteAdress.equals(getRemoteAdress()))
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
	
	public void sendGameState() {
		send(Constants.MESSAGE_INTENT_GAME_STATE, game.state.toJSON());
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
