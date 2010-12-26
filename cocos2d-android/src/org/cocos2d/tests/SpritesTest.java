package org.cocos2d.tests;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.camera.CCOrbitCamera;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.instant.CCInstantAction;
import org.cocos2d.actions.instant.CCPlace;
import org.cocos2d.actions.instant.CCToggleVisibility;
import org.cocos2d.actions.interval.CCBezierBy;
import org.cocos2d.actions.interval.CCBlink;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRepeat;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.actions.interval.CCTintBy;
import org.cocos2d.actions.interval.CCTintTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCBezierConfig;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.utils.CCFormatter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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


    static abstract class SpriteDemo extends CCLayer {

        CCSprite grossini;
        CCSprite tamara;

        public SpriteDemo() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
            label.setPosition(CGPoint.make(s.width / 2, s.height / 2));
            addChild(label);

            // TODO
            // Example:
            // You can create a sprite using a Texture2D
//            Texture2D tex = new Texture2D("grossini.png");
            grossini = CCSprite.sprite("grossini.png");


            // Example:
            // Or you can create an sprite using a filename. PNG, JPEG and BMP files are supported. Probably TIFF too
            tamara = CCSprite.sprite("grossinis_sister1.png");

            addChild(grossini, 1);
            // addChild(tamara, 2);

            grossini.setPosition(CGPoint.make(60, s.height / 3));
            // tamara.setPosition(CGPoint.make(60, 2 * s.height / 3));

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

        protected void centerSprites() {
            CGSize s = CCDirector.sharedDirector().winSize();

            grossini.setPosition(CGPoint.make(s.width / 3, s.height / 2));
            // tamara.setPosition(CGPoint.make(2 * s.width / 3, s.height / 2));
        }

        protected abstract String title();
    }

    static class SpriteManual extends SpriteDemo {

        @Override
        public void onEnter() {
            super.onEnter();
            
            this.setAnchorPoint(CGPoint.make(0, 0));
            tamara.setScaleX(2.5f);
            tamara.setScaleY(-1.0f);
            tamara.setPosition(CGPoint.make(100, 100));

            grossini.setRotation(120.0f);
            grossini.setOpacity(128);
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

            CGSize s = CCDirector.sharedDirector().winSize();


            CCIntervalAction actionTo = CCMoveTo.action(2, CGPoint.make(s.width - 40, s.height - 40));
            CCIntervalAction actionBy = CCMoveBy.action(2, CGPoint.make(80, 80));

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

            CCIntervalAction actionTo = CCRotateTo.action(2, 45);
            CCIntervalAction actionBy = CCRotateBy.action(2, 360);

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

            CCIntervalAction actionTo = CCScaleTo.action(2, 0.5f);
            CCIntervalAction actionBy = CCScaleBy.action(2, 2.0f);

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

            CCIntervalAction actionTo = CCJumpTo.action(2, CGPoint.make(300, 300), 50, 4);
            CCIntervalAction actionBy = CCJumpBy.action(2, CGPoint.make(300, 0), 50, 4);

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

            CGSize s = CCDirector.sharedDirector().winSize();

            //
            // startPosition can be any coordinate, but since the movement
            // is relative to the Bezier curve, item it (0,0)
            //

            // sprite 1
            CCBezierConfig bezier = new CCBezierConfig();
            bezier.controlPoint_1 = CGPoint.ccp(0, s.height / 2);
            bezier.controlPoint_2 = CGPoint.ccp(300, -s.height / 2);
            bezier.endPosition = CGPoint.ccp(300, 100);

            CCIntervalAction bezierForward = CCBezierBy.action(3, bezier);
            CCIntervalAction bezierBack = bezierForward.reverse();
            CCIntervalAction seq = CCSequence.actions(bezierForward, bezierBack);
            CCAction rep = CCRepeatForever.action(seq);


            // sprite 2
            CCBezierConfig bezier2 = new CCBezierConfig();
            bezier2.controlPoint_1 = CGPoint.ccp(100, s.height / 2);
            bezier2.controlPoint_2 = CGPoint.ccp(200, -s.height / 2);
            bezier2.endPosition = CGPoint.ccp(300, 0);

            CCIntervalAction bezierForward2 = CCBezierBy.action(3, bezier2);
            CCIntervalAction bezierBack2 = bezierForward2.reverse();
            CCIntervalAction seq2 = CCSequence.actions(bezierForward2, bezierBack2);
            CCAction rep2 = CCRepeatForever.action(seq2);


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

            CCIntervalAction action1 = CCBlink.action(2, 10);
            CCIntervalAction action2 = CCBlink.action(2, 5);

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
            CCIntervalAction action1 = CCFadeIn.action(1.0f);
            CCIntervalAction action2 = CCFadeOut.action(1.0f);

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

            CCIntervalAction action1 = CCTintTo.action(2, ccColor3B.ccc3((byte) 255, (byte) 0, (byte) 255));
            CCIntervalAction action2 = CCTintBy.action(2, ccColor3B.ccc3((byte) 0, (byte) 128, (byte) 128));

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

            CCAnimation animation = CCAnimation.animation("dance", 0.2f);
            for (int i = 1; i < 15; i++) {
                animation.addFrame(new CCFormatter().format("grossini_dance_%02d.png", i));
            }

            // CCIntervalAction action = CCAnimate.action(animation);

            // grossini.runAction(action);
        }

        public String title() {
            return "Animation";
        }
    }


    static class SpriteSequence extends SpriteDemo {
        public static CCLayer layer() {
            return new SpriteSequence();
        }

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction action = CCSequence.actions(CCMoveBy.action(2, CGPoint.make(240, 0)),
                    CCRotateBy.action(2, 540));

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

            CCIntervalAction action = CCSpawn.actions(CCJumpBy.action(2, CGPoint.make(300, 0), 50, 4),
                    CCRotateBy.action(2, 720));

            grossini.runAction(action);
        }

        public String title() {
            return "Spawn: Jump + Rotate";
        }
    }

    static class SpriteReverse extends SpriteDemo {
        public static CCLayer layer() {
            return new SpriteReverse();
        }

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction jump = CCJumpBy.action(2, CGPoint.make(300, 0), 50, 4);
            CCIntervalAction action = CCSequence.actions(jump, jump.reverse());

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

            CCIntervalAction move = CCMoveBy.action(1, CGPoint.make(150, 0));
            CCIntervalAction action = CCSequence.actions(move, CCDelayTime.action(2), move);

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

            CCIntervalAction move1 = CCMoveBy.action(1, CGPoint.make(250, 0));
            CCIntervalAction move2 = CCMoveBy.action(1, CGPoint.make(0, 50));
            CCIntervalAction seq = CCSequence.actions(move1, move2, move1.reverse());
            CCAction action = CCSequence.actions(seq, seq.reverse());

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

            CCIntervalAction move1 = CCMoveBy.action(1, CGPoint.make(250, 0));
            CCIntervalAction move2 = CCMoveBy.action(1, CGPoint.make(0, 50));
            CCInstantAction tog1 = CCToggleVisibility.action();
            CCInstantAction tog2 = CCToggleVisibility.action();
            CCIntervalAction seq = CCSequence.actions(move1, tog1, move2, tog2, move1.reverse());
            CCAction action = CCRepeat.action(CCSequence.actions(seq, seq.reverse()), 3);


            // Test:
            //   Also test that the reverse of Hide is Show, and vice-versa
            grossini.runAction(action);

            CCIntervalAction move_tamara = CCMoveBy.action(1, CGPoint.make(100, 0));
            CCIntervalAction move_tamara2 = CCMoveBy.action(1, CGPoint.make(50, 0));
            CCInstantAction hide = new CCHide();
            CCIntervalAction seq_tamara = CCSequence.actions(move_tamara, hide, move_tamara2);
            CCIntervalAction seq_back = seq_tamara.reverse();
            tamara.runAction(CCSequence.actions(seq_tamara, seq_back));
        }

        public String title() {
            return "Reverse Sequence 2";
        }
    }


    static class SpriteRepeat extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            CGPoint pos = CGPoint.make(150, 0);
            CCIntervalAction a1 = CCMoveBy.action(1, pos);
            pos.x = 60; pos.y = 60;
            CCAction action1 = CCRepeat.action(CCSequence.actions(CCPlace.action(pos), a1), 3);
            CCAction action2 = CCRepeatForever.action(CCSequence.actions(a1.copy(), a1.reverse()));

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

            CCIntervalAction action = CCSequence.actions(
                    CCMoveBy.action(2, CGPoint.make(200, 0)),
                    CCCallFunc.action(this, "callback"));
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

            CCIntervalAction orbit1 = CCOrbitCamera.action(2, 1, 0, 0, 180, 0, 0);
            CCIntervalAction action1 = CCSequence.actions(
                    orbit1,
                    orbit1.reverse());

            CCIntervalAction orbit2 = CCOrbitCamera.action(2, 1, 0, 0, 180, -45, 0);
            CCAction action2 = CCSequence.actions(
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
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        CCDirector.sharedDirector().setLandscape(false);

        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        CCScene scene = CCScene.node();
        scene.setAnchorPoint(CGPoint.make(0, 0));
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
}
