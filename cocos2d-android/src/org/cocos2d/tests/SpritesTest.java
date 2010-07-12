package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.camera.OrbitCamera;
import org.cocos2d.actions.instant.*;
import org.cocos2d.actions.interval.*;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCBezierConfig;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.CCFormatter;

public class SpritesTest extends Activity {
    // private static final String LOG_TAG = SpritesTest.class.getSimpleName();
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

    public static final int kTagAnimationDance = 1;

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            SpriteManual.class,
            SpriteMove.class,
            SpriteRotate.class,
            SpriteScale.class,
            SpriteJump.class,
            SpriteBezier.class,
            SpriteBlink.class,
            SpriteFade.class,
            SpriteTint.class,
            SpriteAnimate.class,
            SpriteSequence.class,
            SpriteSpawn.class,
            SpriteReverse.class,
            SpriteDelayTime.class,
            SpriteRepeat.class,
            SpriteCallFunc.class,
            SpriteReverseSequence.class,
            SpriteReverseSequence2.class,
            SpriteOrbit.class
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


    static abstract class SpriteDemo extends Layer {

        Sprite grossini;
        Sprite tamara;

        public SpriteDemo() {
            CGSize s = Director.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
            label.setPosition(CGPoint.make(s.width / 2, s.height / 2));
            addChild(label);

            // TODO
            // Example:
            // You can create a sprite using a Texture2D
//            Texture2D tex = new Texture2D("grossini.png");
            grossini = Sprite.sprite("grossini.png");


            // Example:
            // Or you can create an sprite using a filename. PNG, JPEG and BMP files are supported. Probably TIFF too
            tamara = Sprite.sprite("grossinis_sister1.png");

            addChild(grossini, 1);
            addChild(tamara, 2);

            grossini.setPosition(CGPoint.make(60, s.height / 3));
            tamara.setPosition(CGPoint.make(60, 2 * s.height / 3));

            MenuItemImage item1 = MenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            MenuItemImage item2 = MenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            MenuItemImage item3 = MenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            Menu menu = Menu.menu(item1, item2, item3);
            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
            addChild(menu, 1);
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


        protected void centerSprites() {
            CGSize s = Director.sharedDirector().winSize();

            grossini.setPosition(CGPoint.make(s.width / 3, s.height / 2));
            tamara.setPosition(CGPoint.make(2 * s.width / 3, s.height / 2));
        }

        protected abstract String title();
    }


    static class SpriteManual extends SpriteDemo {

        @Override
        public void onEnter() {
            super.onEnter();


            tamara.setScaleX(2.5f);
            tamara.setScaleY(-1.0f);
            tamara.setPosition(CGPoint.make(100, 100));

            grossini.setRotation(120.0f);
            grossini.setOpacity((byte) 128);
            grossini.setPosition(CGPoint.make(240, 160));
        }

        @Override
        public String title() {
            return "Manual Transformation";
        }
    }


    static class SpriteMove extends SpriteDemo {

        @Override
        public void onEnter() {
            super.onEnter();

            CGSize s = Director.sharedDirector().winSize();


            IntervalAction actionTo = MoveTo.action(2, s.width - 40, s.height - 40);
            IntervalAction actionBy = MoveBy.action(2, 80, 80);

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        @Override
        public String title() {
            return "MoveTo / MoveBy";
        }
    }

    static class SpriteRotate extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            IntervalAction actionTo = RotateTo.action(2, 45);
            IntervalAction actionBy = RotateBy.action(2, 360);

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        public String title() {
            return "RotateTo / RotateBy";
        }
    }

    static class SpriteScale extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            IntervalAction actionTo = ScaleTo.action(2, 0.5f);
            IntervalAction actionBy = ScaleBy.action(2, 2.0f);

            //	grossini.transformAnchor_ = CCPoint.ccp( [grossini transformAnchor_].x, 0 );

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        public String title() {
            return "ScaleTo / ScaleBy";
        }

    }

    static class SpriteJump extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            IntervalAction actionTo = JumpTo.action(2, 300, 300, 50, 4);
            IntervalAction actionBy = JumpBy.action(2, 300, 0, 50, 4);

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        public String title() {
            return "JumpTo / JumpBy";
        }
    }

    static class SpriteBezier extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            CGSize s = Director.sharedDirector().winSize();

            //
            // startPosition can be any coordinate, but since the movement
            // is relative to the Bezier curve, item it (0,0)
            //

            // sprite 1
            CCBezierConfig bezier = new CCBezierConfig();
            bezier.startPosition = CGPoint.ccp(0, 0);
            bezier.controlPoint_1 = CGPoint.ccp(0, s.height / 2);
            bezier.controlPoint_2 = CGPoint.ccp(300, -s.height / 2);
            bezier.endPosition = CGPoint.ccp(300, 100);

            IntervalAction bezierForward = BezierBy.action(3, bezier);
            IntervalAction bezierBack = bezierForward.reverse();
            IntervalAction seq = Sequence.actions(bezierForward, bezierBack);
            Action rep = RepeatForever.action(seq);


            // sprite 2
            CCBezierConfig bezier2 = new CCBezierConfig();
            bezier2.startPosition = CGPoint.ccp(0, 0);
            bezier2.controlPoint_1 = CGPoint.ccp(100, s.height / 2);
            bezier2.controlPoint_2 = CGPoint.ccp(200, -s.height / 2);
            bezier2.endPosition = CGPoint.ccp(300, 0);

            IntervalAction bezierForward2 = BezierBy.action(3, bezier2);
            IntervalAction bezierBack2 = bezierForward2.reverse();
            IntervalAction seq2 = Sequence.actions(bezierForward2, bezierBack2);
            Action rep2 = RepeatForever.action(seq2);


            grossini.runAction(rep);
            tamara.runAction(rep2);
        }

        public String title() {
            return "BezierBy";
        }
    }


    static class SpriteBlink extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            IntervalAction action1 = Blink.action(2, 10);
            IntervalAction action2 = Blink.action(2, 5);

            tamara.runAction(action1);
            grossini.runAction(action2);
        }

        public String title() {
            return "Blink";
        }
    }

    static class SpriteFade extends SpriteDemo {
        public void onEnter() {
            super.onEnter();

            centerSprites();

            tamara.setOpacity((byte) 0);
            IntervalAction action1 = FadeIn.action(1.0f);
            IntervalAction action2 = FadeOut.action(1.0f);

            tamara.runAction(action1);
            grossini.runAction(action2);
        }

        public String title() {
            return "FadeIn / FadeOut";
        }
    }

    static class SpriteTint extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            IntervalAction action1 = TintTo.action(2, (byte) 255, (byte) 0, (byte) 255);
            IntervalAction action2 = TintBy.action(2, (byte) 0, (byte) 128, (byte) 128);

            tamara.runAction(action1);
            grossini.runAction(action2);
        }

        public String title() {
            return "TintTo / TintBy";
        }
    }

    static class SpriteAnimate extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            tamara.setVisible(false);

            Animation animation = new Animation("dance", 0.2f);
            for (int i = 1; i < 15; i++) {
                animation.addFrame(new CCFormatter().format("grossini_dance_%02d.png", i));
            }

            IntervalAction action = Animate.action(animation);

            grossini.runAction(action);
        }

        public String title() {
            return "Animation";
        }
    }


    static class SpriteSequence extends SpriteDemo {
        public static Layer layer() {
            return new SpriteSequence();
        }

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            IntervalAction action = Sequence.actions(MoveBy.action(2, 240, 0),
                    RotateBy.action(2, 540));

            grossini.runAction(action);
        }

        public String title() {
            return "Sequence: Move + Rotate";
        }
    }

    static class SpriteSpawn extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            IntervalAction action = Spawn.actions(JumpBy.action(2, 300, 0, 50, 4),
                    RotateBy.action(2, 720));

            grossini.runAction(action);
        }

        public String title() {
            return "Spawn: Jump + Rotate";
        }
    }

    static class SpriteReverse extends SpriteDemo {
        public static Layer layer() {
            return new SpriteReverse();
        }

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            IntervalAction jump = JumpBy.action(2, 300, 0, 50, 4);
            IntervalAction action = Sequence.actions(jump, jump.reverse());

            grossini.runAction(action);
        }

        public String title() {
            return "Reverse an Action";
        }
    }

    static class SpriteDelayTime extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            IntervalAction move = MoveBy.action(1, 150, 0);
            IntervalAction action = Sequence.actions(move, DelayTime.action(2), move);

            grossini.runAction(action);
        }

        public String title() {
            return "DelayTime: m + Delay + m";
        }
    }

    static class SpriteReverseSequence extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            IntervalAction move1 = MoveBy.action(1, 250, 0);
            IntervalAction move2 = MoveBy.action(1, 0, 50);
            IntervalAction seq = Sequence.actions(move1, move2, move1.reverse());
            Action action = Sequence.actions(seq, seq.reverse());

            grossini.runAction(action);
        }

        public String title() {
            return "Reverse a Sequence";
        }
    }

    static class SpriteReverseSequence2 extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            // Test:
            //   Sequence should work both with IntervalAction and InstantActions

            IntervalAction move1 = MoveBy.action(1, 250, 0);
            IntervalAction move2 = MoveBy.action(1, 0, 50);
            InstantAction tog1 = ToggleVisibility.action();
            InstantAction tog2 = ToggleVisibility.action();
            IntervalAction seq = Sequence.actions(move1, tog1, move2, tog2, move1.reverse());
            Action action = Repeat.action(Sequence.actions(seq, seq.reverse()), 3);


            // Test:
            //   Also test that the reverse of Hide is Show, and vice-versa
            grossini.runAction(action);

            IntervalAction move_tamara = MoveBy.action(1, 100, 0);
            IntervalAction move_tamara2 = MoveBy.action(1, 50, 0);
            InstantAction hide = new Hide();
            IntervalAction seq_tamara = Sequence.actions(move_tamara, hide, move_tamara2);
            IntervalAction seq_back = seq_tamara.reverse();
            tamara.runAction(Sequence.actions(seq_tamara, seq_back));
        }

        public String title() {
            return "Reverse Sequence 2";
        }
    }


    static class SpriteRepeat extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            IntervalAction a1 = MoveBy.action(1, 150, 0);
            Action action1 = Repeat.action(Sequence.actions(Place.action(60, 60), a1), 3);
            Action action2 = RepeatForever.action(Sequence.actions(a1.copy(), a1.reverse()));

            grossini.runAction(action1);
            tamara.runAction(action2);
        }

        public String title() {
            return "Repeat / RepeatForever actions";
        }
    }

    static class SpriteCallFunc extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            IntervalAction action = Sequence.actions(
                    MoveBy.action(2, 200, 0),
                    CallFunc.action(this, "callback"));
            grossini.runAction(action);
        }

        public void callback() {
            tamara.setVisible(true);
        }

        public String title() {
            return "Callback Action: CallFunc";
        }
    }

    static class SpriteOrbit extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            IntervalAction orbit1 = OrbitCamera.action(2, 1, 0, 0, 180, 0, 0);
            IntervalAction action1 = Sequence.actions(
                    orbit1,
                    orbit1.reverse());

            IntervalAction orbit2 = OrbitCamera.action(2, 1, 0, 0, 180, -45, 0);
            Action action2 = Sequence.actions(
                    orbit2,
                    orbit2.reverse());


            grossini.runAction(action1);
            tamara.runAction(action2);
        }


        public String title() {
            return "OrbitCamera Action";
        }
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
}
