package game.uielements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import game.Display;
import game.Display.UI;
import game.GameState;
import game.Player;
import game.Transition;
import game.camera.Camera;
import game.camera.CameraState;
import misc.Constants;
import misc.FloatRect;
import misc.Tools;
import misc.Vector2D;
import misc.design.Design;

public class Board extends UIElement {
	
	private final BufferedImage boardImage;
	
	public final HashMap<Integer, Color> highlightedFields = new HashMap<>();
	
	private final static int	FIELD_HIGHLIGHT_FROM = 0,
								FIELD_HIGHLIGHT_TO   = 1;
	
	private Transition fieldHighlightTransition = new Transition(FIELD_HIGHLIGHT_FROM, FIELD_HIGHLIGHT_TO, 10);

	public Board() {
		super(0, 0, 0, 0);
		
		boardImage = Design.getImage("board.png");
		width = height = boardImage.getWidth();
		
		highlightedFields.put(3, Design.BOARD_HIGHLIGHT_ACCENT_FLASH_COLOR);
	}
	
	public CameraState getCameraStateFocus(int boardPos) {
		boardPos %= Constants.BOARD_FIELDS_AMOUNT;
		
		FloatRect fieldBounds = getFieldBounds(boardPos);
		
		float x = fieldBounds.x + fieldBounds.width/2;
		float y = fieldBounds.y + fieldBounds.height/2;
		float angle = 0;
		
		float sideFields = Constants.BOARD_FIELDS_AMOUNT/4 + 1;
		
		if(boardPos < sideFields-1)
			angle = 0;
		else if(boardPos < 2 * (sideFields-1))
			angle = -90;
		else if(boardPos < 3 * (sideFields-1))
			angle = -180;
		else
			angle = -270;
		
		return new CameraState(x, y, Constants.BOARD_FIELD_FOCUS_ZOOM, angle);
	}
	
	public FloatRect getFieldBounds(int position) {
		position %= Constants.BOARD_FIELDS_AMOUNT;
		
		final int sideFields = Constants.BOARD_FIELDS_AMOUNT/4 + 1;
		
		float cornerFieldWidth = width*Constants.BOARD_CORNER_FIELD_SIZE_RATIO;
		float sideFieldWidth = (width - 2f*cornerFieldWidth - Constants.BOARD_PADDING_SIZE_RATIO*width)/(sideFields-2f);
		float paddingWidth = width*Constants.BOARD_PADDING_SIZE_RATIO/2f;
		
		switch(position) {
			case 0:
				return new FloatRect(width/2-cornerFieldWidth-paddingWidth,	height/2-cornerFieldWidth-paddingWidth,	cornerFieldWidth,	cornerFieldWidth);
			case sideFields-1:
				return new FloatRect(-width/2+paddingWidth,					height/2-cornerFieldWidth-paddingWidth,	cornerFieldWidth,	cornerFieldWidth);
			case 2*(sideFields-1):
				return new FloatRect(-width/2+paddingWidth,					-height/2+paddingWidth,					cornerFieldWidth,	cornerFieldWidth);
			case 3*(sideFields-1):
				return new FloatRect(width/2-cornerFieldWidth-paddingWidth,	-height/2+paddingWidth,					cornerFieldWidth,	cornerFieldWidth);
		}
		
		float x = 0, y = 0, w = 1, h = 1;
		
		if(position < sideFields-1) {
			x = width/2-cornerFieldWidth-paddingWidth - sideFieldWidth*position;
			y = +width/2f - cornerFieldWidth - paddingWidth;
			w = sideFieldWidth;
			h = cornerFieldWidth;
		}
		else if(position < 2 * (sideFields-1)) {
			position -= sideFields-1;
			x = -width/2f + paddingWidth;
			y = height/2-cornerFieldWidth-paddingWidth - sideFieldWidth*position;
			w = cornerFieldWidth;
			h = sideFieldWidth;
		}
		else if(position < 3 * (sideFields-1)) {
			position -= 2*(sideFields-1);
			x = -(width/2-cornerFieldWidth-paddingWidth) + sideFieldWidth*(position-1);
			y = -width/2f + paddingWidth;
			w = sideFieldWidth;
			h = cornerFieldWidth;
		}
		else {
			position -= 3*(sideFields-1);
			x = +width/2f - cornerFieldWidth - paddingWidth;
			y = -(height/2-cornerFieldWidth-paddingWidth) + sideFieldWidth*(position-1);
			w = cornerFieldWidth;
			h = sideFieldWidth;
		}
		
		return new FloatRect(x, y, w, h);
	}
	
	private CameraState calculatePlayerPosition(Player p, int offsetPosition) {		// TODO offset
		FloatRect fieldBounds = getFieldBounds(p.getPosition());
		final int sideFields = Constants.BOARD_FIELDS_AMOUNT/4 + 1;
		final float playerSize = Design.BOARD_PLAYER_SIZE_RATIO*width;
		
		float	x = fieldBounds.x + fieldBounds.width/2f - playerSize/2f,
				y = fieldBounds.y + fieldBounds.height/2f - playerSize/2f;
		
		float a;
		if(p.getPosition() < sideFields-1) {
			a = 0;
		}
		else if(p.getPosition() < 2*(sideFields-1)) {
			a = 90;
		}
		else if(p.getPosition() < 3*(sideFields-1)) {
			a = 180;
		} else {
			a = 270;
		}
		
		return new CameraState(x, y, 0, (float) Math.toRadians(a));
	}
	
	@Override
	public boolean isMouseHovering(MouseEvent e, UI ui) { return false; }

	@Override
	public void paint(Graphics2D g2, Display display) {
		GameState gameState = display.server.getLatestGameState();
		Camera camera = display.camera;
		UI ui = display.ui;
		
		float cZoom = (float) Math.exp(camera.zoom);
		width = height = Math.min(ui.getWidth(), ui.getHeight()-Design.INFO_PANEL_HEIGHT);
		int dWidth = (int) (width*cZoom);
		
		float cX = -camera.x*cZoom;
		float cY = -camera.y*cZoom - Design.INFO_PANEL_HEIGHT/2;
		
		double cAngle = Math.toRadians(camera.angle);
		
		g2.rotate(cAngle, ui.getWidth()/2, ui.getHeight()/2);
		
		// BOARD
		g2.drawImage(boardImage, (int) (cX - dWidth/2 + ui.getWidth()/2), (int) (cY - dWidth/2 + ui.getHeight()/2), (int) dWidth, (int) dWidth, null);
		
		// HIGHLIGHTED FIELDS
		if(!highlightedFields.isEmpty()) {
			if(fieldHighlightTransition.isDone()) {
				fieldHighlightTransition = new Transition(
					fieldHighlightTransition.from == FIELD_HIGHLIGHT_TO ? FIELD_HIGHLIGHT_FROM	: FIELD_HIGHLIGHT_TO,
					fieldHighlightTransition.from == FIELD_HIGHLIGHT_TO ? FIELD_HIGHLIGHT_TO	: FIELD_HIGHLIGHT_FROM,
					Design.BOARD_HIGHLIGHT_TRANSITION_DURATION
				).withEaseDegree(1);
			}
			
			Iterator<Entry<Integer, Color>> highlighted = highlightedFields.entrySet().iterator();
			g2.setStroke(new BasicStroke((int) Math.max(Design.BOARD_HIGHLIGHT_FIELDS_STROKE_WIDTH * fieldHighlightTransition.getValue(), 2)));
			
			while(highlighted.hasNext()) {
				Entry<Integer, Color> i = highlighted.next();
				
				g2.setColor(Tools.colorLerp(i.getValue(), Design.BOARD_HIGHLIGHT_FLASH_COLOR, 1-fieldHighlightTransition.getValue()));
				FloatRect r = getFieldBounds(i.getKey());
				
				float eX = r.x*cZoom;
				float eY = r.y*cZoom;
				int eW = (int) (r.width*cZoom);
				int eH = (int) (r.height*cZoom);
				
				
				g2.drawRoundRect((int) (cX + eX + ui.getWidth()/2), (int) (cY + eY + ui.getHeight()/2), (int) eW, (int) eH, 2, 2);
				
			}
		}
		
		// PLAYERS
		final float playerSize = Design.BOARD_PLAYER_SIZE_RATIO*width;
		for(int i = 0; i<gameState.getPlayers().size(); i++) {
			Player p = gameState.getPlayers().get(i);
			CameraState pos = calculatePlayerPosition(p, i);
			
			if(p.xTransition.to != pos.x)
				p.xTransition = new Transition(p.xTransition.getValue(), pos.x, Design.BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION).withEaseDegree(1);
			if(p.yTransition.to != pos.y)
				p.yTransition = new Transition(p.yTransition.getValue(), pos.y, Design.BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION).withEaseDegree(1);
			if(p.yTransition.to != pos.angle)
				p.aTransition = new Transition(p.aTransition.getValue(), pos.angle, Design.BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION).withEaseDegree(1);
			
			float pX = cX + p.xTransition.getValue() * cZoom + ui.getWidth()/2;
			float pY = cY + p.yTransition.getValue() * cZoom + ui.getHeight()/2;
			int pS = (int) (playerSize*cZoom);
			
			g2.rotate(p.aTransition.getValue(), (int) (pX+pS/2), (int) (pY+pS/2));
			
			g2.drawImage(p.image != null ? p.image : Design.IMAGE_NOT_FOUND, (int) pX, (int) pY, (int) pS, (int) pS, null);
			
			g2.rotate(-p.aTransition.getValue(), (int) (pX+pS/2), (int) (pY+pS/2));
		}
		
		g2.rotate(-cAngle, ui.getWidth()/2, ui.getHeight()/2);
	}
	
	public static float distanceBetween(Vector2D a, Vector2D b) {
		return (float) Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2));
	}

}
