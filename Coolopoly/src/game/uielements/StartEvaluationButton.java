package game.uielements;

import java.awt.Graphics2D;

import game.Display;
import game.GameState;
import misc.Constants;

public class StartEvaluationButton extends Button {
	
	private final Display display;

	public StartEvaluationButton(Display display, int x, int y, int relativeTo) {
		super("Start", x, y, relativeTo);
		this.display = display;
		setSelectable(true);
	}
	
	@Override
	public void onPressed() {
		
		if(display.server.getLatestGameState().isRunning()) {
			if(isSelected()) {
				display.dialogs.add(
					new DialogWindow(
						"Are you sure?",
						"As soon as all participants have voted for the evaluation, the game will end and a winner will be chosen.",
						new DialogWindowOption[] {
							new DialogWindowOption("Confirm") {
								@Override
								public void onSelect() {
									display.server.vote(Constants.GAME_VOTE_EVALUATE, true);
									if(!display.dialogs.isEmpty())
										display.dialogs.get(0).pop();
								}
							},
							new DialogWindowOption("Cancel") {
								@Override
								public void onSelect() {
									setSelected(false);
									if(!display.dialogs.isEmpty())
										display.dialogs.get(0).pop();
								}
							}
						}
					).setHotkeyPopable(false)
				);
			} else {
				display.server.vote(Constants.GAME_VOTE_EVALUATE, false);
			}
		} else if(display.server.getLatestGameState().isOver()) {
			display.server.requestGameState();
		} else {
			display.server.vote(Constants.GAME_VOTE_START, isSelected());
		}
	}
	
	@Override
	public void paint(Graphics2D g2, Display display) {
		GameState gameState = this.display.server.getLatestGameState();
		
		boolean updateSelection = false;
		if(gameState.isWaiting()) {
			updateSelection = !label.equals(label = "I'm ready!");
		} else if(gameState.isRunning()) {
			updateSelection = !label.equals(label = "Evaluate");
		} else if(gameState.isOver()) {
			updateSelection = !label.equals(label = "Restart");
		} else return;
		
		if(updateSelection)
			setSelected(false);
		
		super.paint(g2, display);
	}

}
