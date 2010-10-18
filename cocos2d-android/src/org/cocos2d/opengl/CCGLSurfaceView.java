package org.cocos2d.opengl;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.view.MotionEvent;

public class CCGLSurfaceView extends GLSurfaceView {
	private static final int VIEWID = 0x1235;
    // private static final String LOG_TAG = CCGLSurfaceView.class.getSimpleName();
    private CCTouchDispatcher mDispatcher;

    public CGSize frame;
    protected AsyncEventer eventHandler;

    public CCGLSurfaceView(Context context) {
        super(context);

        CCDirector.theApp = (Activity) context;

        eventHandler = new AsyncEventer();
        mDispatcher = CCTouchDispatcher.sharedDispatcher();

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setId(VIEWID);        
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    	frame = CGSize.make(right - left, bottom - top);
    }        

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	eventHandler.doInBackground(event);        
        return true;
    }
    
    class AsyncEventer extends AsyncTask<MotionEvent, Void, Void> {

    	@Override
    	protected Void doInBackground(MotionEvent... events) {
    		// TODO Auto-generated method stub
    		MotionEvent event = events[0];
    		switch (event.getAction()) {
    		case MotionEvent.ACTION_CANCEL:
    			mDispatcher.touchesCancelled(event);
    			break;
    		case MotionEvent.ACTION_DOWN:
    			mDispatcher.touchesBegan(event);
    			break;
    		case MotionEvent.ACTION_MOVE:
    			mDispatcher.touchesMoved(event);
    			break;
    		case MotionEvent.ACTION_UP:
    			mDispatcher.touchesEnded(event);
    			break;
    		}
			return null;

    		/*
    		synchronized (CCDirector.sharedDirector()) {
    			try {
    				CCDirector.sharedDirector().wait(20L);
    			} catch (InterruptedException e) {
    				// Do nothing
    			}
    		} */
    	}    	
    }
}

