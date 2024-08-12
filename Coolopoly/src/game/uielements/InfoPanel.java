package game.uielements;

import java.awt.Color;
import java.awt.Graphics2D;

import game.Display;

public class InfoPanel extends UIElement {
	
	public static final Color	BACKGROUND_COLOR = new Color(50, 50, 75),
								BACKGROUND_ACCENT_COLOR = new Color(120, 150, 250),
								FOREGROUND_COLOR = Color.WHITE;

	public InfoPanel() {
		super(0, 0, 0, 100);
	}

	@Override
	public void paint(Graphics2D g2, Display display) {
		// BACKGROUND
		g2.setColor(BACKGROUND_COLOR);
		g2.fillRect(0, display.getHeight()-(int) height, display.getWidth(), (int) height);
		g2.setColor(BACKGROUND_ACCENT_COLOR);
		g2.fillRect(0, display.getHeight()-(int) height-5, display.getWidth(), 5);
		g2.setColor(FOREGROUND_COLOR);
		g2.fillRect(0, display.getHeight()-(int) height-3, display.getWidth(), 1);
		
		// MONEY
	}

}
