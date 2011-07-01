package org.cocos2d.tests;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.base.CCSpeed;
import org.cocos2d.actions.ease.CCEaseBackIn;
import org.cocos2d.actions.ease.CCEaseBackInOut;
import org.cocos2d.actions.ease.CCEaseBackOut;
import org.cocos2d.actions.ease.CCEaseBounceIn;
import org.cocos2d.actions.ease.CCEaseBounceInOut;
import org.cocos2d.actions.ease.CCEaseBounceOut;
import org.cocos2d.actions.ease.CCEaseElasticIn;
import org.cocos2d.actions.ease.CCEaseElasticInOut;
import org.cocos2d.actions.ease.CCEaseElasticOut;
import org.cocos2d.actions.ease.CCEaseExponentialIn;
import org.cocos2d.actions.ease.CCEaseExponentialInOut;
import org.cocos2d.actions.ease.CCEaseExponentialOut;
import org.cocos2d.actions.ease.CCEaseIn;
import org.cocos2d.actions.ease.CCEaseInOut;
import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.ease.CCEaseSineIn;
import org.cocos2d.actions.ease.CCEaseSineInOut;
import org.cocos2d.actions.ease.CCEaseSineOut;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class EaseActionsTest extends Activity {
    // private static final String LOG_TAG = AtlasTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        scene.addChild(nextAction());

        // Make the Scene active
        CCDirector.sharedDirector().runWithScene(scene);
    }

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            SpriteEase.class,
            SpriteEaseInOut.class,
            SpriteEaseExponential.class,
            SpriteEaseExponentialInOut.class,
            SpriteEaseSine.class,
            SpriteEaseSineInOut.class,
            SpriteEaseElastic.class,
            SpriteEaseElasticInOut.class,
            SpriteEaseBounce.class,
            SpriteEaseBounceInOut.class,
            SpriteEaseBack.class,
            SpriteEaseBackInOut.class,
            SpeedTest.class,
//            SchedulerTest.class,
    };

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
        // CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    static final int kTagAction1 = 1;
    static final int kTagAction2 = 2;
    static final int kTagSlider = 3;

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
        CCSprite kathia;
         
        public SpriteDemo() {

            // Example:
            // You can create a sprite using a CCTexture2D
            CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage("grossini.png");
            grossini = CCSprite.sprite(tex);

            // Example:
            // Or you can create an sprite using a filename. PNG and BMP files are supported. Probably TIFF too
            tamara = CCSprite.sprite("grossinis_sister1.png");
            kathia = CCSprite.sprite("grossinis_sister2.png");

            addChild(grossini, 3);
            addChild(kathia, 2);
            addChild(tamara, 1);


            CGSize s = CCDirector.sharedDirector().winSize();

            grossini.setPosition(CGPoint.make(60, 50));
            kathia.setPosition(CGPoint.make(60, 150));
            tamara.setPosition(CGPoint.make(60, 250));

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
            addChild(label, 1);
            label.setPosition(CGPoint.make(s.width / 2, s.height - 50));

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

        public void positionForTwo() {
            grossini.setPosition(CGPoint.make( 60, 120 ));
            tamara.setPosition(CGPoint.make( 60, 220));
            kathia.setVisible(false);
        }
        
        public String title() {
        	return "No Title";
        }
    }


    static class SpriteEase extends SpriteDemo {
        public void onEnter() {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_in = CCEaseIn.action(move.copy(), 3.0f);
            CCIntervalAction move_ease_in_back = move_ease_in.reverse();
        
            CCIntervalAction move_ease_out = CCEaseOut.action(move.copy(), 3.0f);
            CCIntervalAction move_ease_out_back = move_ease_out.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
        
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_in, delay.copy(), move_ease_in_back, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_out, delay.copy(), move_ease_out_back, delay.copy());
        
        
            CCAction a2 = grossini.runAction(CCRepeatForever.action(seq1));
            a2.setTag(1);
        
            CCAction a1 = tamara.runAction(CCRepeatForever.action(seq2));
            a1.setTag(1);
        
            CCAction a = kathia.runAction(CCRepeatForever.action(seq3));
            a.setTag(1);
            
            schedule("testStopAction", 6.25f);
        }
        
        public void testStopAction(float dt) {
            unschedule("testStopAction");
            tamara.stopAction(1);
            kathia.stopAction(1);
            grossini.stopAction(1);
        }
        
        public String title() {
            return "EaseIn - EaseOut - Stop";
        }
    }

    static class SpriteEaseInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
        //	IntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_inout1 = CCEaseInOut.action(move.copy(), 2.0f);
            CCIntervalAction move_ease_inout_back1 = move_ease_inout1.reverse();
        
            CCIntervalAction move_ease_inout2 = CCEaseInOut.action(move.copy(), 3.0f);
            CCIntervalAction move_ease_inout_back2 = move_ease_inout2.reverse();
        
            CCIntervalAction move_ease_inout3 = CCEaseInOut.action(move.copy(), 4.0f);
            CCIntervalAction move_ease_inout_back3 = move_ease_inout3.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
        
            CCIntervalAction seq1 = CCSequence.actions(move_ease_inout1, delay, move_ease_inout_back1, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_inout2, delay.copy(), move_ease_inout_back2, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_inout3, delay.copy(), move_ease_inout_back3, delay.copy());
        
            tamara.runAction(CCRepeatForever.action(seq1));
            kathia.runAction(CCRepeatForever.action(seq2));
            grossini.runAction(CCRepeatForever.action(seq3));
        }
        
        public String title() {
            return "EaseInOut and rates";
        }
    }


    static class SpriteEaseSine extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_in = CCEaseSineIn.action(move.copy());
            CCIntervalAction move_ease_in_back = move_ease_in.reverse();
        
            CCIntervalAction move_ease_out = CCEaseSineOut.action(move.copy());
            CCIntervalAction move_ease_out_back = move_ease_out.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
        
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_in, delay.copy(), move_ease_in_back, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_out, delay.copy(), move_ease_out_back, delay.copy());
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
            kathia.runAction(CCRepeatForever.action(seq3));
        }
        
        @Override
        public String title() {
            return "EaseSineIn - EaseSineOut";
        }
    }
    
    static class SpriteEaseSineInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease = CCEaseSineInOut.action(move.copy());
            CCIntervalAction move_ease_back = move_ease.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease, delay.copy(), move_ease_back, delay.copy());
        
            positionForTwo();
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
        }
        
        public String title()
        {
            return "EaseSineInOut action";
        }
    }

    static class SpriteEaseExponential extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_in = CCEaseExponentialIn.action(move.copy());
            CCIntervalAction move_ease_in_back = move_ease_in.reverse();
        
            CCIntervalAction move_ease_out = CCEaseExponentialOut.action(move.copy());
            CCIntervalAction move_ease_out_back = move_ease_out.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_in, delay.copy(), move_ease_in_back, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_out, delay.copy(), move_ease_out_back, delay.copy());
        
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
            kathia.runAction(CCRepeatForever.action(seq3));
        }
        public String title()
        {
            return "ExpIn - ExpOut actions";
        }
    }

    static class SpriteEaseExponentialInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease = CCEaseExponentialInOut.action(move.copy());
            CCIntervalAction move_ease_back = move_ease.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease, delay.copy(), move_ease_back, delay.copy());
        
            positionForTwo();
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
        }
        public String title()
        {
            return "EaseExponentialInOut action";
        }
    }

    static class SpriteEaseElasticInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
        
            CCIntervalAction move_ease_inout1 = CCEaseElasticInOut.action(move.copy(), 0.3f);
            CCIntervalAction move_ease_inout_back1 = move_ease_inout1.reverse();
        
            CCIntervalAction move_ease_inout2 = CCEaseElasticInOut.action(move.copy(), 0.45f);
            CCIntervalAction move_ease_inout_back2 = move_ease_inout2.reverse();
        
            CCIntervalAction move_ease_inout3 = CCEaseElasticInOut.action(move.copy(), 0.6f);
            CCIntervalAction move_ease_inout_back3 = move_ease_inout3.reverse();
        
            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move_ease_inout1, delay, move_ease_inout_back1, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_inout2, delay.copy(), move_ease_inout_back2, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_inout3, delay.copy(), move_ease_inout_back3, delay.copy());
        
            tamara.runAction(CCRepeatForever.action(seq1));
            kathia.runAction(CCRepeatForever.action(seq2));
            grossini.runAction(CCRepeatForever.action(seq3));
        }
        public String title()
        {
            return "EaseElasticInOut action";
        }
    }

    static class SpriteEaseElastic extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_in = CCEaseElasticIn.action(move.copy());
            CCIntervalAction move_ease_in_back = move_ease_in.reverse();
        
            CCIntervalAction move_ease_out = CCEaseElasticOut.action(move.copy());
            CCIntervalAction move_ease_out_back = move_ease_out.reverse();
            
            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_in, delay.copy(), move_ease_in_back, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_out, delay.copy(), move_ease_out_back, delay.copy());
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
            kathia.runAction(CCRepeatForever.action(seq3));
        }
        public String title()
        {
            return "Elastic In - Out actions";
        }
    }

    static class SpriteEaseBounce extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_in = CCEaseBounceIn.action(move.copy());
            CCIntervalAction move_ease_in_back = move_ease_in.reverse();
        
            CCIntervalAction move_ease_out = CCEaseBounceOut.action(move.copy());
            CCIntervalAction move_ease_out_back = move_ease_out.reverse();

            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay.copy(), move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_in, delay.copy(), move_ease_in_back, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_out, delay.copy(), move_ease_out_back, delay.copy());
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
            kathia.runAction(CCRepeatForever.action(seq3));
        }
        
        @Override
        public String title() {
            return "Bounce In - Out actions";
        }
    }

    static class SpriteEaseBounceInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease = CCEaseBounceInOut.action(move.copy());
            CCIntervalAction move_ease_back = move_ease.reverse();
            
            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease, delay.copy(), move_ease_back, delay.copy());
        
            positionForTwo();
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
        }
        public String title()
        {
            return "EaseBounceInOut action";
        }
    }

    static class SpriteEaseBack extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease_in = CCEaseBackIn.action(move.copy());
            CCIntervalAction move_ease_in_back = move_ease_in.reverse();
        
            CCIntervalAction move_ease_out = CCEaseBackOut.action(move.copy());
            CCIntervalAction move_ease_out_back = move_ease_out.reverse();

            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease_in, delay.copy(), move_ease_in_back, delay.copy());
            CCIntervalAction seq3 = CCSequence.actions(move_ease_out, delay.copy(), move_ease_out_back, delay.copy());
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
            kathia.runAction(CCRepeatForever.action(seq3));
        }
        public String title()
        {
            return "Back In - Out actions";
        }
    }
    
    static class SpriteEaseBackInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction move = CCMoveBy.action(3, CGPoint.make(s.width-130,0));
            CCIntervalAction move_back = move.reverse();
        
            CCIntervalAction move_ease = CCEaseBackInOut.action(move.copy());
            CCIntervalAction move_ease_back = move_ease.reverse();

            CCIntervalAction delay = CCDelayTime.action(0.25f);
            
            CCIntervalAction seq1 = CCSequence.actions(move, delay, move_back, delay.copy());
            CCIntervalAction seq2 = CCSequence.actions(move_ease, delay.copy(), move_ease_back, delay.copy());
        
            positionForTwo();
        
            grossini.runAction(CCRepeatForever.action(seq1));
            tamara.runAction(CCRepeatForever.action(seq2));
        }
        public String title()
        {
            return "EaseBackInOut action";
        }
    }


    static class SpeedTest extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            CGSize s = CCDirector.sharedDirector().winSize();
            // rotate and jump
            CCIntervalAction jump1 = CCJumpBy.action(4, CGPoint.make(-s.width+80,0), 100, 4);
            CCIntervalAction jump2 = jump1.reverse();
            CCIntervalAction rot1 = CCRotateBy.action(4, 360*2);
            CCIntervalAction rot2 = rot1.reverse();
        
            CCIntervalAction seq3_1 = CCSequence.actions(jump2, jump1);
            CCIntervalAction seq3_2 = CCSequence.actions(rot1, rot2);
            CCIntervalAction spawn = CCSpawn.actions(seq3_1, seq3_2);
            CCAction action = CCSpeed.action(spawn, 1.0f);
            action.setTag(kTagAction1);
        
            CCAction action2 = action.copy();
            CCAction action3 = action.copy();
        
            action2.setTag(kTagAction1);
            action3.setTag(kTagAction1);
        
            grossini.runAction(action2);
            tamara.runAction( action3);
            kathia.runAction(action);
        
        
            schedule("altertime", 1.0f);
        }
        
        public void altertime(float dt)
        {
            CCSpeed action1 = (CCSpeed)grossini.getAction(kTagAction1);
            CCSpeed action2 = (CCSpeed)tamara.getAction(kTagAction1);
            CCSpeed action3 = (CCSpeed)kathia.getAction(kTagAction1);
        
            action1.setSpeed(ccMacros.CCRANDOM_0_1() * 2);
            action2.setSpeed(ccMacros.CCRANDOM_0_1() * 2);
            action3.setSpeed(ccMacros.CCRANDOM_0_1() * 2);
        
        }
        
        @Override
        public String title() {
            return "Speed action";
        }
    }

//    static class SchedulerTest extends SpriteDemo {
//    	  UISlider	*sliderCtl;
//        public UISlider sliderCtl()
//        {
//            if (sliderCtl == nil)
//            {
//                CGRect frame = CGRectMake(174.0f, 12.0f, 120.0f, 7.0f);
//                sliderCtl = [[UISlider alloc] initWithFrame:frame];
//                [sliderCtl addTarget:self action:@selector(sliderAction:) forControlEvents:UIControlEventValueChanged];
//
//                // in case the parent view draws with a custom color or gradient, use a transparent color
//                sliderCtl.backgroundColor = [UIColor clearColor];
//
//                sliderCtl.minimumValue = 0.0f;
//                sliderCtl.maximumValue = 2.0f;
//                sliderCtl.continuous = YES;
//                sliderCtl.value = 1.0f;
//
//                sliderCtl.tag = kTagSlider;	// tag this view for later so we can remove it from recycled table cells
//            }
//            return [sliderCtl autorelease];
//        }
//
//        public void sliderAction()
//        {
//            [[Scheduler sharedScheduler] setTimeScale: sliderCtl.value];
//        }
//
//        public void onEnter()
//        {
//            super.onEnter();
//
//
//            // rotate and jump
//            IntervalAction jump1 = [JumpBy.action(4, -400,0) height:100 jumps:4];
//            IntervalAction jump2 = [jump1.reverse();
//            IntervalAction rot1 = [RotateBy.action(4 angle:360*2];
//            IntervalAction rot2 = [rot1.reverse();
//
//            IntervalAction seq3_1 = [Sequence actions:jump2, jump1);
//            IntervalAction seq3_2 = Sequence.actions(rot1, rot2);
//            IntervalAction spawn = [Spawn actions:seq3_1, seq3_2);
//            IntervalAction action = [RepeatForever.action(spawn];
//
//            IntervalAction action2 = [[action.copy(), ;
//            IntervalAction action3 = [[action.copy(), ;
//
//
//            grossini.runAction(Speed.action(action speed:0.5f));
//            [tamara.runAction(Speed.action(action2 speed:1.5f));
//            [kathia.runAction(Speed.action(action3 speed:1.0f));
//
//            ParticleSystem *emitter = [ParticleFireworks node];
//            addChild:emitter];
//
//            sliderCtl = sliderCtl];
//            [[[[Director sharedDirector] openGLView] window] addSubview: sliderCtl];
//        }
//
//        public void onExit()
//        {
//            [sliderCtl removeFromSuperview];
//            [super onExit];
//        }
//
//        public String title()
//        {
//            return "Scheduler scaleTime Test";
//        }
//    }
//
    /*
    @implementation SchedulerTest
    - (UISlider *)sliderCtl
    {
        if (sliderCtl == nil) 
        {
            CGRect frame = CGRectMake(174.0f, 12.0f, 120.0f, 7.0f);
            sliderCtl = [[UISlider alloc] initWithFrame:frame];
            [sliderCtl addTarget:self action:@selector(sliderAction:) forControlEvents:UIControlEventValueChanged];
            
            // in case the parent view draws with a custom color or gradient, use a transparent color
            sliderCtl.backgroundColor = [UIColor clearColor];
            
            sliderCtl.minimumValue = 0.0f;
            sliderCtl.maximumValue = 2.0f;
            sliderCtl.continuous = YES;
            sliderCtl.value = 1.0f;
    		
    		sliderCtl.tag = kTagSlider;	// tag this view for later so we can remove it from recycled table cells
        }
        return [sliderCtl autorelease];
    }

    -(void) sliderAction:(id) sender
    {
    	[[CCScheduler sharedScheduler] setTimeScale: sliderCtl.value];
    }

    -(void) onEnter
    {
    	[super onEnter];
    	
    	CGSize s = [[CCDirector sharedDirector] winSize];
    	
    	// rotate and jump
    	CCIntervalAction *jump1 = [CCJumpBy actionWithDuration:4 position:ccp(-s.width+80,0) height:100 jumps:4];
    	CCIntervalAction *jump2 = [jump1 reverse];
    	CCIntervalAction *rot1 = [CCRotateBy actionWithDuration:4 angle:360*2];
    	CCIntervalAction *rot2 = [rot1 reverse];
    	
    	id seq3_1 = [CCSequence actions:jump2, jump1, nil];
    	id seq3_2 = [CCSequence actions: rot1, rot2, nil];
    	id spawn = [CCSpawn actions:seq3_1, seq3_2, nil];
    	id action = [CCRepeatForever actionWithAction:spawn];
    	
    	id action2 = [[action copy] autorelease];
    	id action3 = [[action copy] autorelease];
    	
    	
    	[grossini runAction: [CCSpeed actionWithAction:action speed:0.5f]];
    	[tamara runAction: [CCSpeed actionWithAction:action2 speed:1.5f]];
    	[kathia runAction: [CCSpeed actionWithAction:action3 speed:1.0f]];
    	
    	CCParticleSystem *emitter = [CCParticleFireworks node];
    	[self addChild:emitter];
    	
    	sliderCtl = [self sliderCtl];
    	[[[[CCDirector sharedDirector] openGLView] window] addSubview: sliderCtl];
    }

    -(void) onExit
    {
    	[sliderCtl removeFromSuperview];
    	[super onExit];
    }

    -(NSString *) title
    {
    	return @"Scheduler scaleTime Test";
    }
    @end
    */

}
