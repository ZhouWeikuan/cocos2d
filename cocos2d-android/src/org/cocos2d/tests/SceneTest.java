package org.cocos2d.tests;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemFont;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.transitions.CCSlideInTTransition;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class SceneTest extends Activity {
    // private static final String LOG_TAG = SceneTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);

        applicationDidFinishLaunching(this, mGLSurfaceView);
    }

    static class Layer1 extends CCLayer {

        public Layer1() {
            CCMenuItemFont item1 = CCMenuItemFont.item("Test pushScene", this, "onPushScene");
            CCMenuItemFont item2 = CCMenuItemFont.item("Test pushScene w/transition", this, "onPushSceneTran");
            CCMenuItemFont item3 = CCMenuItemFont.item("Quit", this, "onQuit");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.alignItemsVertically();

            addChild(menu);
        }

        public void onPushScene(Object sender) {
            CCScene scene = CCScene.node();
            scene.addChild(new Layer2(), 0);
            CCDirector.sharedDirector().pushScene(scene);
        }

        public void onPushSceneTran(Object sender) {
            CCScene scene = CCScene.node();
            scene.addChild(new Layer2(), 0);
            CCDirector.sharedDirector().pushScene(CCSlideInTTransition.transition(1, scene));
        }

        public void onQuit(Object sender) {
            CCDirector.sharedDirector().popScene();
        }

        public void onVoid() {
        }
    }

    static class Layer2 extends CCLayer {
        public Layer2() {
            CCMenuItemFont item1 = CCMenuItemFont.item("Replace Scene", this, "onReplaceScene");
            CCMenuItemFont item2 = CCMenuItemFont.item("Replace Scene Transition", this, "onReplaceSceneTransition");
            CCMenuItemFont item3 = CCMenuItemFont.item("Go Back", this, "onGoBack");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.alignItemsVertically();

            addChild(menu);
        }

        public void onGoBack(Object sender) {
            CCDirector.sharedDirector().popScene();
        }

        public void onReplaceScene(Object sender) {
            CCScene scene = CCScene.node();
            scene.addChild(new Layer3(), 0);
            CCDirector.sharedDirector().replaceScene(scene);
        }

        public void onReplaceSceneTransition(Object sender) {
            CCScene s = CCScene.node();
            s.addChild(new Layer3(), 0);
            CCDirector.sharedDirector().replaceScene(CCFlipXTransition.transition(2.0f, s, CCTransitionScene.tOrientation.kOrientationLeftOver));
        }
    }

    static class Layer3 extends CCColorLayer {
        public Layer3() {
            super(new ccColor4B(0, 0, 255, 255));

            isTouchEnabled_ = true;

            CCLabel label = CCLabel.makeLabel("Touch to pop scene", "DroidSans", 32);
            addChild(label);
            float width = CCDirector.sharedDirector().winSize().width;
            float height = CCDirector.sharedDirector().winSize().height;
            label.setPosition(CGPoint.make(width / 2, height / 2));
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event) {
            CCDirector.sharedDirector().popScene();
            return CCTouchDispatcher.kEventHandled;
        }
    }


    // CLASS IMPLEMENTATIONS
    public void applicationDidFinishLaunching(Context context, View view) {

        // attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(view);

        // set landscape mode
        CCDirector.sharedDirector().setLandscape(false);

        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(false);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        CCScene scene = CCScene.node();
        scene.addChild(new Layer1(), 0);

        // Make the Scene active
        CCDirector.sharedDirector().runWithScene(scene);

    }

    // getting a call, pause the game
    public void onPause() {
    	super.onPause();
    	
    	CCDirector.sharedDirector().onPause();
    }

    // call got rejected
    public void onResume() {
    	super.onResume();
    	
        CCDirector.sharedDirector().onResume();
    }

    public void onDestroy() {
    	super.onDestroy();
    	
    	CCDirector.sharedDirector().end();
    }
}
