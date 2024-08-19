package misc.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import game.uielements.ButtonStyle;
import misc.Tools;

public class Design {
	
	// INFO PANEL
	
	public static final int		INFO_PANEL_HEIGHT = 50;
	
	public static final Font	INFO_PANEL_MONEY_FONT = new Font("Monospaced", Font.BOLD, 20),
								INFO_PANEL_MONEY_CHANGE_FONT = new Font("Monospaced", Font.BOLD, 17),
								INFO_PANEL_DEBUG_FONT = new Font("Monospaced", Font.PLAIN, 13);
	
	public static final Color	INFO_PANEL_BACKGROUND_COLOR			= new Color( 50,  50,  75),
								INFO_PANEL_BACKGROUND_ACCENT_COLOR	= new Color(120, 150, 250),
								INFO_PANEL_FOREGROUND_COLOR			= Color.WHITE,
								INFO_PANEL_GREEN_COLOR				= new Color(100, 250, 110),
								INFO_PANEL_RED_COLOR				= new Color(255,  90, 100);

	public static final int		INFO_PANEL_MONEY_TRANSITION_DURATON = 1000;
	
	
	// DIALOG WINDOW
	
	public static final Color	DIALOG_WINDOW_BACKGROUND_COLOR		= new Color(  0,   0,   0, 100),
								DIALOG_WINDOW_PANEL_COLOR			= new Color( 20,  50,  20),
								DIALOG_WINDOW_BORDER_COLOR			= new Color(180, 190, 170),
								DIALOG_WINDOW_FOREGROUND_COLOR		= new Color(  0, 255,   0);
	
	public static final Stroke	DIALOG_WINDOW_BORDER_STROKE	= new BasicStroke(5);
	
	public static final int		DIALOG_WINDOW_BORDER_RADIUS	= 15;
	
	public static final Font	DIALOG_WINDOW_TITLE_FONT	= new Font("Monospaced", Font.BOLD, 17),
								DIALOG_WINDOW_MESSAGE_FONT	= new Font("Monospaced", Font.ITALIC, 15);
	
	public static final int		DIALOG_WINDOW_CONTENT_PADDING	= 10,
								DIALOG_WINDOW_MAX_WIDTH			= 800,
								DIALOG_WINDOW_MAX_HEIGHT		= 400;
	
	
	// BUTTONS
	
								// DEFAULT
	public static final Color	BUTTON_DEFAULT_BACKGROUND_COLOR			= new Color( 35,  55, 240),
								BUTTON_DEFAULT_FOREGROUND_COLOR			= new Color(255, 255, 255),
								BUTTON_DEFAULT_BORDER_COLOR				= new Color( 55, 115, 245),
								// HOVER
								BUTTON_DEFAULT_HOVER_BACKGROUND_COLOR	= new Color( 50,  80, 255),
								BUTTON_DEFAULT_HOVER_FOREGROUND_COLOR	= new Color(255, 255, 255),
								BUTTON_DEFAULT_HOVER_BORDER_COLOR		= new Color(110, 130, 255),
								// CLICK
								BUTTON_DEFAULT_CLICK_BACKGROUND_COLOR	= new Color( 20,  50, 205),
								BUTTON_DEFAULT_CLICK_FOREGROUND_COLOR	= new Color(255, 255, 255),
								BUTTON_DEFAULT_CLICK_BORDER_COLOR		= new Color( 50,  80, 240);
	
	public static final Font	BUTTON_LABEL_FONT	 = new Font("Monospaced", Font.BOLD, 15);
	
	public static final int		BUTTON_LABEL_HORIZONTAL_PADDING = 10,
								BUTTON_LABEL_VERTICAL_PADDING	= 6,
								BUTTON_BORDER_RADIUS			= 15;
	
	public static final Stroke	BUTTON_BORDER_STROKE = new BasicStroke(3);
	
	public static final float	BUTTON_ICON_MAX_WIDTH_RATIO		= 0.75f,
								BUTTON_ICON_MAX_HEIGHT_RATIO	= 0.75f;
	
	public static final ButtonStyle BUTTON_STANDARD_STYLE	= new ButtonStyle(),
									BUTTON_GREEN_STYLE		= new ButtonStyle()
																.withBackgroundColor		(new Color(  0, 170,  55))
																.withBorderColor			(new Color( 80, 200,  90))
																.withHoverBackgroundColor	(new Color( 40, 195,  50))
																.withHoverBorderColor		(new Color(120, 220, 125))
																.withClickBackgroundColor	(new Color(  0, 145,  45))
																.withClickBorderColor		(new Color( 50, 180,  60)),
									BUTTON_RED_STYLE		= new ButtonStyle()
																.withBackgroundColor		(new Color(205,  45,  40))
																.withBorderColor			(new Color(235,  85,  80))
																.withHoverBackgroundColor	(new Color(240,  55,  50))
																.withHoverBorderColor		(new Color(255, 115, 115))
																.withClickBackgroundColor	(new Color(180,  40,  30))
																.withClickBorderColor		(new Color(215,  75,  70));
	
	
	// BOARD
	
	public static final int		BOARD_HIGHLIGHT_FIELDS_STROKE_WIDTH = 5;
	
	public static final long	BOARD_HIGHLIGHT_TRANSITION_DURATION = 400,
								BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION = 300;
	
	public static final Color	BOARD_HIGHLIGHT_FLASH_COLOR = new Color(200, 255, 220),
								BOARD_HIGHLIGHT_ACCENT_FLASH_COLOR = new Color(100, 250, 140);
	
	public static final float	BOARD_PLAYER_SIZE_RATIO = 120/3000f;
	
	
	// OTHERS
	
	public static final BufferedImage IMAGE_NOT_FOUND = getImage("not-found.png");
	
	
	// METHODS
	
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
	
	public static BufferedImage inkImage(BufferedImage image, Color color) {
		if(image == null)
			return null;
		
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y<image.getHeight(); y++) {
			for(int x = 0; x<image.getWidth(); x++) {
				int alpha = new Color(image.getRGB(x, y), true).getAlpha();
				if(alpha != 0)
					result.setRGB(x, y, Tools.rgbWithOpaque(color, alpha).getRGB());
				else
					result.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
			}
		}
		return result;
		
	}
	
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
