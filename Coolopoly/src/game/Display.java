package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import game.uielements.Board;
import game.uielements.Button;
import game.uielements.DialogWindow;
import game.uielements.DialogWindowOption;
import game.uielements.InfoPanel;
import game.uielements.OptionPane;
import game.uielements.UIElement;
import misc.Constants;
import misc.design.Design;
import networking.ServerConnection;

public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public final ServerConnection server;
	
	public final UI ui;
	
	public ArrayList<UIElement> uiElements;
	public ArrayList<DialogWindow> dialogs;
	
	public final Camera camera;
	
	private Transition zoomOutTransition = new Transition(0, 0, 10);	// TODO
	
	private int clickMeButtonClicks = 0;

	public Display(ServerConnection server) {
		super();
		
		this.server = server;
		
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
				System.exit(0);
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
		
		uiElements.add(new Button(10, 100, Design.INFO_PANEL_HEIGHT - 6, 3 * Design.INFO_PANEL_HEIGHT - 6).withIcon(Design.getImage("arrow-left-icon.png")));
		uiElements.add(new Button(10, 100, Design.INFO_PANEL_HEIGHT - 6, Design.INFO_PANEL_HEIGHT - 6, Button.RELATIVE_TO_TOP_RIGHT));
		
		uiElements.add(new Button("Click me!", 0, 10, Button.RELATIVE_TO_TOP_CENTER) {
			@Override
			public void onPressed() {
				clickMeButtonClicks++;
				label = "Click me! (" + clickMeButtonClicks + ")";
			}
		});
		
		uiElements.add(new Button(20 + Design.INFO_PANEL_HEIGHT, 100, Design.INFO_PANEL_HEIGHT - 6, Design.INFO_PANEL_HEIGHT - 6) {
			@Override
			public void onPressed() {
				DialogWindowOption[] options = {
					DialogWindowOption.getDiscardOption("Cancel", dialogs),
					DialogWindowOption.getDiscardOption("OK", dialogs)
				};
				dialogs.add(new DialogWindow("Hello", "Information.\nMultiple lines with multiple words.\nYet another line.", options));
			}
		});
		
		dialogs = new ArrayList<>();
		
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
	
	private long debugCooldown = 0; // TODO
	
	public void updateGame() {
		if(server.getLatestGameState() == null) {
			return;
		}
		
		Player player = server.getLatestGameState().thisPlayer();
		
		if(player == null) {
			OptionPane.sendExceptionPanel("Player not found", "Could not find a player with your username.");
			System.exit(0);
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
			
			// if(Constants.DEBUG)
				// System.out.println("\nFPS: " + ui.fps + "\nx: " + camera.x + "\ny: " + camera.y + "\nRotation: " + camera.angle + "\nZoom: " + camera.zoom + "\nBoard size: " + ui.board.width + ", " + ui.board.height + "\nPlayer position: " + player.getPosition());
		}
		
		if(Controls.TAB_DOWN) {
			if(camera.transitionTo(camera.getState().withX(0).withY(0).withZoom(0).withAngle(0), 350))
				zoomOutTransition = new Transition(zoomOutTransition.getValue(), 1, 350);
		} else if(!camera.freeMovement) {
			camera.transitionTo(ui.board.getCameraStateFocus(player.getPosition()), 500);
			
			if(zoomOutTransition.to != 0)
				zoomOutTransition = new Transition(zoomOutTransition.getValue(), 0, 350);
		}

		if(Controls.F_DOWN && Constants.DEBUG && System.currentTimeMillis()-debugCooldown > 200) {
			debugCooldown = System.currentTimeMillis();
			
			player.changePosition(1);
			
			try {
				player.changeMoney(100);
			} catch (GameIllegalMoveException e) { }
			
			/*CameraState focusPosition = ui.board.getCameraStateFocus(player.getPosition());
				
			long transitionTime = Math.max((long) (Board.distanceBetween(camera.getState().getPosition(), focusPosition.getPosition())*2), 500l);
			
			camera.transitionTo(focusPosition, transitionTime);*/
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
			
			// DIALOG WINDOWS
			if(!dialogs.isEmpty())
				dialogs.get(0).paint(g2, Display.this);
		}
		
	}

}
