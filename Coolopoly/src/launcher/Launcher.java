package launcher;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import game.Display;
import game.uielements.OptionPane;
import game.uielements.OptionPaneResponse;
import misc.Console;
import misc.Constants;
import networking.ServerConnection;

public class Launcher {

	public static void main(String[] args) {
		ServerConnection connection;
		
		session:
		while(true) {
			String username	= "";
			String serverIp	= Constants.INIT_SERVER_ADDRESS;
			int serverPort	= Constants.INIT_SERVER_PORT;
			String lastServerField = serverIp + ":" + serverPort;
			boolean justWatching = false;
			
			dialog:
			while(true) {
				OptionPaneResponse loginInfo = OptionPane.sendLoginPanel(username, lastServerField);
				
				if(loginInfo.result != 0)
					System.exit(0);
				
				username 		= loginInfo.fields.get("username").trim();
				lastServerField	= loginInfo.fields.get("address");
				justWatching	= loginInfo.fields.get("just_watching").equals("yes");
				String[] address = lastServerField.split(":");
			
				try {				
					if(address.length < 2 || address[0].length() < 1 || address[1].length() < 1)
						throw new Exception("Invalid server address. Please separate the server IP and the port by a colon.");
					
					serverIp = address[0];
					
					try {
						serverPort = Integer.parseInt(address[1]);
					} catch(NumberFormatException e) {
						throw new Exception("Invalid port. The port has to be a number.");
					}
				} catch(Exception e) {
					OptionPane.sendExceptionPanel("Login Error", e.getMessage());
					continue dialog;
				}
				
				break dialog;
			}
			
			Console.log("Connecting as \"" + username + "\" to " + serverIp + " and port " + serverPort + "...");
			
			try {
				connection = new ServerConnection(username, serverIp, serverPort, justWatching);
				
				Console.log("Waiting for the response...");
				long now = System.currentTimeMillis();
				
				pending:
				while(connection.getLoginState() == ServerConnection.LOGIN_PENDING) {
					if(System.currentTimeMillis() - now > Constants.LOGIN_TIMEOUT) {
						OptionPane.sendExceptionPanel("Timeout", "The server did not response within " + Constants.LOGIN_TIMEOUT + " milliseconds.");
						break pending;
					}
				}
				
				Console.log("Reached login state " + connection.getLoginState() + ".");
				
				if(connection.getLoginState() == ServerConnection.LOGIN_SUCCESSFULL)
					break session;
			} catch (UnknownHostException | ConnectException e) {
				OptionPane.sendExceptionPanel("Cannot connect", "Either the host is unknown or the host refused to connect with the client.\nPlease check the server address.");
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		Console.log("Launching display...");
		
		new Display(connection);
	}

}
