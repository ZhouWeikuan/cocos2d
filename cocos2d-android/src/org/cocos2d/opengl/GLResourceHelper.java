package org.cocos2d.opengl;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCDirector;

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
        return _sharedResourceHelper;
    }
    
    public interface GLResorceTask {
    	void perform(GL10 gl);
    }
    
    /**
     * 
     * GL resources should implement this
     *
     */
    public interface Resource {
    }
    
	/**
	 * These objects are stored in reloadQueue,
	 * they should be removed manually,
	 * DO NOT STORE REFERENCE TO CCTexture2D in TextureLoader,
	 * GLResourceLoader is called in finalize()
	 */
	public interface GLResourceLoader {
		void load(Resource res);
	}

	private ConcurrentLinkedQueue<GLResorceTask> taskQueue;
	private Map<Resource, GLResourceLoader> reloadMap;

	
	public GLResourceHelper() {
		taskQueue = new ConcurrentLinkedQueue<GLResorceTask>();
		reloadMap = Collections.synchronizedMap(new WeakHashMap<GLResourceHelper.Resource, GLResourceHelper.GLResourceLoader>());
	}

    public void addLoader(final Resource res, final GLResourceLoader loader, boolean addTask) {
    	if(addTask) {
    		GLResorceTask task = new GLResorceTask() {
				@Override
				public void perform(GL10 gl) {
					loader.load(res);
					reloadMap.put(res, loader);
				}
			};
    		perform(task);
    	} else {
    		reloadMap.put(res, loader);
    	}
    }
    
    private boolean reloadTaskIsInQueue;
    /**
     * This should be called only when recreating GL context
     */
	public void reloadResources() {
		if(reloadTaskIsInQueue)
			return;
			
		reloadTaskIsInQueue = true;
		taskQueue.add(new GLResorceTask() {
			@Override
			public void perform(GL10 gl) {
				for(Entry<Resource, GLResourceLoader> entry : reloadMap.entrySet()) {
					Resource res = entry.getKey();
					if(res != null)
						entry.getValue().load(res);
				}
				reloadTaskIsInQueue = false;
			}
		});
	}

	/**
	 * Add OGL task in queue. If perform is called from another task
	 * then task is performed immediately.
	 * @param res GL task
	 */
	public void perform(GLResorceTask res) {
		if(inUpdate) {
			res.perform(CCDirector.gl);
		} else {
			taskQueue.add(res);
		}
	}

	private volatile boolean inUpdate = false;

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

	public void setInUpdate(boolean inUpd) {
		inUpdate = inUpd;
	}
}
