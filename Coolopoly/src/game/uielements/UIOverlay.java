package game.uielements;

import java.awt.Graphics2D;
import java.util.ArrayList;

import game.Display;

public class UIOverlay extends UIElement {
	
	public boolean isHotkeyPopable = true;

	public UIOverlay(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public UIOverlay(float x, float y, float width, float height, ArrayList<UIElement> children) {
		super(x, y, width, height, children);
	}
	
	public void pop() {}

	@Override
	public void paint(Graphics2D g2, Display display) {}

}
