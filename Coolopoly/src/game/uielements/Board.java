package game.uielements;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Display;
import game.Display.UI;
import game.camera.Camera;
import game.camera.CameraState;
import launcher.Launcher;
import misc.Constants;

public class Board extends UIElement {
	
	private final BufferedImage boardImage;

	public Board() {
		super(0, 0, 0, 0);
		
		boardImage = Launcher.getImage("board.png");
		width = height = boardImage.getWidth();
	}
	
	public CameraState getCameraStateFocus(float boardPos) {
		boardPos %= Constants.BOARD_FIELDS_AMOUNT;
		int fieldWidth = (int) (width * Constants.BOARD_FIELDS_WIDTH_FACTOR);
		
		float x;
		float y;
		float angle;
		float sideFields = Constants.BOARD_FIELDS_AMOUNT/4 + 1;
		
		if(boardPos < sideFields-1) {
			x = (1/2.0f - (boardPos/sideFields)) * width - (fieldWidth/2.0f);
			y = (+width/2.0f) - (fieldWidth/2.0f);
			angle = 0;
		}
		else if(boardPos < 2 * (sideFields-1)) {
			boardPos -= sideFields-1;
			x = (-width/2.0f) + (fieldWidth/2.0f);
			y = (1/2.0f - (boardPos/sideFields)) * width - (fieldWidth/2.0f);
			angle = -90;
		}
		else if(boardPos < 3 * (sideFields-1)) {
			boardPos -= 2*(sideFields-1);
			x = (-1/2.0f + (boardPos/sideFields)) * width + (fieldWidth/2.0f);
			y = (-width/2.0f) + (fieldWidth/2.0f);
			angle = -180;
		}
		else {
			boardPos -= 3*(sideFields-1);
			x = (+width/2.0f) - (fieldWidth/2.0f);
			y = (-1/2.0f + (boardPos/sideFields)) * width + (fieldWidth/2.0f);
			angle = -270;
		}
		
		return new CameraState(x, y, Constants.BOARD_FIELD_FOCUS_ZOOM, angle);
	}
	
	@Override
	public boolean isMouseHovering(MouseEvent e) { return false; }
	
	@Override
	public boolean isPressed(MouseEvent e) { return false; }

	@Override
	public void paint(Graphics2D g2, Display display) {
		Camera camera = display.camera;
		UI ui = display.ui;
		
		float cZoom = (float) Math.exp(camera.zoom);
		width = height = Math.min(ui.getWidth(), ui.getHeight());
		int dWidth = (int) (width*cZoom);
		
		float cX = -camera.x*cZoom;
		float cY = -camera.y*cZoom;
		
		double cAngle = Math.toRadians(camera.angle);
		
		g2.rotate(cAngle, ui.getWidth()/2, ui.getHeight()/2);
		
		g2.drawImage(boardImage, (int) (cX - dWidth/2 + ui.getWidth()/2), (int) (cY - dWidth/2 + ui.getHeight()/2), (int) dWidth, (int) dWidth, null);
		
		g2.rotate(-cAngle, ui.getWidth()/2, ui.getHeight()/2);
	}
	
	public static float distanceBetween(Vector2D a, Vector2D b) {
		return (float) Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2));
	}

}
