package game;

public class Camera {
	
	// X, Y
	public float x, y;
	
	// ROTATION
	public float angle;
	
	// ZOOM
	public float zoom;
	
	// TRANSITION
	public Transition transition;
	
	// OTHERS
	public boolean freeMovement;

	public Camera() {
		// X, Y
		x = y = 0;
		
		// ROTATION
		angle = 0;
		
		// ZOOM
		zoom = 0;
		
		// OTHERS
		freeMovement = true;
	}
	
	public void update() {
		if(transition != null) {
			CameraState state = transition.getPosition();
			
			x = state.x;
			y = state.y;
			zoom = state.zoom;
			angle = state.angle;
			
			if(transition.isDone())
				transition = null;
		}
	}
	
	public boolean transitionTo(float x, float y, long duration) {
		return transitionTo(getState().withX(x).withY(y), duration);
	}
	
	public boolean transitionTo(CameraState state, long duration) {
		if((transition == null || transition.isDone()) && !state.equals(getState())) {
			transition = new Transition(getState(), state, duration);
			return true;
		} return false;
	}
	
	public CameraState getState() {
		return new CameraState(x, y, zoom, angle);
	}
	
	public class Transition {
		public final CameraState from;
		public final CameraState to;
		
		public final long start, end;
		
		public int easeDegree = 3;
		
		public Transition(CameraState from, CameraState to, long start, long end) {
			this.from = from;
			this.to = to;
			this.start = start;
			this.end = end;
		}
		
		public Transition(CameraState from, CameraState to, long duration) {
			this(from, to, System.currentTimeMillis(), System.currentTimeMillis()+duration);
		}
		
		public Transition withEaseDegree(int easeDegree) {
			this.easeDegree = easeDegree;
			return this;
		}
		
		public CameraState getPosition() {
			if(isDone())
				return to;
			
			long duration = end-start;
			double t = ease((System.currentTimeMillis()-start)/(double) duration, easeDegree);
			
			float x = lerp(from.x, to.x, t);
			float y = lerp(from.y, to.y, t);
			float zoom = lerp(from.zoom, to.zoom, t);
			float angle = lerp(from.angle, to.angle, t);
			
			return new CameraState(x, y, zoom, angle);
		}
		
		public boolean isDone() {
			return System.currentTimeMillis() > end;
		}
		
		private double ease(double t, double degree) {
			if(t < 0)
				return 0;
			else if(t > 1)
				return 1;
			
			double x = 1.0/2;
			double a = 1/(Math.pow(x, degree) + Math.pow(1-x, degree));
			
			return t < x ? a*Math.pow(t, degree) : -a*Math.pow(1-t, degree) + 1;
		}
		
		private float lerp(float from, float to, double t) {
			return x = from + (float) (t * (to-from));
		}
	}

}
