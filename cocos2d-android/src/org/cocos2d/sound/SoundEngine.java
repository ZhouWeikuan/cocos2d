package org.cocos2d.sound;

import java.io.IOException;

import org.cocos2d.utils.collections.IntMap;
import org.cocos2d.utils.collections.IntMap.Entry;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

// TODO: support volume customizations for both effects and background ...
public class SoundEngine {
	// effects are sounds that less than 5 seconds, better in 3 seconds
	IntMap<Integer> effectsMap = new IntMap<Integer>();
	IntMap<Integer> streamsMap = new IntMap<Integer>();
	
	// sounds are background sounds, usually longer than 5 seconds
	IntMap<MediaPlayer> soundsMap = new IntMap<MediaPlayer>();
	SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	int lastSndId = -1;
	Float prevEffectsVolume = null;
	Float prevSoundsVolume = null;
	Float effectsVolume = null;
	Float soundsVolume = null;
	boolean mute = false;
	
    static SoundEngine _sharedEngine = null;

    public static SoundEngine sharedEngine() {
        synchronized(SoundEngine.class) {
            if (_sharedEngine == null) {
                _sharedEngine = new SoundEngine();
            }
        }
        return _sharedEngine;
    }

    public static void purgeSharedEngine() {
        synchronized(SoundEngine.class) {
            _sharedEngine = null;
        }
    }
    
    public void setEffectsVolume(Float volume) {
    	if (mute)
    		return;
    	
    	effectsVolume = volume;
    }
    
    public Float getEffectsVolume() {
    	return effectsVolume;
    }
    
    public void setSoundVolume(Float volume) {
    	if (mute)
    		return;
    	
    	soundsVolume = volume;
    	for (Entry<MediaPlayer> each : soundsMap)
    	{
    		each.getValue().setVolume(soundsVolume, soundsVolume);
    	}
    }
    
    public Float getSoundsVolume() {
    	return soundsVolume;
    }
    
    public void mute() {
    	if (mute)
    		return;
    	
    	prevEffectsVolume = effectsVolume;
    	prevSoundsVolume = soundsVolume;
    	effectsVolume = 0f;
    	setSoundVolume(0f);
    	mute = true;
    }
    
    public void unmute() {
    	if (!mute)
    		return;
    	
    	effectsVolume = prevEffectsVolume;
    	mute = false;
    	setSoundVolume(prevSoundsVolume);
    }
    
    public boolean isMute() {
    	return mute;
    }

	public void preloadEffect(Context app, int resId){
		synchronized(effectsMap) {
			Integer sndId = effectsMap.get(resId);
			if (sndId != null)
				return;
			
			sndId = sp.load(app, resId, 0);
			effectsMap.put(resId, sndId);
		}
	}
		
	public void playEffect(Context app, int resId) {
		Integer sndId = -1;
		synchronized (effectsMap) {
			sndId = effectsMap.get(resId);
			if (sndId == null) {
				sndId = sp.load(app, resId, 0);
				effectsMap.put(resId, sndId);
			}
		}

		int streamId = sp.play(sndId, 1.0f, 1.0f, 0, 0, 1.0f);
		if (effectsVolume != null) {
			sp.setVolume(streamId, effectsVolume, effectsVolume);
		}
		streamsMap.put(resId, streamId);
	}
	
	public void stopEffect(Context app, int resId) {
		Integer sid = streamsMap.get(resId);
		if (sid != null) {
			sp.stop(sid);
		}
	}
	
	public void preloadSound(Context ctxt, int resId) {
		synchronized(soundsMap) {			
			MediaPlayer mp = soundsMap.get(resId);
			if (mp != null)
				return;
			
			mp = MediaPlayer.create(ctxt, resId);
//			mp.prepareAsync();
			soundsMap.put(resId, mp);
		}
	}
	
	public void playSound(Context ctxt, int resId, boolean loop) {
		if (lastSndId != -1) {
			pauseSound();
		}
		
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(resId);
			if (mp == null) {
				mp = MediaPlayer.create(ctxt, resId);
				
				// failed to create
				if(mp == null)
					return;
				
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
		lastSndId = resId;
		mp.start();
		if (soundsVolume != null)
			mp.setVolume(soundsVolume, soundsVolume);

		if (loop)
			mp.setLooping(true);
	}
	
	public void pauseSound() {
		if (lastSndId == -1)
			return;
		
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(lastSndId);
			if (mp == null)
				return;
		}
		mp.pause();
	}
	
	public void resumeSound() {
		if (lastSndId == -1)
			return;
		
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(lastSndId);
			if (mp == null)
				return;
		}
		mp.start();
	}
	
	public void realesSound(int resId)
	{
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(resId);
			if (mp != null) {
				mp.release();
				soundsMap.remove(resId);
			}
		}
	}
	
	public void realesAllSounds() {
		
		for(Entry<MediaPlayer> mp : soundsMap) {
			mp.getValue().release();
		}
		
		soundsMap.clear();
	}
	
	public void realesAllEffects() {
		sp.release();
	}

}
