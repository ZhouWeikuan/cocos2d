package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.particlesystem.ParticleSun;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class CocosNodeTest extends Activity {
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

        CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    public static final int kTagSprite1 = 1;
    public static final int kTagSprite2 = 2;
    public static final int kTagSprite3 = 3;


    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Test1.class,
            Test2.class,
            Test3.class,
            Test4.class,
            Test5.class,
            Test6.class,
            Test7.class,
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

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 18);
            label.setPosition(CGPoint.make(s.width / 2, s.height - 30));
            addChild(label);

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);
            menu.setPosition(CGPoint.zero());
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

        public void onEnter() {
            super.onEnter();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp0 = CCSprite.sprite("grossini.png");
            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");
            CCSprite point0 = CCSprite.sprite("r1.png");
            CCSprite point1 = CCSprite.sprite("r1.png");
            CCSprite point2 = CCSprite.sprite("r1.png");

            point0.setScale(0.25f);
            point1.setScale(0.25f);
            point2.setScale(0.25f);

            sp0.setPosition(CGPoint.make(s.width/2, s.height / 2));
            point0.setPosition(sp0.getPosition());
            sp0.setAnchorPoint(CGPoint.make(0.5f, 0.5f));

            sp1.setPosition(CGPoint.make(s.width/2 - 100, s.height / 2));
            point1.setPosition(sp1.getPosition());
            sp1.setAnchorPoint(CGPoint.zero());

            sp2.setPosition(CGPoint.make(s.width/2 + 100, s.height / 2));
            point2.setPosition(sp2.getPosition());
            sp2.setAnchorPoint(CGPoint.make(1, 1));

            addChild(sp0);
            addChild(sp1);
            addChild(sp2);

            addChild(point0, 1);
            addChild(point1, 1);
            addChild(point2, 1);

            CCIntervalAction a1 = CCRotateBy.action(2, 360);
            CCIntervalAction a2 = CCScaleBy.action(2, 2);

            CCAction action1 = CCRepeatForever.action(CCSequence.actions(a1, a2, a2.reverse()));

            CCAction action2 = action1.copy();
            CCAction action0 = action1.copy();

            sp0.runAction(action0);
            sp1.runAction(action1);
            sp2.runAction(action2);
        }

        public String title() {
            return "Transform Anchor";
        }
    }

    static class Test2 extends TestDemo {

        public void onEnter() {
            super.onEnter();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");
            CCSprite sp3 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp4 = CCSprite.sprite("grossinis_sister2.png");

            sp1.setPosition(CGPoint.make(100, s.height / 2));
            sp2.setPosition(CGPoint.make(s.width - 100, s.height / 2));
            addChild(sp1);
            addChild(sp2);

            sp3.setScale(0.25f);
            sp4.setScale(0.25f);

            sp1.addChild(sp3);
            sp2.addChild(sp4);

            CCIntervalAction a1 = CCRotateBy.action(2, 360);
            CCIntervalAction a2 = CCScaleBy.action(2, 2);

            CCAction action1 = CCRepeatForever.action(CCSequence.actions(a1, a2, a2.reverse()));
            CCAction action2 = CCRepeatForever.action(CCSequence.actions(a1.copy(), a2.copy(), a2.reverse()));

            sp2.setAnchorPoint(CGPoint.zero());

            sp1.runAction(action1);
            sp2.runAction(action2);
        }

        public String title() {
            return "Transform Anchor and Children";
        }
    }

    static class Test3 extends TestDemo {

        public void onEnter() {
            super.onEnter();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");
            CCSprite sp3 = CCSprite.sprite("grossini.png");

            sp1.setPosition(CGPoint.make(20, 80));
            sp2.setPosition(CGPoint.make(70, 50));
            sp3.setPosition(CGPoint.make(s.width / 2, s.height / 2));

            // these tags belong to sp3 (kTagSprite1, kTagSprite2)
            sp3.addChild(sp1, -1, kTagSprite1);
            sp3.addChild(sp2, 1, kTagSprite2);

            // this tag belong to Test3 (kTagSprite1)
            addChild(sp3, 0, kTagSprite1);

            CCIntervalAction a1 = CCRotateBy.action(4, 360);
            CCAction action1 = CCRepeatForever.action(a1);
            sp3.runAction(action1);

            schedule("changeZOrder", 2.0f);
        }

        public void changeZOrder(float dt) {
            CCNode grossini = getChild(kTagSprite1);

            CCNode sprite1 = grossini.getChild(kTagSprite1);
            CCNode sprite2 = grossini.getChild(kTagSprite2);

            int zt = sprite1.getZOrder();
            grossini.reorderChild(sprite1, sprite2.getZOrder());
            grossini.reorderChild(sprite2, zt);
        }

        public String title() {
            return "Z Order";
        }
    }

    static class Test4 extends TestDemo {

        public Test4() {

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");

            sp1.setPosition(CGPoint.make(100, s.height / 2));
            sp2.setPosition(CGPoint.make(s.width - 100, s.height / 2));

            addChild(sp1, 0, 2);
            addChild(sp2, 0, 3);

            schedule("delay2", 2.0f);
            schedule("delay4", 4.0f);
        }

        public void delay2(float dt) {
            CCNode node = getChild(2);
            CCIntervalAction action1 = CCRotateBy.action(1, 360);
            node.runAction(action1);
        }

        public void delay4(float dt) {
            unschedule("delay4");
            removeChild(3, false);
        }


        public String title() {
            return "Tags";
        }
    }

    static class Test5 extends TestDemo {

        public Test5() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");

            sp1.setPosition(CGPoint.make(100, s.height / 2));
            sp2.setPosition(CGPoint.make(s.width - 100, s.height / 2));

            CCIntervalAction rot = CCRotateBy.action(2, 360);
            CCIntervalAction rot_back = rot.reverse();
            CCAction forever = CCRepeatForever.action(
                    CCSequence.actions(rot, rot_back));
            CCAction forever2 = forever.copy();

            addChild(sp1, 0, kTagSprite1);
            addChild(sp2, 0, kTagSprite2);

            sp1.runAction(forever);
            sp2.runAction(forever2);

            schedule("addAndRemove", 2.0f);
        }

        public void addAndRemove(float dt) {
            CCNode sp1 = getChild(kTagSprite1);
            CCNode sp2 = getChild(kTagSprite2);

            removeChild(sp1, false);
            removeChild(sp2, true);

            addChild(sp1, 0, kTagSprite1);
            addChild(sp2, 0, kTagSprite2);
        }

        public String title() {
            return "Remove";
        }
    }

    static class Test6 extends TestDemo {

        public Test6() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite sp11 = CCSprite.sprite("grossinis_sister1.png");

            CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");
            CCSprite sp21 = CCSprite.sprite("grossinis_sister2.png");

            sp1.setPosition(CGPoint.make(100, s.height / 2));
            sp2.setPosition(CGPoint.make(s.width - 100, s.height / 2));


            CCIntervalAction rot = CCRotateBy.action(2, 360);
            CCIntervalAction rot_back = rot.reverse();
            CCAction forever1 = CCRepeatForever.action(CCSequence.actions(rot, rot_back));
            CCAction forever11 = forever1.copy();

            CCAction forever2 = forever1.copy();
            CCAction forever21 = forever1.copy();

            addChild(sp1, 0, kTagSprite1);
            sp1.addChild(sp11);
            addChild(sp2, 0, kTagSprite2);
            sp2.addChild(sp21);

            sp1.runAction(forever1);
            sp11.runAction(forever11);
            sp2.runAction(forever2);
            sp21.runAction(forever21);

            schedule("addAndRemove", 2.0f);
        }

        public void addAndRemove(float dt) {
            CCNode sp1 = getChild(kTagSprite1);
            CCNode sp2 = getChild(kTagSprite2);

            removeChild(sp1, false);
            removeChild(sp2, true);

            addChild(sp1, 0, kTagSprite1);
            addChild(sp2, 0, kTagSprite2);
        }


        public String title() {
            return "Remove with Children";
        }
    }


    static class Test7 extends TestDemo {

        public Test7() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
            addChild(sp1, 0, kTagSprite1);

            sp1.setPosition(CGPoint.make(s.width / 2, s.height / 2));

            schedule("shouldNotCrash", 1.0f);
        }

        public void shouldNotCrash(float delta) {
            unschedule("shouldNotCrash");

            CGSize s = CCDirector.sharedDirector().winSize();

            // if the node has timers, it crashes
            CCNode explosion = ParticleSun.node();

            // if it doesn't, it works Ok.
            //	CocosNode explosion = Sprite.sprite("grossinis_sister2.png");

            explosion.setPosition(CGPoint.make(s.width / 2, s.height / 2));

            runAction(CCSequence.actions(CCRotateBy.action(2, 360),
                    CCCallFuncN.action(this, "removeMe")));

            addChild(explosion);
        }

        // remove
        public void removeMe(CCNode node) {
            parent_.removeChild(node, true);
            nextCallback();
        }


        public String title() {
            return "Stress Test #1";
        }
    }

}
