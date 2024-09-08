package sound;

import javax.sound.sampled.Clip;

public class Sound {
	
	public final String path;
	public final Clip clip;

	public Sound(String path, Clip clip) {
		this.path = path;
		this.clip = clip;
	}

}
