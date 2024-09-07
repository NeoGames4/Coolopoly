package game.uielements;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import game.DiceState;
import game.Display;
import game.Display.UI;
import game.GameState;
import game.Player;
import game.Transition;
import game.properties.Property;
import misc.Constants;
import misc.Notification;
import misc.Tools;
import misc.design.ColorPair;
import misc.design.Design;
import sound.SoundManager;

public class InfoPanel extends UIElement {
	
	private Transition moneyTransition = new Transition(0, 0, 10);
	
	private Transition	lastMoneyOpaqueTransition			= new Transition(0, 0, 10),
						lastMoneyPositionOffsetTransition	= new Transition(0, 0, 10);
	private int lastMoney = 0;
	
	private Transition	tooltipTextTransition = null,
						diceTransition = null;
	
	private final Button	diceButton,
							buyButton,
							propertiesButton;
	
	private static final BufferedImage	DICE_ICON = Design.getImage("dice-icon.png"),
										NEXT_ICON = Design.getImage("arrow-right-icon.png");
										
	private static final BufferedImage[] DICE_NUMBERS = new BufferedImage[6];
	static {
		for(int i = 0; i<DICE_NUMBERS.length; i++)
			DICE_NUMBERS[i] = Design.inkImage(Design.getImage("dice-icon-" + (i+1) + ".png"), Design.INFO_PANEL_DICE_COLOR);
	}
	
	private static final String	DICE_TOOLTIP_TEXT = "2x random(1, 6)",
								NEXT_TOOLTIP_TEXT = "next()";

	public InfoPanel(Display display) {
		super(0, 0, 0, Design.INFO_PANEL_HEIGHT);
		
		diceButton = new Button(10, 6, 2 * Design.INFO_PANEL_HEIGHT, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_RIGHT) {
			@Override
			public void onPressed() {
				GameState gameState = display.server.getLatestGameState();
				if(!gameState.playerDiced())
					gameState.dice();
				else
					gameState.nextPlayer();
			}
		}.withIcon(DICE_ICON).withButtonStyle(Design.BUTTON_RED_STYLE).setTooltip(DICE_TOOLTIP_TEXT);
		
		buyButton = new Button((int) diceButton.width+20, 6, Design.INFO_PANEL_HEIGHT - 12, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_RIGHT) {
			@Override
			public void onPressed() {
				display.server.getLatestGameState().buyProperty();
			}
		}.withIcon(Design.getImage("cart-icon.png")).withButtonStyle(Design.BUTTON_GREEN_STYLE).setTooltip("buy(this)");

		propertiesButton = new Button((int) (diceButton.width+20 + buyButton.width+10), 6, Design.INFO_PANEL_HEIGHT - 12, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_RIGHT)
										.withIcon(Design.getImage("eye-icon.png")).withButtonStyle(Design.BUTTON_GREEN_STYLE).setSelectable(true).setTooltip("Property Highlighting");
		
		Button aboutButton = new Button(10, 6, Design.INFO_PANEL_HEIGHT - 12, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_LEFT) {
			@Override
			public void onPressed() {
				DialogWindowOption[] options = {
					DialogWindowOption.getDiscardOption("OK", display.dialogs),
					new DialogWindowOption("Open on GitHub") {
						@Override
						public void onSelect() {
							try {
								Desktop.getDesktop().browse(new URI(Constants.GAME_GITHUB));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				display.dialogs.add(
					new DialogWindow(
						"About " + Constants.GAME_TITLE,
						"Version " + Constants.GAME_VERSION + "\n" +
						"Graphics, music & development by " + Constants.GAME_AUTHOR + ".\n" +
						"License: " + Constants.GAME_LICENSE + ".\n\n" +
						Constants.GAME_DISCLAIMER,
						options
					)
				);
			}
		}.withIcon(Design.getImage("info-icon.png")).setTooltip("About");
		
		Button settingsButton = new Button((int) (20 + aboutButton.width), 6, Design.INFO_PANEL_HEIGHT - 12, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_LEFT) {
			@Override
			public void onPressed() {
				// TODO
			}
		}.withIcon(Design.getImage("gear-icon.png")).setTooltip("Settings");
		
		Button managePropertiesButton = new Button((int) (30 + aboutButton.width + settingsButton.width), 6, Design.INFO_PANEL_HEIGHT - 12, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_LEFT) {
			@Override
			public void onPressed() {
				display.overlays.add(new ManagePropertiesOverlay());
			}
		}.withIcon(Design.getImage("arrow-down-icon.png")).setTooltip("manage(downloads)").withButtonStyle(Design.BUTTON_GREEN_STYLE);
		
		display.uiElements.add(diceButton);
		display.uiElements.add(buyButton);
		display.uiElements.add(propertiesButton);
		
		display.uiElements.add(aboutButton);
		display.uiElements.add(settingsButton);
		display.uiElements.add(managePropertiesButton);
	}
	
	@Override
	public boolean isMouseHovering(MouseEvent e, UI ui) { return false; }
	
	private boolean prevPlayerDiced = false;

	@Override
	public void paint(Graphics2D g2, Display display) {
		GameState gameState = display.server.getLatestGameState();
		Player player = gameState.thisPlayer();
		
		// BACKGROUND
		g2.setColor(Design.INFO_PANEL_BACKGROUND_COLOR);
		g2.fillRect(0, display.ui.getHeight()-(int) height, display.ui.getWidth(), (int) height);
		g2.setColor(Design.INFO_PANEL_BACKGROUND_ACCENT_COLOR);
		g2.fillRect(0, display.ui.getHeight()-(int) height-5, display.ui.getWidth(), 5);
		g2.setColor(Design.INFO_PANEL_FOREGROUND_COLOR);
		g2.fillRect(0, display.ui.getHeight()-(int) height-3, display.ui.getWidth(), 1);
		
		// MONEY TRANSITION
		if(player != null && moneyTransition.to != player.getMoney()) {
			lastMoney = (int) moneyTransition.to;
			moneyTransition = new Transition(
				(int) moneyTransition.getValue(),
				player.getMoney(),
				Design.INFO_PANEL_MONEY_TRANSITION_DURATON
			);
			lastMoneyOpaqueTransition = new Transition(255, 0, System.currentTimeMillis()+1500, System.currentTimeMillis()+2250).withEaseDegree(1);
			lastMoneyPositionOffsetTransition = new Transition(20, 0, 150).withEaseDegree(1);
			SoundManager.safePlay(SoundManager.MONEY_CHANGED_SOUND, 0);
		}
		
		// MONEY PRINT
		g2.setFont(Design.INFO_PANEL_MONEY_FONT);
		g2.setColor(Design.INFO_PANEL_FOREGROUND_COLOR);
		
		String moneyStr = "Free: " + (int) moneyTransition.getValue() + " " + Constants.MONEY_UNIT;
		int moneyStrWidth = g2.getFontMetrics().stringWidth(moneyStr);
		g2.drawString(
			moneyStr,
			display.ui.getWidth()/2	- moneyStrWidth/2,
			display.ui.getHeight()	- Design.INFO_PANEL_HEIGHT + g2.getFontMetrics().getHeight() + 2
		);
		
		// MONEY CHANGE
		if(player != null && !lastMoneyOpaqueTransition.isDone()) {
			int diff = player.getMoney()-lastMoney;
			String diffStr = (diff > 0 ? "+" + diff : diff) + " " + Constants.MONEY_UNIT;
			
			g2.setColor(Tools.rgbWithOpaque(
				diff > 0 ? Design.INFO_PANEL_GREEN_COLOR : Design.INFO_PANEL_RED_COLOR,
				(int) lastMoneyOpaqueTransition.getValue()
			));
			g2.setFont(Design.INFO_PANEL_MONEY_CHANGE_FONT);
			g2.drawString(
				diffStr,
				display.ui.getWidth()/2	- g2.getFontMetrics().stringWidth(diffStr)/2,
				display.ui.getHeight()	- Design.INFO_PANEL_HEIGHT + g2.getFontMetrics(Design.INFO_PANEL_MONEY_FONT).getHeight() + g2.getFontMetrics().getHeight() + (int) lastMoneyPositionOffsetTransition.getValue()
			);
		}
		
		// BUTTONS
		boolean diceStateChanged = prevPlayerDiced == (prevPlayerDiced = gameState.playerDiced());
		boolean thisPlayersTurn = gameState.currentPlayer().nameEquals(player);
		
		diceButton.setEnabled(thisPlayersTurn);
		
		if(diceStateChanged)
			diceButton.withIcon(gameState.playerDiced() ? NEXT_ICON : DICE_ICON)
					.setTooltip(gameState.playerDiced() ? NEXT_TOOLTIP_TEXT : DICE_TOOLTIP_TEXT);
		
		buyButton.setEnabled(gameState.playerCanBuy())
				.setTooltip("buy(\"" + gameState.currentProperty().title + "\")");
		
		if(display.getTicks() % Constants.FPS/2 == 0) {
			if(propertiesButton.isSelected()) {
				HashMap<Integer, ColorPair> fields = new HashMap<>();
				
				Iterator<Entry<Property, Player>> i = display.server.getLatestGameState().getPropertiesWithOwners().entrySet().iterator();
				while(i.hasNext()) {
					Entry<Property, Player> e = i.next();
					if(e.getValue() == null)
						continue;
					
					fields.put(
						e.getKey().position,
						e.getValue().nameEquals(player)	? new ColorPair(Design.BOARD_HIGHLIGHT_OWN_PROPERTY_PRIMARY_COLOR,	Design.BOARD_HIGHLIGHT_OWN_PROPERTY_SECONDARY_COLOR)
														: new ColorPair(Design.BOARD_HIGHLIGHT_SOLD_PROPERTY_PRIMARY_COLOR,	Design.BOARD_HIGHLIGHT_SOLD_PROPERTY_SECONDARY_COLOR)
					);
				}
				
				display.ui.board.highlightedFields.put(Constants.BOARD_HIGHLIGHTING_PROPERTIES, fields);
			} else
				display.ui.board.highlightedFields.remove(Constants.BOARD_HIGHLIGHTING_PROPERTIES);
		}
		
		// PLAYER LIST
		g2.setFont(Design.INFO_PANEL_PLAYER_LIST_FONT);
		g2.setColor(Design.INFO_PANEL_FOREGROUND_COLOR);
		g2.drawString("PLAYERS", 10, g2.getFontMetrics().getHeight());
		
		Player currentPlayer = gameState.currentPlayer();
		
		for(int i = 0; i<gameState.getPlayers().size(); i++) {
			Player p = gameState.getPlayers().get(i);
			
			g2.setColor(p.nameEquals(currentPlayer) ? Design.INFO_PANEL_PLAYERS_HIGHLIGHT_COLOR : Design.INFO_PANEL_PLAYERS_FOREGROUND_COLOR);
			
			String pStr = (currentPlayer.nameEquals(p) ? "-> " : "   ") + p.name;
			if(gameState.isOver())
				pStr += " (total: " + p.getTotalWorth() + " " + Constants.MONEY_UNIT + ")";
			else if(!p.nameEquals(player))
				pStr += " (" + p.getMoney() + " " + Constants.MONEY_UNIT + ")";
			
			g2.drawString(pStr, 10, (i+2) * g2.getFontMetrics().getHeight());
		}
		
		// DICE
		if(!gameState.playerDiced() && diceTransition != null && diceTransition.from == 0)
			diceTransition = new Transition(1, 0, Design.INFO_PANEL_DICE_TRANSITION_DURATION);
		
		if(gameState.playerDiced() || (diceTransition != null && !diceTransition.isDone())) {
			if(diceTransition == null || (gameState.playerDiced() && diceTransition.from != 0))
				diceTransition = new Transition(0, 1, Design.INFO_PANEL_DICE_TRANSITION_DURATION);
			
			int diceSize = (int) (display.ui.board.width/9);
			int padding = Design.INFO_PANEL_DICE_PADDING;
			int diceWindowWidth = Math.round((diceSize + 2*padding) * (diceTransition.from == 0 ? 1 : diceTransition.getValue()));
			
			int zoomOutY = display.ui.getHeight()/2 - diceSize/2;
			int zoomInY = (int) (display.ui.getHeight() - height - 4*Design.INFO_PANEL_DICE_PADDING - diceSize);
			int diceY = Math.round(display.getZoomTransition().getValue() * (zoomOutY-zoomInY) + zoomInY);
			
			int opaque = (int) (diceTransition.getValue() * Design.INFO_PANEL_DICE_BACKGROUND_COLOR.getAlpha());
			g2.setColor(Tools.rgbWithOpaque(Design.INFO_PANEL_DICE_BACKGROUND_COLOR, opaque));
			g2.fillRoundRect(display.ui.getWidth()/2 - diceSize - padding - padding/2, diceY - padding, 2*diceSize + 3*padding, diceWindowWidth, Design.INFO_PANEL_DICE_CORNER_RADIUS, Design.INFO_PANEL_DICE_CORNER_RADIUS);
			
			opaque = (int) (diceTransition.getValue() * Design.INFO_PANEL_DICE_BORDER_COLOR.getAlpha());
			g2.setColor(Tools.rgbWithOpaque(Design.INFO_PANEL_DICE_BORDER_COLOR, opaque));
			g2.setStroke(Design.INFO_PANEL_DICE_BORDER_STROKE);
			g2.drawRoundRect(display.ui.getWidth()/2 - diceSize - padding - padding/2, diceY - padding, 2*diceSize + 3*padding, diceWindowWidth, Design.INFO_PANEL_DICE_CORNER_RADIUS, Design.INFO_PANEL_DICE_CORNER_RADIUS);
			
			if(gameState.playerDiced()) {
				DiceState dice = gameState.getDiceState();
				int diceA = diceTransition.isDone() ? dice.diceA-1 : Math.round(30 * diceTransition.getValue()) % DICE_NUMBERS.length,
					diceB = diceTransition.isDone() ? dice.diceB-1 : Math.round(30 * diceTransition.getValue() + 3) % DICE_NUMBERS.length;
				
				g2.drawImage(DICE_NUMBERS[diceA], display.ui.getWidth()/2 - diceSize - padding/2, diceY, diceSize, diceSize, null);
				g2.drawImage(DICE_NUMBERS[diceB], display.ui.getWidth()/2            + padding/2, diceY, diceSize, diceSize, null);
			}
		} else
			diceTransition = null;
		
		// NOTIFICATIONS
		g2.setFont(Design.INFO_PANEL_NOTIFICATIONS_FONT);
		
		if(!display.notifications.isEmpty()) {
			int ny = 0;
			for(int i = 0; i<display.notifications.size(); i++) {
				Notification n = display.notifications.get(i);
				if(n.aTransition.getValue() == 0)
					continue;
				ny++;
				
				if(n.yTransition.to != ny * g2.getFontMetrics().getHeight())
					n.yTransition = new Transition(n.yTransition.getValue(), ny * g2.getFontMetrics().getHeight(), Design.INFO_PANEL_NOTIFICATIONS_MOVE_DURATION).withEaseDegree(1);
				
				String str = n.message;
				int strWidth = g2.getFontMetrics().stringWidth(str);
				
				g2.setColor(Tools.rgbWithOpaque(Design.INFO_PANEL_NOTIFICATIONS_BOX_COLOR, Math.round(n.aTransition.getValue() * Design.INFO_PANEL_NOTIFICATIONS_BOX_COLOR.getAlpha())));
				g2.fillRect(display.ui.getWidth()/2 - strWidth/2-1, (int) (n.yTransition.getValue() - 0.8 * g2.getFontMetrics().getHeight())+4, strWidth+2, (int) (0.8*g2.getFontMetrics().getHeight()));
				
				g2.setColor(Tools.rgbWithOpaque(Design.INFO_PANEL_NOTIFICATIONS_TEXT_COLOR, Math.round(n.aTransition.getValue() * Design.INFO_PANEL_NOTIFICATIONS_TEXT_COLOR.getAlpha())));
				g2.drawString(str, display.ui.getWidth()/2 - strWidth/2, n.yTransition.getValue());
				
				if(n.timestamp + n.displayDuration < System.currentTimeMillis() && n.aTransition.isDone() && i+1 != display.notifications.size())
					n.aTransition = new Transition(1, 0, n.timestamp + n.displayDuration, n.timestamp + n.displayDuration + Design.INFO_PANEL_NOTIFICATIONS_FADE_DURATION);
			}
		}
		
		// TOOLTIP
		if(display.tooltipText != null) {
			if(tooltipTextTransition == null)
				tooltipTextTransition = new Transition(0, 1, Design.INFO_PANEL_TOOLTIP_TRANSITION_DURATION);
			
			int yOffset = Math.round((1-tooltipTextTransition.getValue())*10);
			int opaque = (int) (tooltipTextTransition.getValue()*255);
			
			g2.setFont(Design.INFO_PANEL_TOOLTIP_FONT);
			int tooltipWidth = g2.getFontMetrics().stringWidth(display.tooltipText);
			
			g2.setColor(Tools.rgbWithOpaque(Design.INFO_PANEL_TOOLTIP_BACKGROUND, opaque));
			g2.fillRect(display.ui.getWidth()/2 - tooltipWidth/2 - 8, display.ui.getHeight()/5 - yOffset, tooltipWidth+16, g2.getFontMetrics().getHeight()+8);
			
			g2.setColor(Tools.rgbWithOpaque(Design.INFO_PANEL_TOOLTIP_FOREGROUND, opaque));
			g2.drawString(display.tooltipText, display.ui.getWidth()/2 - tooltipWidth/2, display.ui.getHeight()/5 + g2.getFontMetrics().getHeight() - yOffset);
		} else
			tooltipTextTransition = null;
	}

}
