package org.cocos2d.opengl;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

/**
 *  This class performs tasks on the side of OpenGL thread.
 *  CCTexture2D calls perform() in finalize method, and texId is queued to be
 *  deleted later.
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
    
    public interface GLResorceTask {
	void perform(GL10 gl);
    }
    
	private ConcurrentLinkedQueue<GLResorceTask> taskQueue;
	
	public GLResourceHelper() {
		taskQueue = new ConcurrentLinkedQueue<GLResorceTask>();
	}
	
	/**
	 * Add OGL task in queue
	 * @param res GL task
	 */
	public void perform(GLResorceTask res) {
		taskQueue.add(res);
	}

	/**
	 * Method is called from update cycle,
	 * perform all tasks in GL thread
	 * @param gl
	 */
	public void update(GL10 gl) {
		if(taskQueue.size() > 0) {
			GLResorceTask res;
			while((res = taskQueue.poll()) != null) {
				res.perform(gl);
			}
		}
	}
}
