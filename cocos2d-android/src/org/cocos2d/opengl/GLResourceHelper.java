package org.cocos2d.opengl;

import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

/**
 *  This class perfoms cleaning on the side of opengl thread when textures desrtoys.
 *  CCTexture2D calls releaseTexture() in finalize method, and texId is queued to be
 *  deleted later.
 *  
 *  Only texture resources are supported while.
 *  
 * @author genius
 */

public class GLResourceHelper {
	
    private static GLResourceHelper _sharedResourceHelper;

    /** singleton of the CCTouchDispatcher */
    public static GLResourceHelper sharedHelper() {
        if (_sharedResourceHelper == null) {
            synchronized (GLResourceHelper.class) {
                if (_sharedResourceHelper == null) {
                    _sharedResourceHelper = new GLResourceHelper();
                }
            }
        }
        return _sharedResourceHelper;
    }
    
	private ConcurrentLinkedQueue<Integer> releaseQueue;
	
	public GLResourceHelper() {
		releaseQueue = new ConcurrentLinkedQueue<Integer>();
	}
	
	public void releaseTexture(int texId) {
		releaseQueue.add(texId);
		Log.v("DEBUG", "Texture resource addded for destroy: " + texId);
	}
	
	private IntBuffer intBuffer = IntBuffer.allocate(1);
	
	public void update(GL10 gl) {
		if(releaseQueue.size() > 0) {
			Integer texId;
			while((texId = releaseQueue.poll()) != null) {
				intBuffer.put(0, texId);
				gl.glDeleteTextures(1, intBuffer);
			}
		}
	}
}
