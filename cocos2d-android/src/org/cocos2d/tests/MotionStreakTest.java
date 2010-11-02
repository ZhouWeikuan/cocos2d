package org.cocos2d.tests;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class MotionStreakTest extends Activity {
    // private static final String LOG_TAG = CocosNodeTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("There are known problems with this demo.")
                .setPositiveButton("Ok", null)
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
        scene.addChild(nextAction());

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

    public static final int kTagLabel = 1;
    public static final int kTagSprite1 = 1;
    public static final int kTagSprite2 = 2;


    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Test1.class,
//            Test2.class,
    };

    static CCLayer nextAction() {

        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;

        return restartAction();
    }

    static CCLayer backAction() {
        sceneIdx--;
        int total = transitions.length;
        if (sceneIdx < 0)
            sceneIdx += total;
        return restartAction();
    }

    static CCLayer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (CCLayer) c.newInstance();
        } catch (Exception e) {
            return null;
        }
    }


    static abstract class TestDemo extends CCLayer {
        public TestDemo() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
            addChild(label, kTagLabel);
            label.setPosition(CGPoint.make(s.width / 2, s.height / 2));

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
            addChild(menu, -1);
        }

        public static void restartCallback() {
            CCScene s = CCScene.node();
            s.addChild(restartAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void nextCallback() {
            CCScene s = CCScene.node();
            s.addChild(nextAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void backCallback() {
            CCScene s = CCScene.node();
            s.addChild(backAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public abstract String title();
    }

    static class Test1 extends TestDemo {
        CCNode root;
        CCNode target;
        CCMotionStreak streak;

        public void onEnter() {
            super.onEnter();

            CGSize s = CCDirector.sharedDirector().winSize();

            // the root object just rotates around
            root = CCSprite.sprite("r1.png");
            addChild(root, 1);
            root.setPosition(CGPoint.make(s.width / 2, s.height / 2));

            // the target object is offset from root, and the streak is moved to follow it
            target = CCSprite.sprite("r1.png");

            root.addChild(target);
            target.setPosition(CGPoint.make(100, 0));

            // create the streak object and add it to the scene
            streak = new CCMotionStreak(2, 3, "streak.png", 32, 32, new ccColor4B(0, 255, 0, 255));
            addChild(streak);
            // schedule an update on each frame so we can syncronize the streak with the target
            schedule("onUpdate");

            CCIntervalAction a1 = CCRotateBy.action(2, 360);

            CCAction action1 = CCRepeatForever.action(a1);
            CCIntervalAction motion = CCMoveBy.action(2, CGPoint.make(100, 0));
            root.runAction(CCRepeatForever.action(CCSequence.actions(motion, motion.reverse())));
            root.runAction(action1);
        }

        public void onUpdate(float delta) {
            //  CCPoint p = target.absolutePosition();
            //  float r = root.getRotation();
            CGPoint p = target.convertToWorldSpace(0, 0);
            streak.setPosition(p);

        }

        public String title() {
            return "MotionStreak test 1";
        }
    }
}
