package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.uielements.Board;
import game.uielements.UIElement;
import misc.Constants;

public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public final UI ui;
	
	public ArrayList<UIElement> uiElements;
	
	public final Camera camera;

	public Display() {
		super();
		
		setTitle(Constants.GAME_TITLE);
		// setIconImage();									// TODO
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {		// TODO
				System.exit(0);
			}
		});
		addComponentListener(new ComponentAdapter() {		// TODO camera position correction
			@Override
			public void componentResized(ComponentEvent e) {
			}
		});
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(!Constants.DEBUG)
			setSize(screenSize);
		else
			setSize((int) (screenSize.getWidth()*3./5), (int) (screenSize.getHeight()*3./4));
		setLocationRelativeTo(null);
		
		ui = new UI();
		setContentPane(ui);
		uiElements = new ArrayList<>();
		
		addKeyListener(new Controls());
		setFocusTraversalKeysEnabled(false);
		setAutoRequestFocus(true);
		
		camera = new Camera();
		
		setVisible(true);
		
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			updateGame();
			ui.updateUI();
		}, 50, (int) (1000d/Constants.FPS), TimeUnit.MILLISECONDS);
	}
	
	public void updateGame() {
		if(camera.freeMovement) {
			// CAMERA MOVEMENT
			float dx = 0;
			float dy = 0;
			
			if(Controls.W_DOWN) dy--;
			if(Controls.A_DOWN) dx--;
			if(Controls.S_DOWN) dy++;
			if(Controls.D_DOWN) dx++;
			
			if(dx != 0 && dy != 0) {
				float f = (float) Math.sqrt(2);
				dx /= f;
				dy /= f;
			}
			
			camera.x += dx*Constants.CAMERA_MOVEMENT_SPEED/ui.fps;
			camera.y += dy*Constants.CAMERA_MOVEMENT_SPEED/ui.fps;
			
			// ROTATION
			float dangle = 0;
			
			if(Controls.Q_DOWN) dangle++;
			if(Controls.E_DOWN) dangle--;
			
			camera.angle += dangle*Constants.CAMERA_ROTATION_SPEED/ui.fps;
			
			// CAMERA ZOOM
			float dz = 0;
			
			if(Controls.X_DOWN) dz++;
			if(Controls.Y_DOWN) dz--;
			
			camera.zoom += dz*Constants.CAMERA_ZOOM_SPEED/ui.fps;
			if(camera.zoom < Constants.MIN_CAMERA_ZOOM) camera.zoom = Constants.MIN_CAMERA_ZOOM;
			else if(camera.zoom > Constants.MAX_CAMERA_ZOOM) camera.zoom = Constants.MAX_CAMERA_ZOOM;
			
			if(Constants.DEBUG)
				System.out.println("\nFPS: " + ui.fps + "\nx: " + camera.x + "\ny: " + camera.y + "\nRotation: " + camera.angle + "\nZoom: " + camera.zoom + "\nBoard size: " + ui.board.width + ", " + ui.board.height + "\nPlayer position: " + ui.board.player.position);
		}
		
		if(Controls.TAB_DOWN) {
			long now = System.currentTimeMillis();
			CameraTransitionsBuilder tBuilder = new CameraTransitionsBuilder(camera)
					.withATransitions(
							new Transition(camera.angle, camera.angle + 270, 1000)
					);
			camera.transitionWith(tBuilder.build());
		}
		else if(Controls.F_DOWN && (camera.transition == null || camera.transition.isDone())) {
			ui.board.player.position = (int) (Constants.BOARD_FIELDS_AMOUNT*Math.random());
			
			CameraState focusPosition = ui.board.getCameraStateFocus(ui.board.player.position);
					
			long transitionTime = Math.max((long) (Board.distanceBetween(camera.getState().getPosition(), focusPosition.getPosition())*2), 500l);
			
			camera.transitionTo(focusPosition, transitionTime);
		}
		
		camera.update();
	}
	
	public class UI extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private final Board board;
		
		public float fps = Constants.FPS;
		public long latestFrame = System.currentTimeMillis();

		public UI() {
			super();
			board = new Board("You");
		}
		
		public void update() {
			repaint();
			
			// FPS
			long now = System.currentTimeMillis();	// TODO
			if(now != latestFrame)
				fps = 1000/(now - latestFrame);
			else
				fps = 1000;
			latestFrame = now;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			// BACKGROUND
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
			
			// BOARD
			board.paint(g2, Display.this);
			
			// UI ELEMENTS
			for(UIElement e : uiElements) {
				e.paint(g2, Display.this);
			}
		}
		
	}

}
