package org.cocos2d.tests;

import java.lang.reflect.Constructor;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.transitions.CCFadeBLTransition;
import org.cocos2d.transitions.CCFadeDownTransition;
import org.cocos2d.transitions.CCFadeTRTransition;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.transitions.CCFadeUpTransition;
import org.cocos2d.transitions.CCFlipAngularTransition;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.transitions.CCFlipYTransition;
import org.cocos2d.transitions.CCJumpZoomTransition;
import org.cocos2d.transitions.CCMoveInBTransition;
import org.cocos2d.transitions.CCMoveInLTransition;
import org.cocos2d.transitions.CCMoveInRTransition;
import org.cocos2d.transitions.CCMoveInTTransition;
import org.cocos2d.transitions.CCRotoZoomTransition;
import org.cocos2d.transitions.CCShrinkGrowTransition;
import org.cocos2d.transitions.CCSlideInBTransition;
import org.cocos2d.transitions.CCSlideInLTransition;
import org.cocos2d.transitions.CCSlideInRTransition;
import org.cocos2d.transitions.CCSlideInTTransition;
import org.cocos2d.transitions.CCSplitColsTransition;
import org.cocos2d.transitions.CCSplitRowsTransition;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.transitions.CCTurnOffTilesTransition;
import org.cocos2d.transitions.CCZoomFlipAngularTransition;
import org.cocos2d.transitions.CCZoomFlipXTransition;
import org.cocos2d.transitions.CCZoomFlipYTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class TransitionsTest extends Activity {
    // private static final String LOG_TAG = TransitionsTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;

    private static final float TRANSITION_DURATION = 1.2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);
    }

    @Override
    public void onStart() {
        super.onStart();

        // attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        CCDirector.sharedDirector().setLandscape(true);

        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        CCScene scene = CCScene.node();
        scene.addChild(new TestLayer1());
        
        // Make the Scene active
        CCDirector.sharedDirector().runWithScene(scene);
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
        // CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    static class FadeWhiteTransition extends CCFadeTransition {
        public FadeWhiteTransition(float t, CCScene s) {
            super(t, s, new ccColor3B(255, 255, 255));
        }
    }

    static class FlipXLeftOverTransition extends CCFlipXTransition {
        public FlipXLeftOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationLeftOver);
        }
    }

    static class FlipXRightOverTransition extends CCFlipXTransition {
        public FlipXRightOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationRightOver);
        }
    }

    static class FlipYUpOverTransition extends CCFlipYTransition {
        public FlipYUpOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationUpOver);
        }
    }

    static class FlipYDownOverTransition extends CCFlipYTransition {
        public FlipYDownOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationDownOver);
        }
    }

    static class FlipAngularLeftOverTransition extends CCFlipAngularTransition {
        public FlipAngularLeftOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationLeftOver);
        }
    }

    static class FlipAngularRightOverTransition extends CCFlipAngularTransition {
        public FlipAngularRightOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationRightOver);
        }
    }

    static class ZoomFlipXLeftOverTransition extends CCZoomFlipXTransition {
        public ZoomFlipXLeftOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationLeftOver);
        }
    }

    static class ZoomFlipXRightOverTransition extends CCZoomFlipXTransition {
        public ZoomFlipXRightOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationRightOver);
        }
    }

    static class ZoomFlipYUpOverTransition extends CCZoomFlipYTransition {
        public ZoomFlipYUpOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationUpOver);
        }
    }

    static class ZoomFlipYDownOverTransition extends CCZoomFlipYTransition {
        public ZoomFlipYDownOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationDownOver);
        }
    }

    static class ZoomFlipAngularLeftOverTransition extends CCZoomFlipAngularTransition {
        public ZoomFlipAngularLeftOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationLeftOver);
        }
    }

    static class ZoomFlipAngularRightOverTransition extends CCZoomFlipAngularTransition {
        public ZoomFlipAngularRightOverTransition(float t, CCScene s) {
            super(t, s, Orientation.kOrientationRightOver);
        }
    }

    static int sceneIdx = 0;
    static Class<?> transitions[] = {
            CCJumpZoomTransition.class,            
            CCFadeTRTransition.class,
            CCFadeBLTransition.class,
            CCFadeUpTransition.class,
            CCFadeDownTransition.class,
            CCTurnOffTilesTransition.class,
            CCSplitRowsTransition.class,
            CCSplitColsTransition.class,
            CCFadeTransition.class,
            FadeWhiteTransition.class,
            FlipXLeftOverTransition.class,
            FlipXRightOverTransition.class,
            FlipYUpOverTransition.class,
            FlipYDownOverTransition.class,
            FlipAngularLeftOverTransition.class,
            FlipAngularRightOverTransition.class,
            ZoomFlipXLeftOverTransition.class,
            ZoomFlipXRightOverTransition.class,
            ZoomFlipYUpOverTransition.class,
            ZoomFlipYDownOverTransition.class,
            ZoomFlipAngularLeftOverTransition.class,
            ZoomFlipAngularRightOverTransition.class,
            CCShrinkGrowTransition.class,
            CCRotoZoomTransition.class,
            CCMoveInLTransition.class,
            CCMoveInRTransition.class,
            CCMoveInTTransition.class,
            CCMoveInBTransition.class,
            CCSlideInLTransition.class,
            CCSlideInRTransition.class,
            CCSlideInTTransition.class,
            CCSlideInBTransition.class,
    };

    static CCTransitionScene nextTransition(float d, CCScene s) {
        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;

        return restartTransition(d, s);
    }

    static CCTransitionScene backTransition(float d, CCScene s) {
        sceneIdx--;
        int total = transitions.length;
        if (sceneIdx < 0)
            sceneIdx += total;

        return restartTransition(d, s);
    }

    static CCTransitionScene restartTransition(float d, CCScene s) {
        try {
            Class<?> c = transitions[sceneIdx];
            Class<?> partypes[] = new Class[2];
            partypes[0] = Float.TYPE;
            partypes[1] = s.getClass();
            Constructor<?> ctor = c.getConstructor(partypes);
            Object arglist[] = new Object[2];
            arglist[0] = d;
            arglist[1] = s;
            return (CCTransitionScene) ctor.newInstance(arglist);
        } catch (Exception e) {
            return null;
        }
    }

    static class TestLayer1 extends CCLayer {

        public TestLayer1() {

            CGSize s = CCDirector.sharedDirector().winSize();
            float x = s.width;
            float y = s.height;

            CCSprite bg1 = CCSprite.sprite("background1.jpg");
            bg1.setAnchorPoint(CGPoint.make(0, 0));
            addChild(bg1, -1);

            CCLabel label = CCLabel.makeLabel("SCENE 1", "DroidSans", 64);

            label.setPosition(CGPoint.make(x / 2, y / 2));
            addChild(label);

            // menu
            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
            addChild(menu, 1);
        }

        public void nextCallback() {
            CCScene scene = CCScene.node();
            scene.addChild(new TestLayer2());
            CCDirector.sharedDirector().replaceScene(nextTransition(TRANSITION_DURATION, scene));
        }

        public void backCallback() {
            CCScene scene = CCScene.node();
            scene.addChild(new TestLayer2());
            CCDirector.sharedDirector().replaceScene(backTransition(TRANSITION_DURATION, scene));
        }

        public void restartCallback() {
            CCScene scene = CCScene.node();
            scene.addChild(new TestLayer2());
            CCDirector.sharedDirector().replaceScene(restartTransition(TRANSITION_DURATION, scene));
        }
    }

    static class TestLayer2 extends CCLayer {

        public TestLayer2() {

            CGSize s = CCDirector.sharedDirector().winSize();
            float x = s.width;
            float y = s.height;

            CCSprite bg2 = CCSprite.sprite("background2.jpg");
            bg2.setAnchorPoint(CGPoint.make(0, 0));
            addChild(bg2, -1);

            CCLabel label = CCLabel.makeLabel("SCENE 2", "DroidSans", 64);

            label.setPosition(CGPoint.make(x / 2, y / 2));
            addChild(label);

            // menu
            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
            addChild(menu, 1);

        }

        public void nextCallback() {
            CCScene scene = CCScene.node();
            scene.addChild(new TestLayer1());
            CCDirector.sharedDirector().replaceScene(nextTransition(TRANSITION_DURATION, scene));
        }

        public void backCallback() {
            CCScene scene = CCScene.node();
            scene.addChild(new TestLayer1());
            CCDirector.sharedDirector().replaceScene(backTransition(TRANSITION_DURATION, scene));
        }

        public void restartCallback() {
            CCScene scene = CCScene.node();
            scene.addChild(new TestLayer1());
            CCDirector.sharedDirector().replaceScene(restartTransition(TRANSITION_DURATION, scene));

        }
    }


}
