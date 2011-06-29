package org.cocos2d.tests;

import org.cocos2d.R;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

//
// Click and Move demo
// a cocos2d example
// http://www.cocos2d-iphone.org
//
public class SoundEngineTest extends Activity {
    // private static final String LOG_TAG = ClickAndMoveTest.class.getSimpleName();

    // private static final boolean DEBUG = true;
	public static SoundEngineTest app;
    private CCGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = this;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);
        
        // attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        CCDirector.sharedDirector().setLandscape(false);

        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        CCScene scene = CCScene.node();
        scene.addChild(new MainLayer(), 2);

        // Make the Scene active
        CCDirector.sharedDirector().runWithScene(scene);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

        CCDirector.sharedDirector().onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        CCDirector.sharedDirector().onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CCDirector.sharedDirector().end();
    }

    static class MainLayer extends CCLayer {
        public MainLayer() {
        	super();
        	
        	this.setIsTouchEnabled(true);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            
        	SoundEngine.sharedEngine().playEffect(app, R.raw.effect);

            return CCTouchDispatcher.kEventHandled;
        }

        @Override
        public void onEnter() {
        	super.onEnter();
        	
        	SoundEngine.sharedEngine().playSound(app, R.raw.backsound, true);
        }
        
        @Override
        public void onExit() {
        	SoundEngine.sharedEngine().pauseSound();
        	
        	super.onExit();
        }
    }

}
