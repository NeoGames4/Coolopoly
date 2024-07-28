package game;

public class Camera {
	
	// X, Y
	public float x, y;
	public float targetX, targetY;
	public float vx, vy;
	
	// ROTATION
	public float angle;
	public float targetAngle;
	
	// ZOOM
	public float zoom;
	public float targetZoom;
	
	// OTHERS
	/**
	 * Timestamp (epoch milliseconds) of the latest camera target update.
	 */
	public long latestUpdate;
	
	public boolean freeMovement;

	public Camera() {
		// X, Y
		x = y = 0;
		targetX = targetY = 0;
		vx = vy = 0;
		
		// ROTATION
		angle = targetAngle = 0;
		
		// ZOOM
		zoom = targetZoom = 0;
		
		// OTHERS
		latestUpdate = System.currentTimeMillis();
		freeMovement = true;
	}
	
	public void update() {
	}

}
