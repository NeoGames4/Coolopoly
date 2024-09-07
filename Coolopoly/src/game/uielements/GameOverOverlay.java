package game.uielements;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;

import game.Display;
import game.Display.UI;
import game.GameState;
import game.Player;
import game.Transition;
import misc.Constants;
import misc.Tools;
import misc.design.Design;

public class GameOverOverlay extends UIOverlay {
	
	private Transition opaqueTransition = new Transition(0, 255, 1000);
	
	private boolean pop = false;

	public GameOverOverlay(float x, float y, float width, float height) {
		super(x, y, width, height);
		init();
	}

	public GameOverOverlay(float x, float y, float width, float height, ArrayList<UIElement> children) {
		super(x, y, width, height, children);
		init();
	}
	
	private void init() {
		children.add(new Button("Close", 0, 0, Button.RELATIVE_TO_TOP_RIGHT) {
			@Override
			public void onPressed() {
				pop();
			}
		});
	}
	
	@Override
	public void pop() {
		pop = true;
		opaqueTransition = new Transition(opaqueTransition.getValue(), 0, 1000);
	}

	@Override
	public void paint(Graphics2D g2, Display display) {
		UI ui = display.ui;
		GameState gameState = display.server.getLatestGameState();
		
		if(pop && opaqueTransition.isDone())
			display.overlays.remove(this);
		
		int opaque = (int) opaqueTransition.getValue();
		
		// BACKGROUND
		g2.setColor(Tools.rgbWithOpaque(Design.GAME_OVER_BACKGROUND_COLOR, (int) (Design.GAME_OVER_BACKGROUND_COLOR.getAlpha()*opaque/255.0)));
		g2.fillRect(0, 0, ui.getWidth(), ui.getHeight());
		
		// PANEL
		width	= Math.min(ui.getWidth()  * 3/4, Design.DIALOG_WINDOW_MAX_WIDTH);
		// height	= Math.min(ui.getHeight() * 2/3, Design.DIALOG_WINDOW_MAX_HEIGHT);
		
		x = ui.getWidth()/2 - width/2;
		y = ui.getHeight()/2 - height/2;
		
		g2.setColor(Tools.rgbWithOpaque(Design.GAME_OVER_PANEL_COLOR, opaque));
		g2.fillRoundRect((int) x, (int) y, (int) width, (int) height, Design.GAME_OVER_BORDER_RADIUS, Design.DIALOG_WINDOW_BORDER_RADIUS);
		
		// BORDER
		g2.setStroke(Design.BUTTON_BORDER_STROKE);
		g2.setColor(Tools.rgbWithOpaque(Design.GAME_OVER_PANEL_BORDER_COLOR, opaque));
		
		g2.drawRoundRect((int) x, (int) y, (int) width, (int) height, Design.GAME_OVER_BORDER_RADIUS, Design.GAME_OVER_BORDER_RADIUS);
		
		g2.setStroke(new BasicStroke());
		
		// TITLE
		String titleStr = "Victory!";
		g2.setFont(Design.GAME_OVER_TITLE_FONT);
		int titleY = (int) y + g2.getFontMetrics().getHeight() + Design.GAME_OVER_WINDOW_CONTENT_PADDING/2;
		
		g2.setColor(Tools.rgbWithOpaque(Design.GAME_OVER_FOREGROUND_COLOR, opaque));
		g2.drawString(titleStr, ui.getWidth()/2 - g2.getFontMetrics().stringWidth(titleStr)/2, titleY);
		
		// INFO TEXT
		g2.setFont(Design.GAME_OVER_TEXT_FONT);
		
		int latestLineY = titleY + Design.GAME_OVER_WINDOW_CONTENT_PADDING;
		
		ArrayList<Player> playerRanking = gameState.getPlayers();
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
		}
		
		// OPTIONS
		int maxButtonHeight = 0;
		if(opaqueTransition.isDone()) {
			int optionsXOffset = 0;
			for(UIElement c : children) {
				c.x = (int) x + Design.DIALOG_WINDOW_CONTENT_PADDING + optionsXOffset;
				c.y = latestLineY + Design.DIALOG_WINDOW_CONTENT_PADDING;
				
				optionsXOffset += c.width + Design.DIALOG_WINDOW_CONTENT_PADDING;
				
				c.paint(g2, display);
				
				if(c.height > maxButtonHeight)
					maxButtonHeight = (int) c.height;
			}
		}
		
		height = (latestLineY-y) + 2 * Design.DIALOG_WINDOW_CONTENT_PADDING + maxButtonHeight;
		height = ((int) (height/10))*10f;
	}

}
