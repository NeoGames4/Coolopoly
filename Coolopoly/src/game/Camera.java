package game;

import java.util.ArrayList;
import java.util.List;

import game.uielements.Vector2D;
import misc.Tools;

public class Camera {
	
	// X, Y
	public float x, y;
	
	// ROTATION
	public float angle;
	
	// ZOOM
	public float zoom;
	
	// TRANSITION
	public CameraTransition transition;
	
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
			CameraState state = transition.getCameraState();
			
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
			transition = new CameraTransition(getState(), state, duration);
			return true;
		} return false;
	}
	
	public boolean transitionWith(CameraTransition t) {
		if((transition == null || transition.isDone()) && !t.equals(transition)) {
			transition = t;
			return true;
		} return false;
	}
	
	public CameraState getState() {
		return new CameraState(x, y, zoom, angle);
	}
	
	public class CameraTransition {

		// Do not add elements after initFromTo() has been called
		private final ArrayList<Transition>	xTransitions = new ArrayList<>(),
											yTransitions = new ArrayList<>(),
											zTransitions = new ArrayList<>(),
											aTransitions = new ArrayList<>();
		
		private CameraState from = null;
		private CameraState to	 = null;
		
		public CameraTransition(ArrayList<Transition> xTransitions, ArrayList<Transition> yTransitions, ArrayList<Transition> zTransitions, ArrayList<Transition> aTransitions) {
			this.xTransitions.addAll(xTransitions);
			this.yTransitions.addAll(yTransitions);
			this.zTransitions.addAll(zTransitions);
			this.aTransitions.addAll(aTransitions);
			optimizeAngles();
			init();
		}
		
		public CameraTransition(CameraState from, CameraState to, long start, long end) {
			xTransitions.add(new Transition(from.x, to.x, start, end));
			yTransitions.add(new Transition(from.y, to.y, start, end));
			zTransitions.add(new Transition(from.zoom, to.zoom, start, end));
			aTransitions.add(new Transition(from.angle, to.angle, start, end));
			optimizeAngles();
			init();
		}
		
		
		public CameraTransition(CameraState from, CameraState to, long duration) {
			this(from, to, System.currentTimeMillis(), System.currentTimeMillis()+duration);
		}
		
		// PREPARE DATA AND INIT FROM AND TO
		
		private void init() {
			if(from != null && to != null)
				return;
			
			Vector2D	x = getExtremeTransitions(getState().x,		getState().x,		xTransitions),
						y = getExtremeTransitions(getState().y,		getState().y,		yTransitions),
						z = getExtremeTransitions(getState().zoom,	getState().zoom,	zTransitions),
						a = getExtremeTransitions(getState().angle,	getState().angle,	aTransitions);
			
			from = new CameraState(x.x, y.x, z.x, a.x);
			to   = new CameraState(x.y, y.y, z.y, a.y);
		}
		
		private Vector2D getExtremeTransitions(float earliestX, float latestX, List<Transition> transitions) {
			long earliestT	= System.currentTimeMillis();
			long latestT	= System.currentTimeMillis();

			for(Transition t : transitions) {
				if(t.start < earliestT) {
					earliestT = t.start;
					earliestX = t.from;
				}
				if(t.end > latestT) {
					latestT = t.end;
					latestX = t.to;
				}
			}
			
			return new Vector2D(earliestX, latestX);
		}
		
		// OTHER
		
		private ArrayList<Transition> getAllTransitions() {
			ArrayList<Transition> transitions = new ArrayList<>();
			transitions.addAll(xTransitions);
			transitions.addAll(yTransitions);
			transitions.addAll(zTransitions);
			transitions.addAll(aTransitions);
			return transitions;
		}
		
		public boolean isDone() {
			for(Transition t : getAllTransitions()) {
				if(System.currentTimeMillis() < t.end)
					return false;
			} return true;
		}
		
		// MOVEMENT
		
		public CameraState getCameraState() {
			xTransitions.removeIf(t -> t.isDone());
			yTransitions.removeIf(t -> t.isDone());
			zTransitions.removeIf(t -> t.isDone());
			aTransitions.removeIf(t -> t.isDone());
			
			float x = to.x;
			float y = to.y;
			float z = to.zoom;
			float a = to.angle;
			
			if(!xTransitions.isEmpty())
				x = xTransitions.get(0).getValue();
			
			if(!yTransitions.isEmpty())
				y = yTransitions.get(0).getValue();
			
			if(!zTransitions.isEmpty())
				z = zTransitions.get(0).getValue();
			
			if(!aTransitions.isEmpty())
				a = aTransitions.get(0).getValue();
			
			return new CameraState(x, y, z, a);
		}
		
		// ANGLE OPTIMIZATION
		
		public void optimizeAngles() {
			for(Transition t : aTransitions) {
				t.from	= Tools.absMod(t.from,	360);
				t.to	= Tools.absMod(t.to,	360);
				
				float daNorm = Math.abs(t.from-t.to);
				
				float minFrom	= t.from > 180 ? -t.from+180 : t.from;
				float minTo		= t.to > 180   ? -t.to+180   : t.to;
				
				float daMinNorm = Math.abs(minFrom-t.to);
				float daNormMin = Math.abs(t.from-minTo);
				float daMinMin  = Math.abs(minFrom-minTo);
				
				float min = (float) Math.min(
					daNorm, Math.min(daMinNorm,
							Math.min(daNormMin, daMinMin)
					)
				);
				
				if(daMinNorm == min)
					t.from = minFrom;
				if(daNormMin == min)
					t.to = minTo;
				if(min == daMinMin) {
					t.from = minFrom;
					t.to = minTo;
				}
			}
		}

	}

}
