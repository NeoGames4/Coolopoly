package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.camera.Camera;
import game.camera.CameraState;
import game.uielements.Board;
import game.uielements.Button;
import game.uielements.DialogWindow;
import game.uielements.InfoPanel;
import game.uielements.StartEvaluationButton;
import game.uielements.UIElement;
import game.uielements.UIOverlay;
import launcher.Launcher;
import misc.Constants;
import misc.Notification;
import misc.design.Design;
import networking.ServerConnection;

public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public final ServerConnection server;
	
	public final UI ui;
	
	public final ArrayList<UIElement> uiElements;
	public final ArrayList<DialogWindow> dialogs;
	public final ArrayList<Notification> notifications = new ArrayList<>();
	public final ArrayList<UIOverlay> overlays = new ArrayList<>();
	
	
	public String tooltipText = null;
	
	public final Camera camera;
	
	private Transition zoomTransition;
	
	private Point latestMousePosition = new Point(0, 0);
	private long ticks = 0;
	
	private final Button	startEvalButton,
							refreshButton;

	public Display(ServerConnection server) {
		super();
		
		this.server = server;
		server.display = this;
		
		setTitle(Constants.GAME_TITLE + " as " + server.getUsername());
		setIconImage(Design.getImage("app-icon.png"));
		try {
			// Class.forName("com.apple.eawt.Application");
			// com.apple.eawt.Application.getApplication().setDockIconImage(Design.getImage("app-icon.png")); // TODO
		} catch(Exception e) {}
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				Launcher.main(new String[] {server.getUsername(), server.address, server.port + ""});
			}
		});
		/*addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
			}
		});*/
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(!Constants.DEBUG)
			setSize(screenSize);
		else
			setSize((int) (screenSize.getWidth()*3./5), (int) (screenSize.getHeight()*3./4));
		setLocationRelativeTo(null);
		
		ui = new UI();
		setContentPane(ui);
		uiElements = new ArrayList<>();
		
		int lUIIndex = uiElements.size();
		uiElements.add(lUIIndex, new InfoPanel(this));
		uiElements.add(startEvalButton = new StartEvaluationButton(this, 10, 10, Button.RELATIVE_TO_TOP_RIGHT));
		uiElements.add(refreshButton = new Button("refresh()", 50, 10, Button.RELATIVE_TO_TOP_RIGHT) {
			@Override
			public void onPressed() {
				server.requestGameState();
			}
		});
		
		dialogs = new ArrayList<>();
		
		addKeyListener(new Controls());
		setFocusTraversalKeysEnabled(false);
		setAutoRequestFocus(true);
		
		camera = new Camera();
		zoomTransition = new Transition(0, camera.zoomOut ? 1 : 0, 10);
		
		setVisible(true);
		
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			updateGame();
			ui.updateUI();
			ticks++;
		}, 50, (int) (1000d/Constants.FPS), TimeUnit.MILLISECONDS);
	}
	
	public void updateGame() {
		if(server.getLatestGameState() == null) {
			return;
		}
		
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
			
			camera.x += dx*Constants.FREE_CAMERA_MOVEMENT_SPEED/ui.fps;
			camera.y += dy*Constants.FREE_CAMERA_MOVEMENT_SPEED/ui.fps;
			
			// ROTATION
			float dangle = 0;
			
			if(Controls.Q_DOWN) dangle++;
			if(Controls.E_DOWN) dangle--;
			
			camera.angle += dangle*Constants.FREE_CAMERA_ROTATION_SPEED/ui.fps;
			
			// CAMERA ZOOM
			float dz = 0;
			
			if(Controls.X_DOWN) dz++;
			if(Controls.Y_DOWN) dz--;
			
			camera.zoom += dz*Constants.FREE_CAMERA_ZOOM_SPEED/ui.fps;
			if(camera.zoom < Constants.MIN_CAMERA_ZOOM) camera.zoom = Constants.MIN_CAMERA_ZOOM;
			else if(camera.zoom > Constants.MAX_CAMERA_ZOOM) camera.zoom = Constants.MAX_CAMERA_ZOOM;
			
			// if(Constants.DEBUG)
				// System.out.println("\nFPS: " + ui.fps + "\nx: " + camera.x + "\ny: " + camera.y + "\nRotation: " + camera.angle + "\nZoom: " + camera.zoom + "\nBoard size: " + ui.board.width + ", " + ui.board.height + "\nPlayer position: " + player.getPosition());
		}
		
		if(Controls.TAB_DOWN) {
			Controls.TAB_DOWN = false;
			camera.zoomOut = !camera.zoomOut;
		}
		
		if(camera.zoomOut) {
			if(camera.transitionTo(camera.getState().withX(0).withY(0).withZoom(0).withAngle(0), 350))
				zoomTransition = new Transition(zoomTransition.getValue(), 1, 350);
			
			if(camera.transition == null)
				ui.board.showPlayerNames = true;
		} else if(!camera.freeMovement) {
			ui.board.showPlayerNames = false;
			
			CameraState target = ui.board.getCameraStateFocus(server.getLatestGameState().currentPlayer().getPosition());
			
			double distance = Board.distanceBetween(camera.getState().getPosition(), target.getPosition());
			
			camera.transitionTo(target, (long) Math.max(distance * Design.CAMERA_PLAYER_TRANSITION_DURATION, 350));
			
			if(zoomTransition.to != 0)
				zoomTransition = new Transition(zoomTransition.getValue(), 0, 350);
		}
		
		camera.update();
		
		if(Controls.ESCAPE_DOWN) {
			if(dialogs.size() > 0 && dialogs.get(0).isHotkeyPopable)
				dialogs.get(0).pop();
			else if(overlays.size() > 0 && overlays.get(0).isHotkeyPopable)
				overlays.get(0).pop();
			Controls.ESCAPE_DOWN = false;
		}
	}
	
	public Transition getZoomTransition() {
		return zoomTransition;
	}
	
	public Point getLatestMousePosition() {
		return latestMousePosition;
	}
	
	public long getTicks() {
		return ticks;
	}
	
	public class UI extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		public final Board board;
		
		public float fps = Constants.FPS;
		public long latestFrame = System.currentTimeMillis();

		public UI() {
			super();
			board = new Board();
			
			addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						// DIALOG WINDOWS
						if(!dialogs.isEmpty()) {
							for(UIElement c : new ArrayList<>(dialogs.get(0).children))
								if(c.isMouseHovering(e, UI.this))
									c.onMousePressed();
							return;
						}
						
						// OVERLAY
						if(!overlays.isEmpty()) {
							for(UIElement c : new ArrayList<>(overlays.get(0).children))
								if(c.isMouseHovering(e, UI.this))
									c.onMousePressed();
							return;
						}
						
						// UI
						for(UIElement c : new ArrayList<>(uiElements))
							if(c.isMouseHovering(e, UI.this))
								c.onMousePressed();
					}
	
					@Override
					public void mouseReleased(MouseEvent e) {
						// DIALOG WINDOWS
						if(!dialogs.isEmpty()) {
							for(UIElement c : new ArrayList<>(dialogs.get(0).children))
								if(c.isMouseHovering(e, UI.this))
									c.onMouseReleased();
							return;
						}
						
						// OVERLAY
						if(!overlays.isEmpty()) {
							for(UIElement c : new ArrayList<>(overlays.get(0).children))
								if(c.isMouseHovering(e, UI.this))
									c.onMouseReleased();
							return;
						}
						
						// UI
						for(UIElement c : new ArrayList<>(uiElements))
							if(c.isMouseHovering(e, UI.this))
								c.onMouseReleased();
					}
				}
			);
			
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					// LATEST MOUSE POSITION
					latestMousePosition = new Point(e.getX(), e.getY());
					
					// DIALOG WINDOWS
					if(!dialogs.isEmpty()) {
						for(UIElement c : new ArrayList<>(dialogs.get(0).children)) {
							if(c.isMouseHovering(e, UI.this))
								c.onMouseEntered();
							else
								c.onMouseExited();
						}
						return;
					}
					
					// OVERLAY
					if(!overlays.isEmpty()) {
						for(UIElement c : new ArrayList<>(overlays.get(0).children)) {
							if(c.isMouseHovering(e, UI.this))
								c.onMouseEntered();
							else
								c.onMouseExited();
						}
						return;
					}
					
					// UI
					for(UIElement c : new ArrayList<>(uiElements)) {
						if(c.isMouseHovering(e, UI.this))
							c.onMouseEntered();
						else
							c.onMouseExited();
					}
				}
			}
		);
		}
		
		public void update() {
			repaint();
			
			// FPS
			long now = System.currentTimeMillis();
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
			for(UIElement e : new ArrayList<>(uiElements)) {
				e.paint(g2, Display.this);
			}
			
			if(!overlays.isEmpty())
				overlays.get(0).paint(g2, Display.this);
			
			// DIALOG WINDOWS
			if(!dialogs.isEmpty())
				dialogs.get(0).paint(g2, Display.this);
			
			// OTHERS
			refreshButton.x = startEvalButton.x + startEvalButton.width + 10;
		}
		
	}

}
