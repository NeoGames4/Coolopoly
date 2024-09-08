package game;

public class Transition {
	public float from;
	public float to;
	
	public final long start, end;
	
	public int easeDegree = 3;
	
	public Transition(float from, float to, long start, long end) {
		this.from = from;
		this.to = to;
		this.start = start;
		this.end = end;
	}
	
	public Transition(float from, float to, long duration) {
		this(from, to, System.currentTimeMillis(), System.currentTimeMillis()+duration);
	}
	
	public Transition withEaseDegree(int easeDegree) {
		this.easeDegree = easeDegree;
		return this;
	}
	
	public boolean isDone() {
		return System.currentTimeMillis() > end;
	}
	
	// MOVEMENT
	
	public float getValue() {
		if(isDone())
			return to;
		if(System.currentTimeMillis() < start)
			return from;
		
		long duration = end-start;
		double t = ease((System.currentTimeMillis()-start)/(double) duration, easeDegree);
		
		return lerp(from, to, t);
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
		return from + (float) (t * (to-from));
	}
	
}
