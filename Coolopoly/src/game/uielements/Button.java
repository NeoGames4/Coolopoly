package game.uielements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Display;
import game.Display.UI;
import game.Transition;
import misc.design.Design;

public class Button extends UIElement {
	
	// STYLE
	
	private ButtonStyle style = new ButtonStyle();
	
	private BufferedImage	icon,
							iconHover,
							iconClick;
	
	public String			label;
	
	// TRANSITIONS
	
	private Transition	trXOffset = new Transition(0, 0, 10),	// x position
						trYOffset = new Transition(0, 0, 10),	// y position
						trWOffset = new Transition(0, 0, 10),	// width
						trHOffset = new Transition(0, 0, 10),	// height
						
						trColorROffset = new Transition(0, style.backgroundColor.getRed(),   10),	// background color red
						trColorGOffset = new Transition(0, style.backgroundColor.getGreen(), 10),	// background color green
						trColorBOffset = new Transition(0, style.backgroundColor.getBlue(),  10);	// background color blue
	
	private final static long TRANSITION_DURATION = 50;			// in ms
	
	// BUTTON STATE
	
	private boolean hovering	= false;	// mouse is hovering over the button
	private boolean pressed		= false;	// mouse is pressing the button
	
	// RELATIVE CORNER
	
	public static final int RELATIVE_TO_TOP_LEFT		= 0,
							RELATIVE_TO_TOP_RIGHT		= 1,
							RELATIVE_TO_BOTTOM_LEFT		= 2,
							RELATIVE_TO_BOTTOM_RIGHT	= 3,
							RELATIVE_TO_TOP_CENTER		= 4,
							RELATIVE_TO_BOTTOM_CENTER	= 5;
	
	private int relativeTo = RELATIVE_TO_TOP_LEFT;
	
	
	// CONSTRUCTORS

	public Button(int x, int y, int width, int height) {
		this(x, y, width, height, RELATIVE_TO_TOP_LEFT);
	}
	
	public Button(int x, int y, int width, int height, int relativeTo) {
		super(x, y, width, height);
		this.relativeTo = relativeTo;
	}
	
	public Button(String label, int x, int y) {
		this(label, x, y, RELATIVE_TO_TOP_LEFT);
	}
	
	public Button(String label, int x, int y, int relativeTo) {
		this(x, y, 0, 0, relativeTo);
		this.label = label;
	}
	
	// SET STYLE
	
	public Button withButtonStyle(ButtonStyle style) {
		this.style = style;
		onMouseUpdate();
		withIcon(icon);
		return this;
	}
	
	public Button withIcon(BufferedImage icon) {
		if(icon == null)
			return this;
		
		this.icon = Design.inkImage(icon, style.foregroundColor);
		
		if(!style.foregroundColor.equals(style.hoverForegroundColor))
			iconHover = Design.inkImage(icon, style.hoverForegroundColor);
		else
			iconHover = null;
		
		if(!style.foregroundColor.equals(style.clickForegroundColor))
			iconClick = Design.inkImage(icon, style.clickForegroundColor);
		else iconClick = null;
		
		return this;
	}
	
	// EVENTS
	
	@Override
	public boolean isMouseHovering(MouseEvent e, UI ui) {
		Rectangle d = getBounds(ui);
		if(e.getX() >= d.x && e.getX() <= d.x+width) {			// DOES NOT USE d.width OR d.height
			return e.getY() >= d.y && e.getY() <= d.y+height;
		} return false;
	}
	
	@Override
	public void onMouseEntered() {
		if(!hovering) {
			hovering = true;
			onMouseUpdate();
			trWOffset = new Transition(trWOffset.getValue(), 4, TRANSITION_DURATION).withEaseDegree(1);
			trHOffset = new Transition(trHOffset.getValue(), 4, TRANSITION_DURATION).withEaseDegree(1);
		}
	}
	
	@Override
	public void onMouseExited() {
		if(hovering) {
			hovering = false;
			onMouseReleased();
			onMouseUpdate();
			trWOffset = new Transition(trWOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
			trHOffset = new Transition(trHOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
		}
	}
	
	@Override
	public void onMousePressed() {
		if(!pressed) {
			pressed = true;
			onMouseUpdate();
			trXOffset = new Transition(trXOffset.getValue(), 2, TRANSITION_DURATION).withEaseDegree(1);
			trYOffset = new Transition(trYOffset.getValue(), 2, TRANSITION_DURATION).withEaseDegree(1);
		}
	}
	
	@Override
	public void onMouseReleased() {
		if(pressed) {
			pressed = false;
			onMouseUpdate();
			onPressed();
			trXOffset = new Transition(trXOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
			trYOffset = new Transition(trYOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
		}
	}
	
	private void onMouseUpdate() {
		Color c = style.backgroundColor;
		
		if(pressed)
			c = style.clickBackgroundColor;
		else if(hovering)
			c = style.hoverBackgroundColor;

		trColorROffset = new Transition(trColorROffset.getValue(), c.getRed(),	TRANSITION_DURATION).withEaseDegree(1);
		trColorGOffset = new Transition(trColorGOffset.getValue(), c.getGreen(),TRANSITION_DURATION).withEaseDegree(1);
		trColorBOffset = new Transition(trColorBOffset.getValue(), c.getBlue(), TRANSITION_DURATION).withEaseDegree(1);
	}
	
	// BUTTON DIMENSIONS
	
	public Rectangle getBounds(UI ui) {
		int xi = (int) x, yi = (int) y, wi = (int) width, hi = (int) height;
		
		int xOffset = (int) trXOffset.getValue(),
				yOffset = (int) trYOffset.getValue(),
				wOffset = (int) trWOffset.getValue(),
				hOffset = (int) trHOffset.getValue();
		
		wi += wOffset;
		hi += hOffset;
		
		switch(relativeTo) {
					// TRANSFORMATION			+ OFFSETS
			case RELATIVE_TO_TOP_RIGHT:
				xi = ui.getWidth()  - xi - wi	+ xOffset + wOffset/2;
				yi = 				  yi		+ yOffset - hOffset/2;
				break;
			case RELATIVE_TO_BOTTOM_LEFT:
				xi = 				  xi		+ xOffset - wOffset/2;
				yi = ui.getHeight() - yi - hi	+ yOffset + hOffset/2;
				break;
			case RELATIVE_TO_BOTTOM_RIGHT:
				xi = ui.getWidth()  - xi - wi	+ xOffset + wOffset/2;
				yi = ui.getHeight() - yi - hi	+ yOffset + hOffset/2;
				break;
			case RELATIVE_TO_TOP_CENTER:
				xi = ui.getWidth()/2 + xi - wi/2	+ xOffset + wOffset/2;
				yi = 				   yi			+ yOffset - hOffset/2;
				break;
			case RELATIVE_TO_BOTTOM_CENTER:
				xi = ui.getWidth()/2 + xi - wi/2	+ xOffset + wOffset/2;
				yi = ui.getHeight()	 - yi - hi		+ yOffset + hOffset/2;
				break;
			default:
				xi = 				  xi		+ xOffset - wOffset/2;
				yi = 				  yi		+ yOffset - hOffset/2;
		}
		
		return new Rectangle(xi, yi, wi, hi);
	}
	
	public void onPressed() {}
	
	// PAINT

	@Override
	public void paint(Graphics2D g2, Display display) {
		Rectangle d = getBounds(display.ui);
		
		// BACKGROUND
		g2.setColor(new Color((int) trColorROffset.getValue(), (int) trColorGOffset.getValue(), (int) trColorBOffset.getValue()));
		g2.fillRoundRect(d.x, d.y, d.width, d.height, style.borderRadius, style.borderRadius);
		
		// BORDER & ICON
		BufferedImage iconImg;
		if(pressed) {
			g2.setColor(style.clickBorderColor);
			iconImg = iconClick != null ? iconClick : icon;
		} else if(hovering) {
			g2.setColor(style.hoverBorderColor);
			iconImg = iconHover != null ? iconHover : icon;
		} else {
			g2.setColor(style.borderColor);
			iconImg = icon;
		}
		
		// BORDER
		g2.setStroke(style.borderStroke);
		g2.drawRoundRect(d.x, d.y, d.width, d.height, style.borderRadius, style.borderRadius);
		
		// ICON
		if(iconImg != null) {
			float iconWHRatio = iconImg.getWidth()/(float) iconImg.getHeight();
			int maxWidth  = (int) (style.iconMaxWidthRatio * d.width);
			int maxHeight = (int) (style.iconMaxHeightRatio * d.height);
			
			int maxWidthHeight = (int) (maxWidth  / iconWHRatio);	// height at max width
			int maxHeightWidth = (int) (maxHeight * iconWHRatio);	// width at max height
			
			int iconWidth, iconHeight;
			
			if(maxWidthHeight > d.height) {
				iconHeight = maxHeight;
				iconWidth  = maxHeightWidth;
			} else {
				iconWidth  = maxWidth;
				iconHeight = maxWidthHeight;
			}
			
			g2.drawImage(iconImg, d.x + d.width/2 - iconWidth/2, d.y + d.height/2 - iconHeight/2, iconWidth, iconHeight, null);
		} else if(label != null) {
			g2.setColor(style.foregroundColor);
			g2.setFont(style.labelFont);
			
			int strWidth  = g2.getFontMetrics().stringWidth(label);
			int strHeight = g2.getFontMetrics().getHeight();
			
			boolean wEquals = width		== (width	= strWidth	+ style.labelHorizontalPadding);
			boolean hEquals = height	== (height	= strHeight	+ style.labelVerticalPadding);
			
			if(wEquals && hEquals)
				g2.drawString(label, d.x + d.width/2 - strWidth/2, d.y + d.height/2 + strHeight/2 - 3);	// TODO -3
		}
			
	}

}
