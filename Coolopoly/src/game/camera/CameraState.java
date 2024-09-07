package game.camera;

import misc.Tools;
import misc.Vector2D;

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
			/*Console.log("CHECKING EQUALITY");
			Console.log("x " + x + ", " + objState.x + ", " + (x == objState.x));
			Console.log("y " + y + ", " + objState.y + ", " + (y == objState.y));
			Console.log("z " + zoom + ", " + objState.zoom + ", " + (zoom == objState.zoom));
			Console.log("a " + Tools.absMod(angle, 360) + ", " + Tools.absMod(objState.angle, 360) + ", " + (Tools.absMod(angle, 360) == Tools.absMod(objState.angle, 360)));*/
			return (x == objState.x && y == objState.y && zoom == objState.zoom && Tools.absMod(angle, 360) == Tools.absMod(objState.angle, 360));
		} else return super.equals(object);
	}

}
