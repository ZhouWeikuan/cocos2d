package org.cocos2d.sound;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundEngine {
	// effects are sounds that less than 5 seconds, better in 3 seconds
	static HashMap<Integer, Integer> effectsMap = new HashMap<Integer, Integer>();
	
	// sounds are background sounds, usually longer than 5 seconds
	static HashMap<Integer, MediaPlayer> soundsMap = new HashMap<Integer, MediaPlayer>();
	static SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

	public static void preloadEffects(Context app, int resId){
		synchronized(effectsMap) {
			Integer sndId = effectsMap.get(resId);
			if (sndId != null)
				return;
			
			sndId = sp.load(app, resId, 0);
			effectsMap.put(resId, sndId);
		}
	}
		
	public static void playEffect(Context app, int resId) {
		Integer sndId = -1;
		synchronized (effectsMap) {
			sndId = effectsMap.get(resId);
			if (sndId == null) {
				sndId = sp.load(app, resId, 0);
				effectsMap.put(resId, sndId);
			}
		}

		sp.play(sndId, 1.0f, 1.0f, 0, 0, 1.0f);
	}
	
	public static void preloadSounds(Context ctxt, int resId) {
		synchronized(soundsMap) {			
			MediaPlayer mp = soundsMap.get(resId);
			if (mp != null)
				return;
			
			mp = MediaPlayer.create(ctxt, resId);
			mp.prepareAsync();
			soundsMap.put(resId, mp);
		}
	}
	
	public static void playSound(Context ctxt, int resId, boolean loop) {
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(resId);
			if (mp == null) {
				mp = MediaPlayer.create(ctxt, resId);				
				soundsMap.put(resId, mp);
				try {
					mp.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		mp.start();

		if (loop)
			mp.setLooping(true);
	}
	
	public static void pauseSound(int resId) {
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(resId);
			if (mp == null)
				return;
		}
		mp.pause();
	}
	
	public static void stopSound(int resId) {
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(resId);
			if (mp == null)
				return;
		}
		mp.stop();
	}

}
