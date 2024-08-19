package game.uielements;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;

import game.Display;
import game.Display.UI;
import game.Transition;
import misc.Console;
import misc.Tools;
import misc.design.Design;

public class DialogWindow extends UIElement {

	public final String title;
	
	public final String message;
	
	private Transition opaqueTransition;
	
	private Transition textTransition;
	
	private Transition popTransition = null;

	public DialogWindow(String title, String message, DialogWindowOption[] options) {
		super(0, 0, 0, 0);
		this.title = title;
		this.message = message;
		
		for(DialogWindowOption o : options) {
			children.add(new Button(o.title, 0, 0, Button.RELATIVE_TO_TOP_RIGHT) {
				@Override
				public void onPressed() {
					o.onSelect();
				}
			}.withButtonStyle(Design.BUTTON_GREEN_STYLE));
		}
		
		opaqueTransition	= new Transition(0, Design.DIALOG_WINDOW_BACKGROUND_COLOR.getAlpha(), 450);
		textTransition		= new Transition(0, message.length(), System.currentTimeMillis()+200, System.currentTimeMillis()+1000).withEaseDegree(1);
	}
	
	public void pop() {
		popTransition		= new Transition(1, 0, 350);
		opaqueTransition	= new Transition(opaqueTransition.getValue(), 0, 350);
		children.clear();
	}

	@Override
	public void paint(Graphics2D g2, Display display) {
		UI ui = display.ui;
		
		int opaque		= (int) opaqueTransition.getValue();
		int opaqueToMax = (int) (Math.abs((opaqueTransition.getValue()-opaqueTransition.from)/(opaqueTransition.to-opaqueTransition.from)) * 255);
		
		// BACKGROUND
		g2.setColor(Tools.rgbWithOpaque(Design.DIALOG_WINDOW_BACKGROUND_COLOR, opaque));
		g2.fillRect(0, 0, ui.getWidth(), ui.getHeight());
		
		// PANEL
		width	= Math.min(ui.getWidth()  * 3/4, Design.DIALOG_WINDOW_MAX_WIDTH);
		// height	= Math.min(ui.getHeight() * 2/3, Design.DIALOG_WINDOW_MAX_HEIGHT);
		
		if(popTransition != null)
			width *= popTransition.getValue();
		
		x = ui.getWidth()/2 - width/2;
		y = ui.getHeight()/2 - height/2;
		
		g2.setColor(Tools.rgbWithOpaque(Design.DIALOG_WINDOW_PANEL_COLOR, opaqueToMax));
		g2.fillRoundRect((int) x, (int) y, (int) width, (int) height, Design.DIALOG_WINDOW_BORDER_RADIUS, Design.DIALOG_WINDOW_BORDER_RADIUS);
		
		// BORDER
		g2.setStroke(Design.BUTTON_BORDER_STROKE);
		g2.setColor(Tools.rgbWithOpaque(Design.DIALOG_WINDOW_BORDER_COLOR, opaqueToMax));
		
		g2.drawRoundRect((int) x, (int) y, (int) width, (int) height, Design.DIALOG_WINDOW_BORDER_RADIUS, Design.DIALOG_WINDOW_BORDER_RADIUS);
		
		g2.setStroke(new BasicStroke());
		
		if(popTransition != null) {
			if(popTransition.isDone())
				display.dialogs.remove(this);
			return;
		}
		
		// TITLE
		int titleY = (int) y + g2.getFontMetrics().getHeight() + Design.DIALOG_WINDOW_CONTENT_PADDING/2;
		
		g2.setColor(Tools.rgbWithOpaque(Design.DIALOG_WINDOW_FOREGROUND_COLOR, opaqueToMax));
		g2.setFont(Design.DIALOG_WINDOW_TITLE_FONT);
		g2.drawString(title, (int) x + Design.DIALOG_WINDOW_CONTENT_PADDING, titleY);
		
		// INFO TEXT
		g2.setFont(Design.DIALOG_WINDOW_MESSAGE_FONT);
		
		String[] linesArray = message.substring(0, (int) textTransition.getValue()).split("\n");
		
		ArrayList<String> lines = new ArrayList<>();
		for(String l : linesArray)
			lines.add(l);
		
		int latestLineY = titleY;
		for(int i = 0; i<lines.size(); i++) {
			// AUTO LINE BREAK
			String lineOverflow = "";
			while(g2.getFontMetrics().stringWidth(lines.get(i)) > width - 2 * Design.DIALOG_WINDOW_CONTENT_PADDING) {
				String lastWord = Tools.getLastWord(lines.get(i));
				if(lastWord.equals(lines.get(i)))
					break;
				
				lines.set(i, lines.get(i).substring(0, lines.get(i).length() - lastWord.length() - 1));
				lineOverflow += " " + lastWord;
			}
			
			if(lineOverflow.length() > 0)
				lines.add(i+1, lineOverflow.substring(1));
			
			g2.drawString(lines.get(i), (int) x + Design.DIALOG_WINDOW_CONTENT_PADDING, latestLineY = titleY + (i+1) * g2.getFontMetrics().getHeight());
		}
		
		// OPTIONS
		int maxButtonHeight = 0;
		if(textTransition.isDone()) {
			int optionsXOffset = 0;
			for(UIElement c : children) {
				c.x = (int) x + Design.DIALOG_WINDOW_CONTENT_PADDING + optionsXOffset;
				c.y = latestLineY + Design.DIALOG_WINDOW_CONTENT_PADDING;
				
				optionsXOffset += c.width + Design.DIALOG_WINDOW_CONTENT_PADDING;
				
				c.paint(g2, display);
				
				if(c.height > maxButtonHeight)
					maxButtonHeight = (int) c.height;
			}
		}
		
		height = (latestLineY-y) + 2 * Design.DIALOG_WINDOW_CONTENT_PADDING + maxButtonHeight;
		height = ((int) (height/10))*10f;
	}

}
