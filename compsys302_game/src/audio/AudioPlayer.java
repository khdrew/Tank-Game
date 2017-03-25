package audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.util.HashMap;

public class AudioPlayer {

	private HashMap<String, AudioClip> hm;
	private boolean soundOn = true;
	private boolean musicOn = true;

	public AudioPlayer() {
		hm = new HashMap<String, AudioClip>();
	}

	public void addAudio(String s1, String s2) {
		try {
			AudioClip clip = Applet.newAudioClip(new File(s2).toURI().toURL());
			hm.put(s1, clip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isMusicOn() {
		return musicOn;
	}

	public boolean isSoundOn() {
		return soundOn;
	}

	public void setSound(boolean soundOn) {
		this.soundOn = soundOn;
	}

	public void setMusic(boolean musicOn) {
		this.musicOn = musicOn;
	}

	public AudioClip getClip(String s) {
		return hm.get(s);
	}

	public void play(String s) {
		if (getClip(s) == null)
			return;
		if (soundOn) {
			stop(s);
			getClip(s).play();
		}
	}

	public void loop(String s) {
		if (getClip(s) == null)
			return;
		if (musicOn) {
			getClip(s).loop();
		}
	}

	public void endLoop(String s) {
		if (getClip(s) == null)
			return;
		stop(s);
	}

	public void stop(String s) {
		if (getClip(s) != null)
			getClip(s).stop();
	}
}
