package sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import misc.Console;

public class SoundManager {
	
	public static final String	BUTTON_PRESS_SOUND		= "click_in.wav",
								BUTTON_RELEASE_SOUND	= "click_out.wav",
								MONEY_CHANGED_SOUND		= "ca-ching.wav";
	
	public static final int		SOUND_LOUD	= 0,
								SOUND_ON	= 1,
								SOUND_OFF	= 2;
	
	private static int soundMode = SOUND_LOUD;
	private static float volume = 1f;
	
	private static final HashMap<Integer, Float> MODE_VOLUME_MAP;
	static {
		MODE_VOLUME_MAP = new HashMap<>();
		MODE_VOLUME_MAP.put(SOUND_LOUD,	1.0f);
		MODE_VOLUME_MAP.put(SOUND_ON,	0.33f);
		MODE_VOLUME_MAP.put(SOUND_OFF,	0.0f);
	}

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
		setClipVolume(clip, volume);
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
	
	public static void setClipVolume(Clip clip, float volume) {
		if(volume < 0)
			volume = 0;
		else if(volume > 1)
			volume = 1;
		
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(volume));
	}
	
	public static void setTotalVolume(float volume) {
		SoundManager.volume = volume;
		CURRENTLY_PLAYING.forEach(c -> setClipVolume(c.clip, volume));
	}
	
	public static int getSoundMode() {
		return soundMode;
	}
	
	public static int toggleSoundMode() {
		soundMode++;
		if(soundMode != SOUND_LOUD && soundMode != SOUND_ON && soundMode != SOUND_OFF)
			soundMode = SOUND_LOUD;
		
		float volume = MODE_VOLUME_MAP.containsKey(soundMode) ? MODE_VOLUME_MAP.get(soundMode) : 1;
		setTotalVolume(volume);
	    
		return soundMode;
	}

}
