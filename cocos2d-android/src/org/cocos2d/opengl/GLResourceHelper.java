package org.cocos2d.opengl;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

/**
 *  This class performs cleaning on the side of OpenGL thread when OGL resources are destroyed.
 *  CCTexture2D calls release() in finalize method, and texId is queued to be
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
    
    public interface GLResource {
    	void release(GL10 gl);
    }
    
	private ConcurrentLinkedQueue<GLResource> releaseQueue;
	
	public GLResourceHelper() {
		releaseQueue = new ConcurrentLinkedQueue<GLResource>();
	}
	
	/**
	 * Add OGL texture id in releaseQueue
	 * @param texId OGL texture ID
	 */
	public void release(GLResource res) {
		releaseQueue.add(res);
	}

	/**
	 * Method is called from update cycle,
	 * all textures added to queue are destroyed
	 * @param gl
	 */
	public void update(GL10 gl) {
		if(releaseQueue.size() > 0) {
			GLResource res;
			while((res = releaseQueue.poll()) != null) {
				res.release(gl);
			}
		}
	}
}
