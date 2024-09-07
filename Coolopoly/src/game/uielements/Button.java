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
import sound.SoundManager;

public class Button extends UIElement {
	
	public String tooltipText;
	
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
	
	private boolean hovering		= false,	// mouse is hovering over the button
					pressed			= false,	// mouse is pressing the button
					disabled		= false;	// whether the button should memorize if selected

	private boolean selected		= false;

	private boolean isSelectable	= false;
	
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
		if(!style.equals(this.style)) {
			this.style = style;
			refreshBackgroundColor();
			withIcon(icon);
		}
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
	
	// SET MODE
	
	public Button setEnabled(boolean e) {
		if(disabled == e) {
			disabled = !e;
			refreshBackgroundColor();
		}
		return this;
	}
	
	public Button setSelectable(boolean s) {
		isSelectable = s;
		selected = false;
		return this;
	}
	
	public Button setSelected(boolean s) {
		if(selected != s) {
			selected = s;
			refreshBackgroundColor();
		}
		return this;
	}
	
	// INFORMATION
	
	public Button setTooltip(String s) {
		tooltipText = s;
		return this;
	}
	
	public boolean isSelected() {
		return isSelectable && selected;
	}
	
	// EVENTS
	
	@Override
	public boolean isMouseHovering(MouseEvent e, UI ui) {
		if(disabled)
			return false;
		
		Rectangle d = getBounds(ui);
		if(e.getX() >= d.x && e.getX() <= d.x+width) {			// DOES NOT USE d.width OR d.height
			return e.getY() >= d.y && e.getY() <= d.y+height;
		} return false;
	}
	
	@Override
	public void onMouseEntered() {
		if(!hovering) {
			hovering = true;
			refreshBackgroundColor();
			trWOffset = new Transition(trWOffset.getValue(), 4, TRANSITION_DURATION).withEaseDegree(1);
			trHOffset = new Transition(trHOffset.getValue(), 4, TRANSITION_DURATION).withEaseDegree(1);
		}
	}
	
	@Override
	public void onMouseExited() {
		if(hovering) {
			hovering = false;
			onMouseReleased();
			refreshBackgroundColor();
			trWOffset = new Transition(trWOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
			trHOffset = new Transition(trHOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
		}
	}
	
	@Override
	public void onMousePressed() {
		if(!pressed) {
			pressed = true;
			
			refreshBackgroundColor();
			
			trXOffset = new Transition(trXOffset.getValue(), 2, TRANSITION_DURATION).withEaseDegree(1);
			trYOffset = new Transition(trYOffset.getValue(), 2, TRANSITION_DURATION).withEaseDegree(1);
			
			SoundManager.safePlay(SoundManager.BUTTON_PRESS_SOUND, 0);
		}
	}
	
	@Override
	public void onMouseReleased() {
		if(pressed) {
			pressed = false;
			selected = !selected;
			
			refreshBackgroundColor();
			onPressed();
			
			trXOffset = new Transition(trXOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
			trYOffset = new Transition(trYOffset.getValue(), 0, TRANSITION_DURATION).withEaseDegree(1);
			
			SoundManager.safePlay(SoundManager.BUTTON_RELEASE_SOUND, 0);
		}
	}
	
	private void refreshBackgroundColor() {
		Color c = style.backgroundColor;
		
		if(disabled) {
			c = style.disabledBackgroundColor;
		} else if(pressed) {
			c = style.clickBackgroundColor;
		} else if(isSelected()) {
			c = style.selectedBackgroundColor;
		} else if(hovering) {
			c = style.hoverBackgroundColor;
		}

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
		BufferedImage iconImg = icon;
		g2.setColor(style.borderColor);
		
		if(disabled) {
			g2.setColor(style.disabledBorderColor);
		} else if(pressed) {
			g2.setColor(style.clickBorderColor);
			iconImg = iconClick != null ? iconClick : icon;
		} else if(isSelected()) {
			g2.setColor(style.selectedBorderColor);
		} else if(hovering) {
			g2.setColor(style.hoverBorderColor);
			iconImg = iconHover != null ? iconHover : icon;
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
			
			if(disabled) {
				g2.setColor(style.disabledForegroundColor);
			} else if(pressed) {
				g2.setColor(style.clickForegroundColor);
			} else if(isSelected()) {
				g2.setColor(style.selectedForegroundColor);
			} else if(hovering) {
				g2.setColor(style.hoverForegroundColor);
			}
			
			int strWidth  = g2.getFontMetrics().stringWidth(label);
			int strHeight = g2.getFontMetrics().getHeight();
			
			boolean wEquals = width		== (width	= strWidth	+ style.labelHorizontalPadding);
			boolean hEquals = height	== (height	= strHeight	+ style.labelVerticalPadding);
			
			if(wEquals && hEquals)
				g2.drawString(label, d.x + d.width/2 - strWidth/2, d.y + d.height/2 + strHeight/2 - 3);	// TODO -3
		}
		
		// TOOLTIP
		if(hovering)
			display.tooltipText = tooltipText;
		else if(display.tooltipText != null && display.tooltipText.equals(tooltipText))
			display.tooltipText = null;
			
	}
	
	public static int approximateLabelButtonWidth(Graphics2D g2, ButtonStyle style, String label) {
		return g2.getFontMetrics(style.labelFont).stringWidth(label) + style.labelHorizontalPadding;
	}
	
	public static int approximateLabelButtonHeight(Graphics2D g2, ButtonStyle style) {
		return g2.getFontMetrics(style.labelFont).getHeight() + style.labelVerticalPadding;
	}

}
