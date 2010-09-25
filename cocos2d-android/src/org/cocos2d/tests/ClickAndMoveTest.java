package org.cocos2d.tests;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.CCActionManager;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.*;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.CGPoint;
import org.cocos2d.events.CCTouchDispatcher;

public class ClickAndMoveTest extends Activity {
    // private static final String LOG_TAG = ClickAndMoveTest.class.getSimpleName();

    // private static final boolean DEBUG = true;

    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new AlertDialog.Builder(this)
                .setTitle("Welcome")
                .setMessage("Click on the screen to move and rotate Grossini")
                .setPositiveButton("Start", null)
                .show();

        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);
    }

    @Override
    public void onStart() {
        super.onStart();

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
    public void onPause() {
        super.onPause();

        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        CCDirector.sharedDirector().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CCActionManager.sharedManager().removeAllActions();
        CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    static class MainLayer extends CCLayer {
        static final int kTagSprite = 1;

        public MainLayer() {

            isTouchEnabled_ = true;

            CCSprite sprite = CCSprite.sprite("grossini.png");

            CCLayer layer = CCColorLayer.node(new ccColor4B(255, 255, 0, 255));
            addChild(layer, -1);

            addChild(sprite, 0, kTagSprite);
            sprite.setPosition(CGPoint.make(20, 150));

            sprite.runAction(CCJumpTo.action(4, CGPoint.make(300, 48), 100, 4));

            layer.runAction(CCRepeatForever.action(CCSequence.actions(CCFadeIn.action(1), CCFadeOut.action(1))));
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint convertedLocation = CCDirector.sharedDirector()
            	.convertToGL(CGPoint.make(event.getX(), event.getY()));

            CCNode s = getChild(kTagSprite);
            s.stopAllActions();
            s.runAction(CCMoveTo.action(1.0f, convertedLocation));
           
            CGPoint pnt = s.getPosition();
            float o = convertedLocation.x - pnt.x;
            float a = convertedLocation.y - pnt.y;
            float at = ccMacros.CC_RADIANS_TO_DEGREES((float) Math.atan(o / a));

            if (a < 0) {
                if (o < 0)
                    at = 180 + Math.abs(at);
                else
                    at = 180 - Math.abs(at);
            }

            s.runAction(CCRotateTo.action(1, at));

            return CCTouchDispatcher.kEventHandled;
        }

    }

}

