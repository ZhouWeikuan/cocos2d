package org.cocos2d.tests;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.RotateBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCColor4B;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

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

    public static final int kTagLabel = 1;
    public static final int kTagSprite1 = 1;
    public static final int kTagSprite2 = 2;


    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Test1.class,
//            Test2.class,
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

            Label label = Label.label(title(), "DroidSans", 24);
            addChild(label, kTagLabel);
            label.setPosition(s.width / 2, s.height / 2);

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
        CocosNode root;
        CocosNode target;
        MotionStreak streak;

        public void onEnter() {
            super.onEnter();

            CCSize s = Director.sharedDirector().winSize();

            // the root object just rotates around
            root = Sprite.sprite("r1.png");
            addChild(root, 1);
            root.setPosition(s.width / 2, s.height / 2);

            // the target object is offset from root, and the streak is moved to follow it
            target = Sprite.sprite("r1.png");
            root.addChild(target);
            target.setPosition(100, 0);

            // create the streak object and add it to the scene
            streak = new MotionStreak(2, 3, "streak.png", 32, 32, new CCColor4B(0, 255, 0, 255));
            addChild(streak);
            // schedule an update on each frame so we can syncronize the streak with the target
            schedule("onUpdate");

            IntervalAction a1 = RotateBy.action(2, 360);

            Action action1 = RepeatForever.action(a1);
            IntervalAction motion = MoveBy.action(2, 100, 0);
            root.runAction(RepeatForever.action(Sequence.actions(motion, motion.reverse())));
            root.runAction(action1);
        }

        public void onUpdate(float delta) {
            //  CCPoint p = target.absolutePosition();
            //  float r = root.getRotation();
            CCPoint p = target.convertToWorldSpace(0, 0);
            streak.setPosition(p.x, p.y);

        }

        public String title() {
            return "MotionStreak test 1";
        }
    }
}
