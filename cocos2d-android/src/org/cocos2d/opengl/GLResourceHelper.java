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
	
    private static GLResourceHelper _sharedResourceHelper = new GLResourceHelper();

    /** singleton of the CCTouchDispatcher */
    public static GLResourceHelper sharedHelper() {
//        if (_sharedResourceHelper == null) {
//            synchronized (GLResourceHelper.class) {
//                if (_sharedResourceHelper == null) {
//                    _sharedResourceHelper = new GLResourceHelper();
//                }
//            }
//        }
        return _sharedResourceHelper;
    }
    
    public interface GLResorceTask {
    	void perform(GL10 gl);
    }
    
	/**
	 * These objects are stored in reloadQueue,
	 * they should be removed manually,
	 * DO NOT STORE REFERENCE TO CCTexture2D in TextureLoader,
	 * GLResourceLoader is called in finalize()
	 */
	public interface GLResourceLoader {
		void load();
	}

	private ConcurrentLinkedQueue<GLResorceTask> taskQueue;
    private ConcurrentLinkedQueue<GLResourceLoader> reloadQueue;

	
	public GLResourceHelper() {
		taskQueue = new ConcurrentLinkedQueue<GLResorceTask>();
		reloadQueue = new ConcurrentLinkedQueue<GLResourceLoader>();
	}

    public void addLoader(final GLResourceLoader loader, boolean addTask) {
    	if(addTask) {
	    	taskQueue.add(new GLResorceTask() {
				@Override
				public void perform(GL10 gl) {
					loader.load();
					reloadQueue.add(loader);
				}
			});
    	} else {
    		reloadQueue.add(loader);
    	}
    }
    
    public void removeLoader(GLResourceLoader loader) {
    	reloadQueue.remove(loader);
	}

    /**
     * This should be called only when recreating GL context
     */
	public void reloadResources() {
		taskQueue.add(new GLResorceTask() {
			@Override
			public void perform(GL10 gl) {
				if(reloadQueue.size() > 0) {
					for(GLResourceLoader res : reloadQueue) {
						res.load();
					}
				}
			}
		});
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
