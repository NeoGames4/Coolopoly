package game;

import java.util.ArrayList;

import game.Camera.CameraTransition;

public class CameraTransitionsBuilder {
	
	public final ArrayList<Transition>	xTransitions = new ArrayList<>(),
										yTransitions = new ArrayList<>(),
										zTransitions = new ArrayList<>(),
										aTransitions = new ArrayList<>();
	
	public final Camera camera;

	public CameraTransitionsBuilder(Camera camera) {
		this.camera = camera;
	}
	
	public CameraTransitionsBuilder withXTransitions(Transition... transitions) {
		for(Transition t : transitions)
			xTransitions.add(t);
		return this;
	}
	
	public CameraTransitionsBuilder withYTransitions(Transition... transitions) {
		for(Transition t : transitions)
			yTransitions.add(t);
		return this;
	}
	
	public CameraTransitionsBuilder withZTransitions(Transition... transitions) {
		for(Transition t : transitions)
			zTransitions.add(t);
		return this;
	}
	
	public CameraTransitionsBuilder withATransitions(Transition... transitions) {
		for(Transition t : transitions)
			aTransitions.add(t);
		return this;
	}
	
	public CameraTransition build() {
		return camera.new CameraTransition(xTransitions, yTransitions, zTransitions, aTransitions);
	}

}
