package game;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

public abstract class UIElement {
	
	public float x;
	public float y;
	public float width;
	public float height;
	
	Queue<UIElement> children;
	
	public UIElement(float x, float y, float width, float height) {
		this(x, y, width, height, new LinkedList<>());
	}
	
	public UIElement(float x, float y, float width, float height, Queue<UIElement> children) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.children = children;
	}
	
	public boolean isMouseHovering(MouseEvent e) {
		if(e.getX() >= x && e.getX() <= x+width) {
			return e.getY() >= y && e.getY() <= y+height;
		} return false;
	}
	
	public boolean isPressed(MouseEvent e) {
		return e.getClickCount() > 1 && isMouseHovering(e);
	}
	
	public abstract void paint(Graphics2D g2, Display display);

}
