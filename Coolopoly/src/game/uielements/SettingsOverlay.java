package game.uielements;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Display;
import game.Display.UI;
import game.Transition;
import misc.Tools;
import misc.design.Design;
import sound.SoundManager;

public class SettingsOverlay extends UIOverlay {

	private Transition opaqueTransition = new Transition(0, 255, Design.OVERLAY_WINDOW_DEFAULT_FADE_DURATION);
	
	private final Button	soundButton,
							closeButton;
	
	private static final BufferedImage[] SOUND_BUTTON_IMAGES = {
		Design.getImage("music-loud-icon.png"),
		Design.getImage("music-on-icon.png"),
		Design.getImage("music-silent-icon.png")
	};
	
	private boolean pop = false;

	public SettingsOverlay() {
		super(0, 0, 10, 10);
		
		closeButton = new Button("Close", 0, 0, Button.RELATIVE_TO_TOP_RIGHT) {
			@Override
			public void onPressed() {
				pop();
			}
		};
		children.add(closeButton);
		
		soundButton = new Button(0, 0, Design.INFO_PANEL_HEIGHT-12, Design.INFO_PANEL_HEIGHT-12) {
			@Override
			public void onPressed() {
				int soundMode = SoundManager.toggleSoundMode();
				withIcon(SOUND_BUTTON_IMAGES.length > soundMode ? SOUND_BUTTON_IMAGES[soundMode] : Design.IMAGE_NOT_FOUND);
			}
		}.withIcon(SOUND_BUTTON_IMAGES.length > SoundManager.getSoundMode() ? SOUND_BUTTON_IMAGES[SoundManager.getSoundMode()] : Design.IMAGE_NOT_FOUND);
		children.add(soundButton);
	}
	
	@Override
	public void pop() {
		pop = true;
		opaqueTransition = new Transition(opaqueTransition.getValue(), 0, Design.OVERLAY_WINDOW_DEFAULT_FADE_DURATION);
	}

	@Override
	public void paint(Graphics2D g2, Display display) {
		UI ui = display.ui;
		
		if(pop && opaqueTransition.isDone())
			display.overlays.remove(this);
		
		int opaque = (int) opaqueTransition.getValue();
		
		// BACKGROUND
		g2.setColor(Tools.rgbWithOpaque(Design.SETTINGS_BACKGROUND_COLOR, (int) (Design.SETTINGS_BACKGROUND_COLOR.getAlpha()*opaque/255.0)));
		g2.fillRect(0, 0, ui.getWidth(), ui.getHeight());
		
		// PANEL
		width = Math.min(ui.getWidth() * 3/4, Design.DIALOG_WINDOW_MAX_WIDTH);
		
		x = ui.getWidth()/2 - width/2;
		y = ui.getHeight()/2 - height/2;
		
		g2.setColor(Tools.rgbWithOpaque(Design.SETTINGS_PANEL_COLOR, opaque));
		g2.fillRoundRect((int) x, (int) y, (int) width, (int) height, Design.SETTINGS_BORDER_RADIUS, Design.DIALOG_WINDOW_BORDER_RADIUS);
		
		// BORDER
		g2.setStroke(Design.BUTTON_BORDER_STROKE);
		g2.setColor(Tools.rgbWithOpaque(Design.SETTINGS_PANEL_BORDER_COLOR, opaque));
		
		g2.drawRoundRect((int) x, (int) y, (int) width, (int) height, Design.SETTINGS_BORDER_RADIUS, Design.SETTINGS_BORDER_RADIUS);
		
		g2.setStroke(new BasicStroke());
		
		// TITLE
		String titleStr = "Settings";
		g2.setFont(Design.SETTINGS_TITLE_FONT);
		int titleY = (int) y + g2.getFontMetrics().getHeight() + Design.SETTINGS_WINDOW_CONTENT_PADDING/2;
		
		g2.setColor(Tools.rgbWithOpaque(Design.SETTINGS_FOREGROUND_COLOR, opaque));
		g2.drawString(titleStr, ui.getWidth()/2 - g2.getFontMetrics().stringWidth(titleStr)/2, titleY);
		
		// INFO TEXT
		g2.setFont(Design.SETTINGS_TEXT_FONT);
		
		int latestLineY = titleY + Design.SETTINGS_WINDOW_CONTENT_PADDING;
		
		/*ArrayList<Player> playerRanking = gameState.getPlayers();
		playerRanking.sort((a, b) -> {
			int	aw = a.getTotalWorth(),
				bw = b.getTotalWorth();
			
			return aw < bw
					? 1
					: (aw > bw ? -1 : 0);
		});
		
		int pRank = 1;
		for(int i = 0; i<playerRanking.size(); i++) {
			Player p = playerRanking.get(i);
			
			if(i > 0 && playerRanking.get(i-1).getTotalWorth() > p.getTotalWorth())
				pRank++;
			
			g2.drawString("#" + pRank + " " + p.name + " – owns " + p.properties.size() + " fields – total worth: " + p.getTotalWorth() + " " + Constants.MONEY_UNIT, (int) x + Design.DIALOG_WINDOW_CONTENT_PADDING, latestLineY = titleY + (i+1) * g2.getFontMetrics().getHeight());
		}*/
		
		// OPTIONS
		if(opaqueTransition.isDone() && opaqueTransition.getValue() > 0) {
			soundButton.x = x + width/2 - soundButton.width/2;
			soundButton.y = latestLineY + Design.DIALOG_WINDOW_CONTENT_PADDING;
			latestLineY += soundButton.height;
			
			soundButton.paint(g2, display);
			
			closeButton.x = (int) x + Design.DIALOG_WINDOW_CONTENT_PADDING;
			closeButton.y = latestLineY + Design.DIALOG_WINDOW_CONTENT_PADDING;
			latestLineY += closeButton.height;
			
			closeButton.paint(g2, display);
			
		}
		
		height = (latestLineY-y) + 2 * Design.DIALOG_WINDOW_CONTENT_PADDING;
		height = ((int) (height/10))*10f;
	}

}
