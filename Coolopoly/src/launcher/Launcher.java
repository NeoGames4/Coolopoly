package launcher;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import game.Display;
import misc.Console;
import misc.Constants;
import networking.ClientCommunicator;

public class Launcher {
	
	public static BufferedImage getImage(String path) {
		try {
			BufferedImage image = ImageIO.read(Launcher.class.getResource(path.startsWith(".") ? path.substring(1) : path));
			if(image != null) return image;
			throw new Exception("getResource() on getImage(" + path + ") returns null.");
		} catch(Exception e) {
			e.printStackTrace();
			try {
				return ImageIO.read(new File(path));
			} catch(Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}
	}

	public static void main(String[] args) {
		Console.log("Connecting to " + Constants.SERVER_ADRESS + " and port " + Constants.SERVER_PORT + "!");
		try {
			Display display = new Display(new ClientCommunicator());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
