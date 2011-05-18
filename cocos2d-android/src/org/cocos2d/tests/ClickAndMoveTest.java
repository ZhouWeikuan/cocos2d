package org.cocos2d.tests;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4B;

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
public class ClickAndMoveTest extends Activity {
    // private static final String LOG_TAG = ClickAndMoveTest.class.getSimpleName();

    // private static final boolean DEBUG = true;
	public static ClickAndMoveTest app;
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
        static final int kTagSprite = 1;
        CCProgressTimer  progressTimer;

        public MainLayer() {

        	this.setIsTouchEnabled(true);

            CCSprite sprite = CCSprite.sprite("grossini.png");

            CCLayer layer = CCColorLayer.node(new ccColor4B(255, 255, 0, 255));
            addChild(layer, -1);

            addChild(sprite, 1, kTagSprite);
            sprite.setPosition(CGPoint.make(20, 150));

            sprite.runAction(CCJumpTo.action(4, CGPoint.make(300, 48), 100, 4));

            CCLabel lbl1 = CCLabel.makeLabel("Click on the screen", "DroidSans", 24);
            CCLabel lbl2 = CCLabel.makeLabel("to move and rotate Grossini", "DroidSans", 16);

            addChild(lbl1, 0);
            addChild(lbl2, 1);
            lbl1.setPosition(CGPoint.ccp(160, 240));
            lbl2.setPosition(CGPoint.ccp(160, 200));
            
            progressTimer = CCProgressTimer.progress("iso.png");
            this.addChild(progressTimer, 10);
            progressTimer.setPosition(160, 100);
            progressTimer.setType(CCProgressTimer.kCCProgressTimerTypeVerticalBarTB);
            progressTimer.setPercentage(50.0f);
            
            layer.runAction(CCRepeatForever.action(CCSequence.actions(CCFadeIn.action(1), CCFadeOut.action(1))));
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint convertedLocation = CCDirector.sharedDirector()
            	.convertToGL(CGPoint.make(event.getX(), event.getY()));

            CCNode s = getChildByTag(kTagSprite);
            s.stopAllActions();
            s.runAction(CCMoveTo.action(1.0f, convertedLocation));
           
            CGPoint pnt = s.getPosition();

            float at = CGPoint.ccpCalcRotate(pnt, convertedLocation);

            s.runAction(CCRotateTo.action(1, at));
            
            progressTimer.setPercentage(10.0f + progressTimer.getPercentage());

            return CCTouchDispatcher.kEventHandled;
        }

    }

}
