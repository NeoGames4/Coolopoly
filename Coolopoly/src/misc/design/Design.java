package misc.design;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import misc.Tools;

public class Design {
	
	public static BufferedImage getImage(String path) {
		try {
			BufferedImage image = ImageIO.read(Design.class.getResource(path.startsWith(".") ? path.substring(1) : path));
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
	
	// INFO PANEL
	public static final int	INFO_PANEL_HEIGHT = 50;
	
	public static final Font INFO_PANEL_MONEY_FONT = new Font("Monospaced", Font.BOLD, Tools.getFontSize(INFO_PANEL_HEIGHT/2f));
	
	public static final Font INFO_PANEL_MONEY_CHANGE_FONT = new Font("Monospaced", Font.BOLD, Tools.getFontSize(INFO_PANEL_HEIGHT/3f));
	
	public static final Font INFO_PANEL_DEBUG_FONT = new Font("Monospaced", Font.PLAIN, 13);
	
	public static final Color	INFO_PANEL_BACKGROUND_COLOR			= new Color( 50,  50,  75),
								INFO_PANEL_BACKGROUND_ACCENT_COLOR	= new Color(120, 150, 250),
								INFO_PANEL_FOREGROUND_COLOR			= Color.WHITE,
								INFO_PANEL_GREEN_COLOR				= new Color(100, 250, 110),
								INFO_PANEL_RED_COLOR				= new Color(255,  90, 100);

	public static final int INFO_PANEL_MONEY_TRANSITION_DURATON = 1000;
	
	/*static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		Font moneyFont;
		try {
			moneyFont = Font.createFont(Font.TRUETYPE_FONT, new File("A.ttf")).deriveFont(13);
			ge.registerFont(moneyFont);
		} catch (IOException | FontFormatException | NullPointerException e) {
			Console.err("Could not load the info panel money font. (" + e.toString() + ", " + e.getMessage() + ")");
			moneyFont = null;
		}
		
		INFO_PANEL_MONEY_FONT = moneyFont != null ? moneyFont : new Font("Arial", Font.BOLD, 17);
	}*/

}
