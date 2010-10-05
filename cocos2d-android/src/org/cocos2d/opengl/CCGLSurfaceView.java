package org.cocos2d.opengl;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.events.CCTouchDispatcher;

public class CCGLSurfaceView extends GLSurfaceView {

    // private static final String LOG_TAG = CCGLSurfaceView.class.getSimpleName();
    private CCDirector mRenderer;
    private CCTouchDispatcher mDispatcher;

    public Display frame;

    public CCGLSurfaceView(Context context) {
        super(context);

        mRenderer = CCDirector.sharedDirector();
        CCDirector.theApp = (Activity) context;

        mDispatcher = CCTouchDispatcher.sharedDispatcher();

        setRenderer(mRenderer);

        WindowManager w = ((Activity) context).getWindowManager();
        frame = w.getDefaultDisplay();

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public boolean onTouchEvent(MotionEvent event) {

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

        synchronized (CCDirector.sharedDirector()) {
            try {
                CCDirector.sharedDirector().wait(20L);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        
        return true;
    }

}

