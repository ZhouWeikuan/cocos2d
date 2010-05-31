package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.instant.CallFuncN;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.RotateBy;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.particlesystem.ParticleSun;
import org.cocos2d.types.CCSize;

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
        Director.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        Director.sharedDirector().setLandscape(false);

        // show FPS
        Director.sharedDirector().setDisplayFPS(true);

        // frames per second
        Director.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = Scene.node();
        scene.addChild(nextAction());

        // Make the Scene active
        Director.sharedDirector().runWithScene(scene);

    }

    @Override
    public void onPause() {
        super.onPause();

        Director.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        Director.sharedDirector().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        TextureManager.sharedTextureManager().removeAllTextures();
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

    static Layer nextAction() {

        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;

        return restartAction();
    }

    static Layer backAction() {
        sceneIdx--;
        int total = transitions.length;
        if (sceneIdx < 0)
            sceneIdx += total;
        return restartAction();
    }

    static Layer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (Layer) c.newInstance();
        } catch (Exception e) {
            return null;
        }
    }


    static abstract class TestDemo extends Layer {
        public TestDemo() {
            CCSize s = Director.sharedDirector().winSize();

            Label label = Label.label(title(), "DroidSans", 18);
            label.setPosition(s.width / 2, s.height - 30);
            addChild(label);

            MenuItemImage item1 = MenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            MenuItemImage item2 = MenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            MenuItemImage item3 = MenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            Menu menu = Menu.menu(item1, item2, item3);
            menu.setPosition(0, 0);
            item1.setPosition(s.width / 2 - 100, 30);
            item2.setPosition(s.width / 2, 30);
            item3.setPosition(s.width / 2 + 100, 30);
            addChild(menu, -1);
        }

        public static void restartCallback() {
            Scene s = Scene.node();
            s.addChild(restartAction());
            Director.sharedDirector().replaceScene(s);
        }

        public void nextCallback() {
            Scene s = Scene.node();
            s.addChild(nextAction());
            Director.sharedDirector().replaceScene(s);
        }

        public void backCallback() {
            Scene s = Scene.node();
            s.addChild(backAction());
            Director.sharedDirector().replaceScene(s);
        }

        public abstract String title();
    }

    static class Test1 extends TestDemo {

        public void onEnter() {
            super.onEnter();

            CCSize s = Director.sharedDirector().winSize();

            Sprite sp0 = Sprite.sprite("grossini.png");
            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp2 = Sprite.sprite("grossinis_sister2.png");
            Sprite point0 = Sprite.sprite("r1.png");
            Sprite point1 = Sprite.sprite("r1.png");
            Sprite point2 = Sprite.sprite("r1.png");

            point0.scale(0.25f);
            point1.scale(0.25f);
            point2.scale(0.25f);

            sp0.setPosition(s.width/2, s.height / 2);
            point0.setPosition(sp0.getPositionX(), sp0.getPositionY());
            sp0.setAnchorPoint(0.5f, 0.5f);

            sp1.setPosition(s.width/2 - 100, s.height / 2);
            point1.setPosition(sp1.getPositionX(), sp1.getPositionY());
            sp1.setAnchorPoint(0, 0);

            sp2.setPosition(s.width/2 + 100, s.height / 2);
            point2.setPosition(sp2.getPositionX(), sp2.getPositionY());
            sp2.setAnchorPoint(1, 1);

            addChild(sp0);
            addChild(sp1);
            addChild(sp2);

            addChild(point0, 1);
            addChild(point1, 1);
            addChild(point2, 1);

            IntervalAction a1 = RotateBy.action(2, 360);
            IntervalAction a2 = ScaleBy.action(2, 2);

            Action action1 = RepeatForever.action(Sequence.actions(a1, a2, a2.reverse()));

            Action action2 = action1.copy();
            Action action0 = action1.copy();

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

            CCSize s = Director.sharedDirector().winSize();

            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp2 = Sprite.sprite("grossinis_sister2.png");
            Sprite sp3 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp4 = Sprite.sprite("grossinis_sister2.png");

            sp1.setPosition(100, s.height / 2);
            sp2.setPosition(s.width - 100, s.height / 2);
            addChild(sp1);
            addChild(sp2);

            sp3.scale(0.25f);
            sp4.scale(0.25f);

            sp1.addChild(sp3);
            sp2.addChild(sp4);

            IntervalAction a1 = RotateBy.action(2, 360);
            IntervalAction a2 = ScaleBy.action(2, 2);

            Action action1 = RepeatForever.action(Sequence.actions(a1, a2, a2.reverse()));
            Action action2 = RepeatForever.action(Sequence.actions(a1.copy(), a2.copy(), a2.reverse()));

            sp2.setAnchorPoint(0, 0);

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

            CCSize s = Director.sharedDirector().winSize();

            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp2 = Sprite.sprite("grossinis_sister2.png");
            Sprite sp3 = Sprite.sprite("grossini.png");

            sp1.setPosition(20, 80);
            sp2.setPosition(70, 50);
            sp3.setPosition(s.width / 2, s.height / 2);

            // these tags belong to sp3 (kTagSprite1, kTagSprite2)
            sp3.addChild(sp1, -1, kTagSprite1);
            sp3.addChild(sp2, 1, kTagSprite2);

            // this tag belong to Test3 (kTagSprite1)
            addChild(sp3, 0, kTagSprite1);

            IntervalAction a1 = RotateBy.action(4, 360);
            Action action1 = RepeatForever.action(a1);
            sp3.runAction(action1);

            schedule("changeZOrder", 2.0f);
        }

        public void changeZOrder(float dt) {
            CocosNode grossini = getChild(kTagSprite1);

            CocosNode sprite1 = grossini.getChild(kTagSprite1);
            CocosNode sprite2 = grossini.getChild(kTagSprite2);

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

            CCSize s = Director.sharedDirector().winSize();

            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp2 = Sprite.sprite("grossinis_sister2.png");

            sp1.setPosition(100, s.height / 2);
            sp2.setPosition(s.width - 100, s.height / 2);

            addChild(sp1, 0, 2);
            addChild(sp2, 0, 3);

            schedule("delay2", 2.0f);
            schedule("delay4", 4.0f);
        }

        public void delay2(float dt) {
            CocosNode node = getChild(2);
            IntervalAction action1 = RotateBy.action(1, 360);
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
            CCSize s = Director.sharedDirector().winSize();

            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp2 = Sprite.sprite("grossinis_sister2.png");

            sp1.setPosition(100, s.height / 2);
            sp2.setPosition(s.width - 100, s.height / 2);

            IntervalAction rot = RotateBy.action(2, 360);
            IntervalAction rot_back = rot.reverse();
            Action forever = RepeatForever.action(
                    Sequence.actions(rot, rot_back));
            Action forever2 = forever.copy();

            addChild(sp1, 0, kTagSprite1);
            addChild(sp2, 0, kTagSprite2);

            sp1.runAction(forever);
            sp2.runAction(forever2);

            schedule("addAndRemove", 2.0f);
        }

        public void addAndRemove(float dt) {
            CocosNode sp1 = getChild(kTagSprite1);
            CocosNode sp2 = getChild(kTagSprite2);

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
            CCSize s = Director.sharedDirector().winSize();

            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            Sprite sp11 = Sprite.sprite("grossinis_sister1.png");

            Sprite sp2 = Sprite.sprite("grossinis_sister2.png");
            Sprite sp21 = Sprite.sprite("grossinis_sister2.png");

            sp1.setPosition(100, s.height / 2);
            sp2.setPosition(s.width - 100, s.height / 2);


            IntervalAction rot = RotateBy.action(2, 360);
            IntervalAction rot_back = rot.reverse();
            Action forever1 = RepeatForever.action(Sequence.actions(rot, rot_back));
            Action forever11 = forever1.copy();

            Action forever2 = forever1.copy();
            Action forever21 = forever1.copy();

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
            CocosNode sp1 = getChild(kTagSprite1);
            CocosNode sp2 = getChild(kTagSprite2);

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
            CCSize s = Director.sharedDirector().winSize();

            Sprite sp1 = Sprite.sprite("grossinis_sister1.png");
            addChild(sp1, 0, kTagSprite1);

            sp1.setPosition(s.width / 2, s.height / 2);

            schedule("shouldNotCrash", 1.0f);
        }

        public void shouldNotCrash(float delta) {
            unschedule("shouldNotCrash");

            CCSize s = Director.sharedDirector().winSize();

            // if the node has timers, it crashes
            CocosNode explosion = ParticleSun.node();

            // if it doesn't, it works Ok.
            //	CocosNode explosion = Sprite.sprite("grossinis_sister2.png");

            explosion.setPosition(s.width / 2, s.height / 2);

            runAction(Sequence.actions(RotateBy.action(2, 360),
                    CallFuncN.action(this, "removeMe")));

            addChild(explosion);
        }

        // remove
        public void removeMe(CocosNode node) {
            parent.removeChild(node, true);
            nextCallback();
        }


        public String title() {
            return "Stress Test #1";
        }
    }

}
