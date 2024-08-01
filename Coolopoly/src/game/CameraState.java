package game;

import game.uielements.Vector2D;

public class CameraState {
	
	public final float x, y;
	
	public final float zoom;
	
	public float angle;

	public CameraState(float x, float y, float zoom, float angle) {
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.angle = angle;
	}
	
	public CameraState withX(float x)			{ return new CameraState(x, y, zoom, angle); }
	
	public CameraState withY(float y)			{ return new CameraState(x, y, zoom, angle); }
	
	public CameraState withZoom(float zoom)		{ return new CameraState(x, y, zoom, angle); }
	
	public CameraState withAngle(float angle)	{ return new CameraState(x, y, zoom, angle); }
	
	public Vector2D getPosition() {
		return new Vector2D(x, y);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof CameraState) {
			CameraState objState = (CameraState) object;
			return (x == objState.x && y == objState.y && zoom == objState.zoom && angle == objState.angle);
		} else return super.equals(object);
	}

}
