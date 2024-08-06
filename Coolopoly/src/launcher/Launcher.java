package launcher;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import game.Display;
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
		// Display display = new Display();
		ClientCommunicator communicator = new ClientCommunicator();
		System.out.println("Connecting to " + Constants.SERVER_ADRESS + " and port " + Constants.SERVER_PORT + "!");
		try {
			communicator.connect();
			System.out.println("Connected successfully!");
			while(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
