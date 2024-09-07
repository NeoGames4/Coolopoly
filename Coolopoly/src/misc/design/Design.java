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
	
	public static final int		INFO_PANEL_HEIGHT = 60;
	
	public static final Font	INFO_PANEL_MONEY_FONT			= new Font("Monospaced", Font.BOLD, 21),
								INFO_PANEL_MONEY_CHANGE_FONT	= new Font("Monospaced", Font.BOLD, 18),
								INFO_PANEL_PLAYER_LIST_FONT		= new Font("Monospaced", Font.BOLD, 16),
								INFO_PANEL_TOOLTIP_FONT			= new Font("Monospaced", Font.BOLD, 18),
								INFO_PANEL_NOTIFICATIONS_FONT	= new Font("Monospaced", Font.BOLD, 15);
	
	public static final Color	INFO_PANEL_BACKGROUND_COLOR			= new Color( 50,  50,  75),
								INFO_PANEL_BACKGROUND_ACCENT_COLOR	= new Color(120, 150, 250),
								INFO_PANEL_FOREGROUND_COLOR			= Color.WHITE,
								INFO_PANEL_PLAYERS_FOREGROUND_COLOR	= Color.WHITE,
								INFO_PANEL_PLAYERS_HIGHLIGHT_COLOR	= Color.ORANGE,
								INFO_PANEL_GREEN_COLOR				= new Color(100, 250, 110),
								INFO_PANEL_RED_COLOR				= new Color(255,  90, 100),
								INFO_PANEL_TOOLTIP_FOREGROUND		= Color.WHITE,
								INFO_PANEL_TOOLTIP_BACKGROUND		= Color.BLACK,
								INFO_PANEL_DICE_COLOR				= new Color(255, 255, 255, 200),
								INFO_PANEL_DICE_BACKGROUND_COLOR	= new Color( 50,  50,  75, 175),
								INFO_PANEL_DICE_BORDER_COLOR		= new Color(120, 150, 250, 175),
								INFO_PANEL_NOTIFICATIONS_TEXT_COLOR	= new Color(255, 255, 255),
								INFO_PANEL_NOTIFICATIONS_BOX_COLOR	= new Color(  0,   0,   0, 175);

	public static final int		INFO_PANEL_MONEY_TRANSITION_DURATON		= 1000,
								INFO_PANEL_TOOLTIP_TRANSITION_DURATION	= 350,
								INFO_PANEL_DICE_TRANSITION_DURATION		= 225,
								INFO_PANEL_NOTIFICATIONS_FADE_DURATION	= 200,
								INFO_PANEL_NOTIFICATIONS_MOVE_DURATION	= 250,
								
								INFO_PANEL_DICE_PADDING					= 6,
								INFO_PANEL_DICE_CORNER_RADIUS			= 5;
	
	public static final Stroke	INFO_PANEL_DICE_BORDER_STROKE		= new BasicStroke(3);
	
	
	// DIALOG WINDOW
	
	public static final Color	DIALOG_WINDOW_BACKGROUND_COLOR		= new Color(  0,   0,   0, 170),
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
	
	
	// MANAGE PROPERTIES OVERLAY
	
	public static final int		PROPERTIES_COLUMNS				= 8,
								PROPERTIES_ROWS					= 5,
								
								PROPERTIES_CONTENT_PADDING		= 10,
								PROPERTIES_VERTICAL_MARGIN		= Design.INFO_PANEL_HEIGHT,
								PROPERTIES_HORIZONTAL_MARGIN	= 15,
								
								PROPERTIES_CARD_MIN_WIDTH	= 75,
								PROPERTIES_CARD_TITLE_FONT_MIN_SIZE = 5,
								PROPERTIES_CARD_TEXT_FONT_MIN_SIZE = 4,
								PROPERTIES_CARD_BORDER_RADIUS = 10,
								PROPERTIES_CARD_TITLE_ANIMATION_Y_OFFSET = -30,
								PROPERTIES_CARD_ANIMATION_X_OFFSET = 20,
								
								PROPERTIES_WINDOW_FADE_DURATION = 300;
	
	public static final float	PROPERTIES_CARD_WIDTH_HEIGHT_RATIO = 2/3f;
	
	public static final Color	PROPERTIES_BACKGROUND_COLOR			= new Color(  0,   0,   0, 170),
								PROPERTIES_PANEL_BACKGROUND_COLOR	= new Color(  0,  60, 200),
								PROPERTIES_CARD_BACKGROUND_COLOR	= new Color( 20, 100, 230),
								PROPERTIES_CARD_MORTGAGE_BACKGROUND_COLOR = new Color(50, 50, 50),
								PROPERTIES_CARD_FOREGROUND_COLOR	= Color.WHITE;
	
	public static final Font	PROPERTIES_CARD_TITLE_FONT		= new Font("Monospaced", Font.BOLD, 16),
								PROPERTIES_CARD_STREET_FONT		= new Font("Monospaced", Font.BOLD, 14),
								PROPERTIES_CARD_INFO_FONT		= new Font("Monospaced", Font.PLAIN, 14);
	
	
	// GAME OVER WINDOW
	
	public static final Color	GAME_OVER_BACKGROUND_COLOR			= new Color(  0,   0,   0, 170),
								GAME_OVER_PANEL_COLOR				= new Color(200,  50, 255),
								GAME_OVER_PANEL_BORDER_COLOR		= new Color(255, 100, 255),
								GAME_OVER_FOREGROUND_COLOR			= Color.WHITE;
	
	public static final Stroke	GAME_OVER_BORDER_STROKE		= new BasicStroke(5);
	
	public static final int		GAME_OVER_BORDER_RADIUS		= 15;
	
	public static final Font	GAME_OVER_TITLE_FONT		= new Font("Monospaced", Font.BOLD, 24),
								GAME_OVER_TEXT_FONT			= new Font("Monospaced", Font.BOLD, 17);
	
	public static final int		GAME_OVER_WINDOW_CONTENT_PADDING	= 10;
	
	
	// BUTTONS
	
								// DEFAULT
	public static final Color	BUTTON_DEFAULT_BACKGROUND_COLOR				= new Color( 35,  55, 240),
								BUTTON_DEFAULT_FOREGROUND_COLOR				= new Color(255, 255, 255),
								BUTTON_DEFAULT_BORDER_COLOR					= new Color( 55, 115, 245),
								// HOVER
								BUTTON_DEFAULT_HOVER_BACKGROUND_COLOR		= new Color( 50,  80, 255),
								BUTTON_DEFAULT_HOVER_FOREGROUND_COLOR		= new Color(255, 255, 255),
								BUTTON_DEFAULT_HOVER_BORDER_COLOR			= new Color(110, 130, 255),
								// CLICK
								BUTTON_DEFAULT_CLICK_BACKGROUND_COLOR		= new Color( 20,  50, 205),
								BUTTON_DEFAULT_CLICK_FOREGROUND_COLOR		= new Color(255, 255, 255),
								BUTTON_DEFAULT_CLICK_BORDER_COLOR			= new Color( 50,  80, 240),
								// DISABLED
								BUTTON_DEFAULT_DISABLED_BACKGROUND_COLOR	= new Color(100, 100, 100),
								BUTTON_DEFAULT_DISABLED_FOREGROUND_COLOR	= new Color(255, 255, 255),
								BUTTON_DEFAULT_DISABLED_BORDER_COLOR		= new Color(140, 140, 140),
								// SELECTED
								BUTTON_DEFAULT_SELECTED_BACKGROUND_COLOR	= new Color(  0,  20, 150),
								BUTTON_DEFAULT_SELECTED_FOREGROUND_COLOR	= new Color(220, 220, 220),
								BUTTON_DEFAULT_SELECTED_BORDER_COLOR		= new Color(130, 130, 130);
	
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
																.withClickBorderColor		(new Color( 50, 180,  60))
																.withSelectedBackgroundColor(new Color(  0, 100,  20)),
									BUTTON_RED_STYLE		= new ButtonStyle()
																.withBackgroundColor		(new Color(205,  45,  40))
																.withBorderColor			(new Color(235,  85,  80))
																.withHoverBackgroundColor	(new Color(240,  55,  50))
																.withHoverBorderColor		(new Color(255, 115, 115))
																.withClickBackgroundColor	(new Color(180,  40,  30))
																.withClickBorderColor		(new Color(215,  75,  70))
																.withSelectedBackgroundColor(new Color(150,  30,  35));
	
	
	// BOARD
	
	public static final int		BOARD_HIGHLIGHT_FIELDS_STROKE_WIDTH = 5;
	
	public static final long	BOARD_HIGHLIGHT_TRANSITION_DURATION = 400,
								BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION = 500;
	
	public static final Color	BOARD_HIGHLIGHT_OWN_PROPERTY_PRIMARY_COLOR		= new Color(255, 255,  50),
								BOARD_HIGHLIGHT_OWN_PROPERTY_SECONDARY_COLOR	= new Color(220, 255, 200),
								
								BOARD_HIGHLIGHT_SOLD_PROPERTY_PRIMARY_COLOR		= new Color(255, 110, 100),
								BOARD_HIGHLIGHT_SOLD_PROPERTY_SECONDARY_COLOR	= new Color(255, 200, 180);
	
	public static final float	BOARD_PLAYER_SIZE_RATIO		= 120/3000f,
								BOARD_HOUSE_SIZE_RATIO		=  62/3000f,
								BOARD_HOUSE_SLOT_SIZE_RATIO	=  74/3000f;
	
	public static final BufferedImage	BOARD_HOTEL_IMAGE	= Design.getImage("server-station-icon.png"),
										BOARD_HOUSE_IMAGE	= Design.getImage("server-icon.png");
	
	
	// CAMERA
	
	public static final double	CAMERA_PLAYER_TRANSITION_DURATION	= 2.0;		// per pixel distance
	
	
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
