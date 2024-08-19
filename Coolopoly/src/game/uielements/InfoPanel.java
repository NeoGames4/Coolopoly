package game.uielements;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import game.Display;
import game.Display.UI;
import game.GameState;
import game.Player;
import game.Transition;
import misc.Constants;
import misc.Tools;
import misc.design.Design;

public class InfoPanel extends UIElement {
	
	private Transition moneyTransition = new Transition(0, 0, 10);
	
	private Transition	lastMoneyOpaqueTransition			= new Transition(0, 0, 10),
						lastMoneyPositionOffsetTransition	= new Transition(0, 0, 10);
	private int lastMoney = 0;
	
	private final Button	diceButton,
							propertiesButton;

	public InfoPanel(Display display) {
		super(0, 0, 0, Design.INFO_PANEL_HEIGHT);
		
		diceButton = new Button(10, 6, 2 * Design.INFO_PANEL_HEIGHT, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_LEFT) {
			@Override
			public void onPressed() {	// TODO
				display.server.getLatestGameState();
				System.out.println("Dice");
			}
		}.withIcon(Design.getImage("dice-icon.png")).withButtonStyle(Design.BUTTON_RED_STYLE);

		propertiesButton = new Button((int) diceButton.width+20, 6, 2 * Design.INFO_PANEL_HEIGHT, Design.INFO_PANEL_HEIGHT - 12, Button.RELATIVE_TO_BOTTOM_LEFT) {
			@Override
			public void onPressed() {	// TODO
				display.server.getLatestGameState();
			}
		}.withIcon(Design.getImage("city-icon.png")).withButtonStyle(Design.BUTTON_GREEN_STYLE);
		
		display.uiElements.add(diceButton);
		display.uiElements.add(propertiesButton);
	}
	
	@Override
	public boolean isMouseHovering(MouseEvent e, UI ui) { return false; }

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
		if(moneyTransition.to != player.getMoney()) {
			lastMoney = (int) moneyTransition.to;
			moneyTransition = new Transition(
				(int) moneyTransition.getValue(),
				gameState.thisPlayer().getMoney(),
				Design.INFO_PANEL_MONEY_TRANSITION_DURATON
			);
			lastMoneyOpaqueTransition = new Transition(255, 0, System.currentTimeMillis()+1500, System.currentTimeMillis()+2250).withEaseDegree(1);
			lastMoneyPositionOffsetTransition = new Transition(20, 0, 150).withEaseDegree(1);
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
		if(!lastMoneyOpaqueTransition.isDone()) {
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
		/*diceButton.x		= (display.ui.getWidth() - moneyStrWidth - 2*diceButton.width)/4;
		propertiesButton.x	= (display.ui.getWidth() - moneyStrWidth - 2*propertiesButton.width)/4;
		
		// PLAYER LIST
		/*int debugRowCount = 1;
		g2.drawString("Position: " + player.getPosition(), 10, (debugRowCount++)*g2.getFont().getSize());
		g2.drawString("isRunning: " + gameState.isRunning(), 10, (debugRowCount++)*g2.getFont().getSize());*/
		
		// DEBUG
		int debugRowCount = 1;
		g2.setColor(Design.INFO_PANEL_FOREGROUND_COLOR);
		g2.setFont(Design.INFO_PANEL_DEBUG_FONT);
		g2.drawString("Position: " + player.getPosition(), 10, (debugRowCount++)*g2.getFont().getSize());
		g2.drawString("isRunning: " + gameState.isRunning(), 10, (debugRowCount++)*g2.getFont().getSize());
	}

}
