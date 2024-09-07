package sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import misc.Console;

public class SoundManager {
	
	public static final String	BUTTON_PRESS_SOUND		= "click_in.wav",
								BUTTON_RELEASE_SOUND	= "click_out.wav",
								MONEY_CHANGED_SOUND		= "ca-ching.wav";

	private static final ArrayList<Sound> CURRENTLY_PLAYING = new ArrayList<>();
	
	public static void play(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		play(filePath, 0);
	}
	
	public static void safePlay(String filePath, int loop) {
		try {
			play(filePath, loop);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// e.printStackTrace();
			Console.err("Cannot play " + filePath + " for " + loop + " times.");
		}
	}
	
	public static Clip play(String filePath, int loop) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream input = AudioSystem.getAudioInputStream(new BufferedInputStream(SoundManager.class.getResourceAsStream(filePath)));
		Clip clip = AudioSystem.getClip();
		clip.open(input);
		clip.loop(loop);
		clip.start();
		CURRENTLY_PLAYING.add(new Sound(filePath, clip));
		return clip;
	}
	
	public static boolean stop(Clip clip) {
		CURRENTLY_PLAYING.forEach(c -> {
			if(c.clip.equals(clip))
				c.clip.close();
		});
		return CURRENTLY_PLAYING.removeIf(c -> c.clip.equals(clip));
	}
	
	public static void stopAll() {
		CURRENTLY_PLAYING.forEach(c -> c.clip.close());
		CURRENTLY_PLAYING.clear();
	}

}
