package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import launcher.Constants;
import launcher.Launcher;

public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public final UI ui;
	
	public final Queue<Element> uiElements;
	
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
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(!Constants.DEBUG)
			setSize(screenSize);
		else
			setSize((int) (screenSize.getWidth()*3./5), (int) (screenSize.getHeight()*3./4));
		setLocationRelativeTo(null);
		
		ui = new UI();
		setContentPane(ui);
		
		addKeyListener(new Controls());
		setFocusTraversalKeysEnabled(true);
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
			
			if(Controls.W_DOWN) dy++;
			if(Controls.A_DOWN) dx++;
			if(Controls.S_DOWN) dy--;
			if(Controls.D_DOWN) dx--;
			
			if(dx != 0 && dy != 0) {
				float f = (float) Math.sqrt(2);
				dx /= f;
				dy /= f;
			}
			
			camera.x += dx*Constants.CAMERA_MOVEMENT_SPEED/ui.fps;
			camera.y += dy*Constants.CAMERA_MOVEMENT_SPEED/ui.fps;
			
			// CAMERA ZOOM
			float dz = 0;
			
			if(Controls.X_DOWN) dz++;
			if(Controls.Y_DOWN) dz--;
			
			camera.zoom += dz*Constants.CAMERA_ZOOM_SPEED/ui.fps;
			if(camera.zoom < Constants.MIN_CAMERA_ZOOM) camera.zoom = Constants.MIN_CAMERA_ZOOM;
			else if(camera.zoom > Constants.MAX_CAMERA_ZOOM) camera.zoom = Constants.MAX_CAMERA_ZOOM;
			
			if(Constants.DEBUG)
				System.out.println("\nFPS: " + ui.fps + "\nx: " + camera.x + "\ny: " + camera.y + "\nZoom: " + camera.zoom);
		}
		
		camera.update();
	}
	
	public class UI extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private final BufferedImage boardImage;
		
		public float fps = Constants.FPS;
		public long latestFrame = System.currentTimeMillis();

		public UI() {
			super();
			boardImage = Launcher.getImage("grid.png");
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
			float cZoom = (float) Math.exp(camera.zoom);
			
			final int boardW = (int) (Math.min(getWidth(), getHeight())*cZoom);
			float cX = camera.x*cZoom;
			float cY = camera.y*cZoom;
			
			g2.drawImage(boardImage, (int) (cX - boardW/2 + getWidth()/2), (int) (cY - boardW/2 + getHeight()/2), boardW, boardW, null);
		}
		
	}

}
