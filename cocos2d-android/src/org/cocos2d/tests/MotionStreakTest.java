package org.cocos2d.tests;

import org.cocos2d.actions.UpdateCallback;
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
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCMotionStreak;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MotionStreakTest extends Activity {
    // private static final String LOG_TAG = CocosNodeTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


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
    public static final int kTagSprite1 = 2;
    public static final int kTagSprite2 = 3;

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Test1.class,
            Test2.class,
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


    static class MotionStreakTestLayer extends CCLayer {
        public MotionStreakTestLayer() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 18);
            addChild(label, kTagLabel);
            label.setPosition(CGPoint.make(s.width / 2, s.height / 2 - 50));

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.setPosition(0, 0);
            item1.setPosition(s.width / 2 - 100, 30);
            item2.setPosition(s.width / 2, 30);
            item3.setPosition(s.width / 2 + 100, 30);
            addChild(menu, 1);
        }

        public void restartCallback(Object sender) {
            CCScene s = CCScene.node();
            s.addChild(restartAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void nextCallback(Object sender) {
            CCScene s = CCScene.node();
            s.addChild(nextAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void backCallback(Object sender) {
            CCScene s = CCScene.node();
            s.addChild(backAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public String title() {
            return "No Title";
        }
    }


    static class Test1 extends MotionStreakTestLayer {
        CCNode root;
        CCNode target;
        CCMotionStreak streak;

        public Test1() {
            super();
        }

        public void onEnter() {
            super.onEnter();

            CGSize s = CCDirector.sharedDirector().winSize();

            // the root object just rotates around
            root = CCSprite.sprite("r1.png");
            addChild(root, 1);
            root.setPosition(s.width / 2, s.height / 2);

            // the target object is offset from root, and the streak is moved to follow it
            target = CCSprite.sprite("r1.png");
            root.addChild(target);
            target.setPosition(100, 0);

            // create the streak object and add it to the scene
            streak = new CCMotionStreak(2, 3, "streak.png", 32, 32, new ccColor4B(0, 255, 0, 255));
            addChild(streak);
            // schedule an update on each frame so we can syncronize the streak with the target
            schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					onUpdate(d);
				}
			});

            CCIntervalAction a1 = CCRotateBy.action(2, 360);

            CCAction action1 = CCRepeatForever.action(a1);
            CCIntervalAction motion = CCMoveBy.action(2, CGPoint.make(100, 0));
            root.runAction(CCRepeatForever.action(CCSequence.actions(motion, motion.reverse())));
            root.runAction(action1);
        }

        public void onUpdate(float delta) {
            CGPoint p = target.convertToWorldSpace(0, 0);
            streak.setPosition(p);
        }

        public String title() {
            return "MotionStreak test 1";
        }
    }


    static class Test2 extends MotionStreakTestLayer {
        CCNode root;
        CCNode target;
        CCMotionStreak streak;

        public Test2() {
            super();
        }

        @Override
        public void onEnter() {
            super.onEnter();

            setIsTouchEnabled(true);

            CGSize s = CCDirector.sharedDirector().winSize();

            // create the streak object and add it to the scene
            streak = new CCMotionStreak(3, 3, "streak.png", 64, 32, new ccColor4B(255,255,255,255));
            addChild(streak);

            streak.setPosition(s.width/2, s.height/2);
        }

        public boolean ccTouchesMoved(MotionEvent e) {
            CGPoint touchLocation = CGPoint.ccp(e.getX(), e.getY());
            touchLocation = CCDirector.sharedDirector().convertToGL(touchLocation);

            streak.setPosition(touchLocation);
            return true;
        }

        public String title() {
            return "MotionStreak(touch and move)";
        }
    }

}

