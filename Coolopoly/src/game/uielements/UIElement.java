package game.uielements;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import game.Display;
import game.Display.UI;

public abstract class UIElement {
	
	public float x;
	public float y;
	public float width;
	public float height;
	
	public final ArrayList<UIElement> children;
	
	public UIElement(float x, float y, float width, float height) {
		this(x, y, width, height, new ArrayList<>());
	}
	
	public UIElement(float x, float y, float width, float height, ArrayList<UIElement> children) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.children = children;
	}
	
	public boolean isMouseHovering(MouseEvent e, UI ui) {
		if(e.getX() >= x && e.getX() <= x+width) {
			return e.getY() >= y && e.getY() <= y+height;
		} return false;
	}
	
	public void onMouseEntered() {}
	
	public void onMouseExited() {}
	
	public void onMousePressed() {}
	
	public void onMouseReleased() {}
	
	public abstract void paint(Graphics2D g2, Display display);

}
