package game.uielements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import game.Display;
import game.Display.UI;
import game.GameState;
import game.Player;
import game.Transition;
import game.properties.Property;
import misc.Constants;
import misc.Tools;
import misc.design.Design;

public class ManagePropertiesOverlay extends UIOverlay {
	
	public static final String	ABBREVIATION_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static final long UPDATE_COOLDOWN = 200;
	
	private Transition	opaqueTransition = new Transition(0, 255, Design.OVERLAY_WINDOW_DEFAULT_FADE_DURATION),
						buttonTransition = new Transition(0, 0, 10);
	
	private int page = 0;
	
	private boolean pop = false;
	
	private String latestGameStateIdentifier;
	private Dimension latestUISize;
	private long latestUpdate = System.currentTimeMillis();
	private int latestPage = 0;
	
	private Button	closeButton,
					prevPageButton,
					nextPageButton;
	
	private final ArrayList<UIElement> constantChildren = new ArrayList<>();

	public ManagePropertiesOverlay() {
		super(0, 0, 10, 10);
		
		closeButton = new Button("Close", 0, Design.PROPERTIES_VERTICAL_MARGIN, Button.RELATIVE_TO_BOTTOM_CENTER) {
			@Override
			public void onPressed() {
				pop();
			}
		};
		
		prevPageButton = new Button("<", 0, Design.PROPERTIES_VERTICAL_MARGIN, Button.RELATIVE_TO_BOTTOM_CENTER) {
			@Override
			public void onPressed() {
				page--;
				
				if(page < 0)
					page = 0;
			}
		};
		
		nextPageButton = new Button(">", 0, Design.PROPERTIES_VERTICAL_MARGIN, Button.RELATIVE_TO_BOTTOM_CENTER) {
			@Override
			public void onPressed() {
				page++;
			}
		};
		
		constantChildren.add(prevPageButton);
		constantChildren.add(closeButton);
		constantChildren.add(nextPageButton);
	}
	
	public void pop() {
		pop = true;
		opaqueTransition = new Transition(opaqueTransition.getValue(), 0, Design.OVERLAY_WINDOW_DEFAULT_FADE_DURATION);
	}

	@Override
	public void paint(Graphics2D g2, Display display) {
		UI ui = display.ui;
		GameState gameState = display.server.getLatestGameState();
		Player player = gameState.thisPlayer();
				
		if(latestUISize == null)
			latestUISize = ui.getSize();
		
		boolean updateButtons = System.currentTimeMillis() - latestUpdate > UPDATE_COOLDOWN; 
				
		if(updateButtons) {
			updateButtons = opaqueTransition.isDone() && latestGameStateIdentifier != (latestGameStateIdentifier = gameState.identifier);
		
			if(!latestUISize.equals(latestUISize = ui.getSize()) && !updateButtons)
				updateButtons = true;
			
			if(latestPage != (latestPage = page) && !updateButtons)
				updateButtons = true;
		}
		
		if(updateButtons) {
			children.clear();
			children.addAll(constantChildren);
			
			latestUpdate = System.currentTimeMillis();
		}
		
		if(pop && opaqueTransition.isDone())
			display.overlays.remove(this);
		
		if(player == null)
			return;
		
		ArrayList<Entry<Property, Integer>> properties = new ArrayList<>();
		Iterator<Entry<Property, Integer>> iProp = player.properties.entrySet().iterator();
		
		while(iProp.hasNext())
			properties.add(iProp.next());
		
		properties.sort((a, b) -> {
			int	av = a.getKey().street.charAt(0),
				bv = b.getKey().street.charAt(0);
			
			int ap = a.getKey().position,
				bp = b.getKey().position;
			
			return av > bv	? 1
							: (av < bv	? -1
										: (ap > bp	? 1
													: (ap < bp ? -1 : 0)
								)
							);
		});
		
		int cHeight	= Math.max(ui.getHeight()/4, Math.round(Design.PROPERTIES_CARD_MIN_WIDTH/Design.PROPERTIES_CARD_WIDTH_HEIGHT_RATIO));
		int cWidth	= Math.round(cHeight * Design.PROPERTIES_CARD_WIDTH_HEIGHT_RATIO);
		
		int rows	= (ui.getHeight() - 2*Design.PROPERTIES_VERTICAL_MARGIN)	/ (cHeight + Design.PROPERTIES_CONTENT_PADDING);
		int columns	= (ui.getWidth() - 2*Design.PROPERTIES_HORIZONTAL_MARGIN)	/ (cWidth + Design.PROPERTIES_CONTENT_PADDING);
		
		height	= rows		* (cHeight + Design.PROPERTIES_CONTENT_PADDING);
		width	= columns	* (cWidth  + Design.PROPERTIES_CONTENT_PADDING);
		
		// Console.log("cWidth " + cWidth + " cHeight " + cHeight + " width " + width + " height " + height);
		// Console.log("columns " + columns + " rows " + rows + " properties " + properties.size());
		
		// BACKGROUND
		g2.setColor(Tools.rgbWithOpaque(Design.PROPERTIES_BACKGROUND_COLOR, Math.round((opaqueTransition.getValue()/255)*Design.PROPERTIES_BACKGROUND_COLOR.getAlpha())));
		g2.fillRect(0, 0, ui.getWidth(), ui.getHeight());
		
		// CARDS
		int pageOffset = page * rows*columns;
		int pageMax = rows*columns == 0	? 0
										: properties.size()/(rows*columns) - (properties.size() != rows*columns ? 0 : 1);
		
		outer:
		for(int y = 0; y<rows; y++) {
			int propOffset = y*columns + pageOffset;
			
			for(int x = 0; x<columns; x++) {
				if(properties.size() <= x+propOffset)
					break outer;
				
				Entry<Property, Integer> p = properties.get(x+propOffset);
				
				boolean isMortgaged = p.getValue() < 0;
				
				int	cx = x * (cWidth + Design.PROPERTIES_CONTENT_PADDING) + Design.PROPERTIES_CONTENT_PADDING/2 + Math.round((ui.getWidth()-width)/2),
					cy = y * (cHeight + Design.PROPERTIES_CONTENT_PADDING) + Design.PROPERTIES_CONTENT_PADDING/2 + Design.PROPERTIES_VERTICAL_MARGIN;
				
				int xOffset = Math.round((1-(opaqueTransition.getValue()/255)) * (x-columns/2) * Design.PROPERTIES_CARD_ANIMATION_X_OFFSET);
				
				// BACKGROUND
				Color backgroundColor = Tools.rgbWithOpaque(isMortgaged ? Design.PROPERTIES_CARD_MORTGAGE_BACKGROUND_COLOR : Design.PROPERTIES_CARD_BACKGROUND_COLOR, Math.round(opaqueTransition.getValue()));
				
				g2.setColor(backgroundColor);
				g2.fillRoundRect(cx + xOffset, cy, (int) cWidth, (int) cHeight, Design.PROPERTIES_CARD_BORDER_RADIUS, Design.PROPERTIES_CARD_BORDER_RADIUS);
				
				// PROPERTY COLOR
				g2.setColor(Tools.rgbWithOpaque(p.getKey().color, Math.round(opaqueTransition.getValue())));
				g2.fillRoundRect(cx + xOffset, cy, cWidth, cHeight/9, Design.PROPERTIES_CARD_BORDER_RADIUS, Design.PROPERTIES_CARD_BORDER_RADIUS);
				
				g2.setColor(backgroundColor);
				g2.fillRect(cx + xOffset, cy+cHeight/9 - Design.PROPERTIES_CARD_BORDER_RADIUS, (int) cWidth, Design.PROPERTIES_CARD_BORDER_RADIUS);
				
				// FONT
				g2.setColor(Tools.rgbWithOpaque(Design.PROPERTIES_CARD_FOREGROUND_COLOR, Math.round(opaqueTransition.getValue())));
				int yFontOffset = Math.round((1-(opaqueTransition.getValue()/255)) * Design.PROPERTIES_CARD_TITLE_ANIMATION_Y_OFFSET);
				int fontY;
				
				// TITLE
				g2.setFont(Design.PROPERTIES_CARD_TITLE_FONT);
				String titleStr = p.getKey().title;
				int titleWidth = g2.getFontMetrics().stringWidth(titleStr);				
				
				if(titleWidth > cWidth) {
					titleStr = "";
					String[] words = p.getKey().title.split(" ");
					for(int i = 0; i<words.length; i++) {
						if(words[i].length() < 1)
							continue;
						if(!ABBREVIATION_LETTERS.contains(words[i].charAt(0) + "") && i != words.length-1)
							continue;
						
						titleStr += i != words.length-1 ? words[i].charAt(0) : (i > 0 ? " " : "") + words[i];
					}
					titleWidth = g2.getFontMetrics().stringWidth(titleStr);
				}
				
				int fontSize = Design.PROPERTIES_CARD_TITLE_FONT.getSize();
				while(titleWidth > cWidth && --fontSize > Design.PROPERTIES_CARD_TITLE_FONT_MIN_SIZE) {
					g2.setFont(Tools.fontWithFontSize(Design.PROPERTIES_CARD_TITLE_FONT, fontSize));
					titleWidth = g2.getFontMetrics().stringWidth(titleStr);
				}
				
				g2.drawString(titleStr, cx + cWidth/2 - titleWidth/2, fontY = cy + cHeight/9 + g2.getFontMetrics(Design.PROPERTIES_CARD_TITLE_FONT).getHeight() + yFontOffset - 3);
				
				// STREET
				g2.setFont(Design.PROPERTIES_CARD_STREET_FONT);
				String streetStr = "Group " + p.getKey().street;
				int streetWidth = g2.getFontMetrics().stringWidth(streetStr);
				
				fontSize = Design.PROPERTIES_CARD_STREET_FONT.getSize();
				while(streetWidth > cWidth && --fontSize > Design.PROPERTIES_CARD_TEXT_FONT_MIN_SIZE) {
					g2.setFont(Tools.fontWithFontSize(Design.PROPERTIES_CARD_STREET_FONT, fontSize));
					streetWidth = g2.getFontMetrics().stringWidth(streetStr);
				}
				
				g2.drawString(streetStr, cx + cWidth/2 - streetWidth/2, fontY += g2.getFontMetrics(Design.PROPERTIES_CARD_STREET_FONT).getHeight());
				
				// BUY HOUSE
				int buySellHouseWidth = cWidth/2 - (int) Math.round(1.5*Design.PROPERTIES_CONTENT_PADDING);
				int buySellHouseHeight = cHeight/7;
				
				boolean canBuildHouse = p.getKey().pricePerHouse > 0;
				
				g2.setFont(Design.PROPERTIES_CARD_INFO_FONT);
				
				String priceStr = canBuildHouse ? "Buy: " + p.getKey().pricePerHouse + " " + Constants.MONEY_UNIT + ", sell (" + p.getValue() + "): " + p.getKey().pricePerHouse/2 + " " + Constants.MONEY_UNIT: "No server slots";
				if(isMortgaged)
					priceStr = "Mortgaged";
				int priceWidth = g2.getFontMetrics().stringWidth(priceStr);
				
				fontSize = Design.PROPERTIES_CARD_INFO_FONT.getSize();
				while(priceWidth > cWidth && --fontSize > Design.PROPERTIES_CARD_TEXT_FONT_MIN_SIZE) {
					g2.setFont(Tools.fontWithFontSize(Design.PROPERTIES_CARD_INFO_FONT, fontSize));
					priceWidth = g2.getFontMetrics().stringWidth(priceStr);
				}
					
				g2.drawString(priceStr, cx + cWidth/2 - priceWidth/2, fontY += g2.getFontMetrics(Design.PROPERTIES_CARD_INFO_FONT).getHeight());
				
				// BUY HOUSE AND MORTGAGE BUTTONS
				fontY += Design.PROPERTIES_CONTENT_PADDING;
				if(!isMortgaged) {
					if(updateButtons) {
						Button buyHouse = new Button(cx + cWidth/2 - buySellHouseWidth - Design.PROPERTIES_CONTENT_PADDING/2, fontY, buySellHouseWidth, buySellHouseHeight) {
							@Override
							public void onPressed() {
								gameState.buyHouse(p.getKey());
							}
						}.withButtonStyle(Design.BUTTON_GREEN_STYLE).withIcon(Design.getImage("add-server-icon.png")).setEnabled(canBuildHouse);
						
						// SELL HOUSE
						Button sellHouse = new Button(cx + cWidth/2 + Design.PROPERTIES_CONTENT_PADDING/2, fontY, buySellHouseWidth, buySellHouseHeight) {
							@Override
							public void onPressed() {
								gameState.sellHouse(p.getKey());
							}
						}.withButtonStyle(Design.BUTTON_RED_STYLE).withIcon(Design.getImage("remove-server-icon.png"))
								.setEnabled(player.properties.containsKey(p.getKey()) && player.properties.get(p.getKey()) > 0 && canBuildHouse);
					
						children.add(buyHouse);
						children.add(sellHouse);
					}
						
					g2.setFont(Design.PROPERTIES_CARD_INFO_FONT);
					
					String mortgageStr = p.getKey().isMortgagable() ? "Mortgage: " + p.getKey().mortgage + " " + Constants.MONEY_UNIT : "Cannot mortgage";
					int mortgageWidth = g2.getFontMetrics().stringWidth(mortgageStr);
					
					fontSize = Design.PROPERTIES_CARD_INFO_FONT.getSize();
					while(mortgageWidth > cWidth && --fontSize > Design.PROPERTIES_CARD_TEXT_FONT_MIN_SIZE) {
						g2.setFont(Tools.fontWithFontSize(Design.PROPERTIES_CARD_INFO_FONT, fontSize));
						mortgageWidth = g2.getFontMetrics().stringWidth(mortgageStr);
					}
						
					g2.drawString(mortgageStr, cx + cWidth/2 - mortgageWidth/2, fontY += g2.getFontMetrics(Design.PROPERTIES_CARD_INFO_FONT).getHeight() + buySellHouseHeight);
				} else {
					g2.setFont(Design.PROPERTIES_CARD_INFO_FONT);
					
					String mortgageStr = "Unmortgage: " + p.getKey().getUnmortgagePrice() + " " + Constants.MONEY_UNIT;
					int mortgageWidth = g2.getFontMetrics().stringWidth(mortgageStr);
					
					fontSize = Design.PROPERTIES_CARD_INFO_FONT.getSize();
					while(mortgageWidth > cWidth && --fontSize > Design.PROPERTIES_CARD_TEXT_FONT_MIN_SIZE) {
						g2.setFont(Tools.fontWithFontSize(Design.PROPERTIES_CARD_INFO_FONT, fontSize));
						mortgageWidth = g2.getFontMetrics().stringWidth(mortgageStr);
					}
						
					g2.drawString(mortgageStr, cx + cWidth/2 - mortgageWidth/2, fontY += g2.getFontMetrics(Design.PROPERTIES_CARD_INFO_FONT).getHeight());
				}
					
				if(updateButtons) {
					// SELL HOUSE
					String mortgageStr = "Mortgage";
					int mortgageWidth = Button.approximateLabelButtonWidth(g2, Design.BUTTON_RED_STYLE, mortgageStr);
					
					Button mortgageProperty = new Button(mortgageStr, cx + cWidth/2 - mortgageWidth/2, fontY += Design.PROPERTIES_CONTENT_PADDING) {
						@Override
						public void onPressed() {
							if(isSelected())
								gameState.mortgageProperty(p.getKey());
							else
								gameState.unmortgageProperty(p.getKey());
							setSelected(!isSelected());
						}
					}.withButtonStyle(Design.BUTTON_RED_STYLE).setSelectable(true).setSelected(isMortgaged).setEnabled(p.getKey().isMortgagable());
					
					children.add(mortgageProperty);
				}
			}
		}
		
		if(page > pageMax)
			page = pageMax;
		
		// OPTIONS
		if(opaqueTransition.isDone() && !pop && System.currentTimeMillis() - latestUpdate > UPDATE_COOLDOWN) {
			prevPageButton.x = -closeButton.width - Design.PROPERTIES_CONTENT_PADDING;
			prevPageButton.setEnabled(page > 0);
			
			nextPageButton.x = closeButton.width + Design.PROPERTIES_CONTENT_PADDING;
			nextPageButton.setEnabled(page < pageMax);
			
			if(buttonTransition == null || buttonTransition.to != 1)
				buttonTransition = new Transition(0, 1, Design.OVERLAY_WINDOW_DEFAULT_FADE_DURATION).withEaseDegree(1);
			
			for(int i = 0; i<(int) (buttonTransition.getValue()*children.size()); i++)
				children.get(i).paint(g2, display);
		} else
			buttonTransition = null;
	}

}
