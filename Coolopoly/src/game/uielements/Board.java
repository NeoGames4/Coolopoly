package game.uielements;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
import game.properties.Properties;
import game.properties.Property;
import misc.Constants;
import misc.FloatRect;
import misc.Tools;
import misc.Vector2D;
import misc.design.ColorPair;
import misc.design.Design;

public class Board extends UIElement {
	
	private final BufferedImage boardImage;
	
	public final HashMap<String, HashMap<Integer, ColorPair>> highlightedFields = new HashMap<>();
	
	private final static int	FIELD_HIGHLIGHT_FROM = 0,
								FIELD_HIGHLIGHT_TO   = 1;
	
	private Transition fieldHighlightTransition = new Transition(FIELD_HIGHLIGHT_FROM, FIELD_HIGHLIGHT_TO, 10);
	
	public boolean showPlayerNames = false;

	public Board() {
		super(0, 0, 0, 0);
		
		boardImage = Design.getImage("board.png");
		width = height = boardImage.getWidth();
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
		
		if(position == 0)
			return new FloatRect(width/2-cornerFieldWidth-paddingWidth,	height/2-cornerFieldWidth-paddingWidth,	cornerFieldWidth,	cornerFieldWidth);
		if(position == sideFields-1)
			return new FloatRect(-width/2+paddingWidth,					height/2-cornerFieldWidth-paddingWidth,	cornerFieldWidth,	cornerFieldWidth);
		if(position == 2*(sideFields-1))
			return new FloatRect(-width/2+paddingWidth,					-height/2+paddingWidth,					cornerFieldWidth,	cornerFieldWidth);
		if(position == 3*(sideFields-1))
			return new FloatRect(width/2-cornerFieldWidth-paddingWidth,	-height/2+paddingWidth,					cornerFieldWidth,	cornerFieldWidth);
		
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
		
		float playerSize = Design.BOARD_PLAYER_SIZE_RATIO*width;
		
		float	x = fieldBounds.x + fieldBounds.width/2f - playerSize/2f,
				y = fieldBounds.y + fieldBounds.height/2f - playerSize/2f,
				a = calculateAngle(p.getPosition());
		
		return new CameraState(x, y, 0, (float) Math.toRadians(a));
	}
	
	private CameraState calculateHousePosition(Property prop, float width, float height, int index, int total) {
		FloatRect fieldBounds = getFieldBounds(prop.position);
		
		boolean isHotel = total >= prop.rent.length-1;
		
		float houseMaxHeight = Design.BOARD_HOUSE_SLOT_SIZE_RATIO * this.width;
		float totalWidth = (isHotel ? 1 : total) * width;
		
		float	x = fieldBounds.x + fieldBounds.width/2f - totalWidth/2 + index * width,
				y = fieldBounds.y + houseMaxHeight/2 - height/2,
				a = calculateAngle(prop.position);
		
		if(a == 90 || a == 270)
			y -= houseMaxHeight;			// TODO fix this solution
		
		return new CameraState(x, y, 0, (float) Math.toRadians(a));
	}
	
	private CameraState calculatePropertyCenter(Property prop) {
		FloatRect fieldBounds = getFieldBounds(prop.position);
		
		float	x = fieldBounds.x + fieldBounds.width/2f,
				y = fieldBounds.y + fieldBounds.height/2f,
				a = calculateAngle(prop.position);
		
		return new CameraState(x, y, 0, (float) Math.toRadians(a));
	}
	
	public float calculateAngle(int position) {
		final int sideFields = Constants.BOARD_FIELDS_AMOUNT/4 + 1;
		if(position < sideFields-1) {
			return 0;
		}
		else if(position == sideFields-1) {
			return 45;
		}
		else if(position < 2*(sideFields-1)) {
			return 90;
		}
		else if(position == 2*(sideFields-1)) {
			return 135;
		}
		else if(position < 3*(sideFields-1)) {
			return 180;
		}
		else if(position == 3*(sideFields-1)) {
			return 225;
		}
		else {
			return 270;
		}
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
		
		if(gameState == null)
			return;
		
		// HIGHLIGHTED FIELDS
		if(!highlightedFields.isEmpty()) {
			if(fieldHighlightTransition.isDone()) {
				fieldHighlightTransition = new Transition(
					fieldHighlightTransition.from == FIELD_HIGHLIGHT_TO ? FIELD_HIGHLIGHT_FROM	: FIELD_HIGHLIGHT_TO,
					fieldHighlightTransition.from == FIELD_HIGHLIGHT_TO ? FIELD_HIGHLIGHT_TO	: FIELD_HIGHLIGHT_FROM,
					Design.BOARD_HIGHLIGHT_TRANSITION_DURATION
				).withEaseDegree(1);
			}
			
			Iterator<Entry<String, HashMap<Integer, ColorPair>>> highlightedSubject = highlightedFields.entrySet().iterator();
			while(highlightedSubject.hasNext()) {
				Iterator<Entry<Integer, ColorPair>> highlighted = highlightedSubject.next().getValue().entrySet().iterator();
				g2.setStroke(
					new BasicStroke(
						Math.round(Math.max(Design.BOARD_HIGHLIGHT_FIELDS_STROKE_WIDTH * fieldHighlightTransition.getValue(), 2))
					)
				);
			
				while(highlighted.hasNext()) {
					Entry<Integer, ColorPair> i = highlighted.next();
					
					FloatRect r = getFieldBounds(i.getKey());
					
					Property prop = Properties.getProperties()[i.getKey()];
					Player propHolder = gameState.getPropertyOwner(prop);
					boolean mortgaged = propHolder != null && propHolder.properties.get(prop) < 0;
					
					float eX = r.x*cZoom;
					float eY = r.y*cZoom;
					int eW = (int) (r.width*cZoom);
					int eH = (int) (r.height*cZoom);
					
					Rectangle fieldRect = new Rectangle((int) (cX + eX + ui.getWidth()/2), (int) (cY + eY + ui.getHeight()/2), (int) eW, (int) eH);
					
					g2.setColor(Tools.rgbWithOpaque(mortgaged ? Design.PROPERTIES_CARD_MORTGAGE_BACKGROUND_COLOR : i.getValue().colorA, 100));
					g2.fillRoundRect(fieldRect.x, fieldRect.y, fieldRect.width, fieldRect.height, 2, 2);
					
					g2.setColor(Tools.colorLerp(i.getValue().colorA, i.getValue().colorB, 1-fieldHighlightTransition.getValue()));
					g2.drawRoundRect(fieldRect.x, fieldRect.y, fieldRect.width, fieldRect.height, 2, 2);
					
					String tooltipText = propHolder != null
											? propHolder.name + "’" + (propHolder.name.endsWith("s") ? "" : "s") + " property (" + prop.title + ")"
											: null;
					
					if(tooltipText != null && fieldRect.contains(display.getLatestMousePosition()) && camera.zoomOut)	// TODO remove camera.zoomOut and fix rotation
						display.tooltipText = tooltipText;
					else if(display.tooltipText != null && display.tooltipText.equals(tooltipText))
						display.tooltipText = null;
				}
			}
		}
		
		// HOUSES
		for(Player p : gameState.getPlayers()) {
			Iterator<Entry<Property, Integer>> i = p.properties.entrySet().iterator();
			while(i.hasNext()) {
				Entry<Property, Integer> prop = i.next();
				
				/*CameraState pos = calculateHousePosition(prop.getKey(), 0, 1);
				
				float pX = cX + pos.x * cZoom + ui.getWidth()/2;
				float pY = cY + pos.y * cZoom + ui.getHeight()/2;
				int pS = (int) (playerSize*cZoom);
				
				g2.rotate(pos.angle, (int) (pX+pS/2), (int) (pY+pS/2));*/
				
				int total = prop.getValue();
				
				CameraState propCenter = calculatePropertyCenter(prop.getKey());
				
				float pcX = cX + propCenter.x * cZoom + ui.getWidth()/2;
				float pcY = cY + propCenter.y * cZoom + ui.getHeight()/2;
				
				g2.rotate(propCenter.angle, (int) pcX, (int) pcY);
				
				if(total >= prop.getKey().rent.length-1) {	// HOTEL
					float imgHeightWidthRadio = Design.BOARD_HOTEL_IMAGE.getHeight()/((float) Design.BOARD_HOTEL_IMAGE.getWidth());
					float imgHeight	= Design.BOARD_HOUSE_SLOT_SIZE_RATIO * width;
					float imgWidth	= imgHeight/imgHeightWidthRadio;
					
					CameraState pos = calculateHousePosition(prop.getKey(), imgWidth, imgHeight, 0, total);
					
					float	pX = cX + pos.x * cZoom + ui.getWidth()/2,
							pY = cY + pos.y * cZoom + ui.getHeight()/2;
					
					int	pW = Math.round(imgWidth*cZoom),
						pH = Math.round(imgHeight*cZoom);
					
					g2.drawImage(Design.BOARD_HOTEL_IMAGE, (int) pX, (int) pY, (int) pW, (int) pH, null);
				} else {									// HOUSES
					float imgHeightWidthRadio = Design.BOARD_HOUSE_IMAGE.getHeight()/((float) Design.BOARD_HOUSE_IMAGE.getWidth());
					float imgHeight	= Design.BOARD_HOUSE_SIZE_RATIO * width;
					float imgWidth	= imgHeight/imgHeightWidthRadio;
					
					int		pW = Math.round(imgWidth*cZoom),
							pH = Math.round(imgHeight*cZoom);
					
					for(int j = 0; j<total; j++) {
						CameraState pos = calculateHousePosition(prop.getKey(), imgWidth, imgHeight, j, total);
						
						float	pX = cX + pos.x * cZoom + ui.getWidth()/2,
								pY = cY + pos.y * cZoom + ui.getHeight()/2;
						
						g2.drawImage(Design.BOARD_HOUSE_IMAGE, (int) pX, (int) pY, (int) pW, (int) pH, null);
					}
				}
				
				g2.rotate(-propCenter.angle, (int) pcX, (int) pcY);
			}
		}
		
		// PLAYERS
		final float playerSize = Design.BOARD_PLAYER_SIZE_RATIO*width;
		
		g2.setColor(Design.INFO_PANEL_FOREGROUND_COLOR);
		g2.setFont(Design.INFO_PANEL_PLAYER_LIST_FONT);
		
		for(int i = 0; i<gameState.getPlayers().size(); i++) {
			Player p = gameState.getPlayers().get(i);
			CameraState pos = calculatePlayerPosition(p, i);
			
			if(p.xTransition.to != pos.x)
				p.xTransition = new Transition(p.xTransition.getValue(), pos.x, Design.BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION).withEaseDegree(1);
			if(p.yTransition.to != pos.y)
				p.yTransition = new Transition(p.yTransition.getValue(), pos.y, Design.BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION).withEaseDegree(1);
			if(p.aTransition.to != pos.angle)
				p.aTransition = new Transition(p.aTransition.getValue(), pos.angle, Design.BOARD_PLAYER_MOVEMENT_TRANSITION_DURATION).withEaseDegree(1);
			
			float pX = cX + p.xTransition.getValue() * cZoom + ui.getWidth()/2;
			float pY = cY + p.yTransition.getValue() * cZoom + ui.getHeight()/2;
			int pS = (int) (playerSize*cZoom);
			
			
			g2.rotate(p.aTransition.getValue(), (int) (pX+pS/2), (int) (pY+pS/2));
			
			g2.drawImage(p.image != null ? p.image : Design.IMAGE_NOT_FOUND, (int) pX, (int) pY, (int) pS, (int) pS, null);
			
			if(showPlayerNames) {
				String playerName = p.name;
				
				float nX = cX + (p.xTransition.getValue() + playerSize/2f - g2.getFontMetrics().stringWidth(playerName)/2f) * cZoom + ui.getWidth()/2;
				float nY = cY + (p.yTransition.getValue()-Constants.BOARD_CORNER_FIELD_SIZE_RATIO*width/2f) * cZoom + ui.getHeight()/2;
				
				g2.drawString(playerName, nX, nY);
			}
			
			g2.rotate(-p.aTransition.getValue(), (int) (pX+pS/2), (int) (pY+pS/2));
		}
		
		g2.rotate(-cAngle, ui.getWidth()/2, ui.getHeight()/2);
	}
	
	public static float distanceBetween(Vector2D a, Vector2D b) {
		return (float) Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2));
	}

}
