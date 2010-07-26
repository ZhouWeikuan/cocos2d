package org.cocos2d.tests;

import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.base.Speed;
import org.cocos2d.actions.ease.EaseBackIn;
import org.cocos2d.actions.ease.EaseBackInOut;
import org.cocos2d.actions.ease.EaseBackOut;
import org.cocos2d.actions.ease.EaseBounceIn;
import org.cocos2d.actions.ease.EaseBounceInOut;
import org.cocos2d.actions.ease.EaseBounceOut;
import org.cocos2d.actions.ease.EaseElasticIn;
import org.cocos2d.actions.ease.EaseElasticInOut;
import org.cocos2d.actions.ease.EaseElasticOut;
import org.cocos2d.actions.ease.EaseExponentialIn;
import org.cocos2d.actions.ease.EaseExponentialInOut;
import org.cocos2d.actions.ease.EaseExponentialOut;
import org.cocos2d.actions.ease.EaseIn;
import org.cocos2d.actions.ease.EaseInOut;
import org.cocos2d.actions.ease.EaseOut;
import org.cocos2d.actions.ease.EaseSineIn;
import org.cocos2d.actions.ease.EaseSineInOut;
import org.cocos2d.actions.ease.EaseSineOut;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.JumpBy;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.RotateBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.actions.interval.Spawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.nodes.TextureManager;
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

        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);
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

        // attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        CCDirector.sharedDirector().setLandscape(false);

        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = Scene.node();
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

        TextureManager.sharedTextureManager().removeAllTextures();
    }

    static final int kTagAction1 = 1;
    static final int kTagAction2 = 2;
    static final int kTagSlider = 3;

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
        Sprite kathia;
         
        public SpriteDemo() {

            // Example:
            // You can create a sprite using a CCTexture2D
            CCTexture2D tex = TextureManager.createTextureFromFilePath("grossini.png");
            grossini = Sprite.sprite(tex);

            // Example:
            // Or you can create an sprite using a filename. PNG and BMP files are supported. Probably TIFF too
            tamara = Sprite.sprite("grossinis_sister1.png");
            kathia = Sprite.sprite("grossinis_sister2.png");

            addChild(grossini, 3);
            addChild(kathia, 2);
            addChild(tamara, 1);


            CGSize s = CCDirector.sharedDirector().winSize();

            grossini.setPosition(CGPoint.make(60, 150));
            kathia.setPosition(CGPoint.make(60, 250));
            tamara.setPosition(CGPoint.make(60, 350));

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
            addChild(label, 1);
            label.setPosition(CGPoint.make(s.width / 2, s.height - 30));

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
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void nextCallback() {
            Scene s = Scene.node();
            s.addChild(nextAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void backCallback() {
            Scene s = Scene.node();
            s.addChild(backAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void positionForTwo()
        {
            grossini.setPosition(CGPoint.make( 60, 120 ));
            tamara.setPosition(CGPoint.make( 60, 220));
            kathia.setVisible(false);
        }
        
        public abstract String title();
    }


    static class SpriteEase extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_in = EaseIn.action(move.copy(), 3.0f);
            IntervalAction move_ease_in_back = move_ease_in.reverse();
        
            IntervalAction move_ease_out = EaseOut.action(move.copy(), 3.0f);
            IntervalAction move_ease_out_back = move_ease_out.reverse();
        
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease_in, move_ease_in_back);
            IntervalAction seq3 = Sequence.actions(move_ease_out, move_ease_out_back);
        
        
            Action a2 = grossini.runAction(RepeatForever.action(seq1));
            a2.setTag(1);
        
            Action a1 = tamara.runAction(RepeatForever.action(seq2));
            a1.setTag(1);
        
            Action a = kathia.runAction(RepeatForever.action(seq3));
            a.setTag(1);
            
            schedule("testStopAction", 6);
        }
        
        public void testStopAction(float dt)
        {
            unschedule("testStopAction");
            tamara.stopAction(1);
            kathia.stopAction(1);
            grossini.stopAction(1);
        }
        
        public String title()
        {
            return "EaseIn - EaseOut - Stop";
        }
    }

    static class SpriteEaseInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            IntervalAction move = MoveBy.action(3, 250,0);
        //	IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_inout1 = EaseInOut.action(move.copy(), 2.0f);
            IntervalAction move_ease_inout_back1 = move_ease_inout1.reverse();
        
            IntervalAction move_ease_inout2 = EaseInOut.action(move.copy(), 3.0f);
            IntervalAction move_ease_inout_back2 = move_ease_inout2.reverse();
        
            IntervalAction move_ease_inout3 = EaseInOut.action(move.copy(), 4.0f);
            IntervalAction move_ease_inout_back3 = move_ease_inout3.reverse();
        
        
            IntervalAction seq1 = Sequence.actions(move_ease_inout1, move_ease_inout_back1);
            IntervalAction seq2 = Sequence.actions(move_ease_inout2, move_ease_inout_back2);
            IntervalAction seq3 = Sequence.actions(move_ease_inout3, move_ease_inout_back3);
        
            tamara.runAction(RepeatForever.action(seq1));
            kathia.runAction(RepeatForever.action(seq2));
            grossini.runAction(RepeatForever.action(seq3));
        }
        
        public String title()
        {
            return "EaseInOut and rates";
        }
    }


    static class SpriteEaseSine extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_in = EaseSineIn.action(move.copy());
            IntervalAction move_ease_in_back = move_ease_in.reverse();
        
            IntervalAction move_ease_out = EaseSineOut.action(move.copy());
            IntervalAction move_ease_out_back = move_ease_out.reverse();
        
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease_in, move_ease_in_back);
            IntervalAction seq3 = Sequence.actions(move_ease_out, move_ease_out_back);
        
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
            kathia.runAction(RepeatForever.action(seq3));
        }
        public String title()
        {
            return "EaseSineIn - EaseSineOut";
        }
    }
    
    static class SpriteEaseSineInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease = EaseSineInOut.action(move.copy());
            IntervalAction move_ease_back = move_ease.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease, move_ease_back);
        
            positionForTwo();
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_in = EaseExponentialIn.action(move.copy());
            IntervalAction move_ease_in_back = move_ease_in.reverse();
        
            IntervalAction move_ease_out = EaseExponentialOut.action(move.copy());
            IntervalAction move_ease_out_back = move_ease_out.reverse();
        
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease_in, move_ease_in_back);
            IntervalAction seq3 = Sequence.actions(move_ease_out, move_ease_out_back);
        
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
            kathia.runAction(RepeatForever.action(seq3));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease = EaseExponentialInOut.action(move.copy());
            IntervalAction move_ease_back = move_ease.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease, move_ease_back);
        
            positionForTwo();
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
        
            IntervalAction move_ease_inout1 = EaseElasticInOut.action(move.copy(), 0.3f);
            IntervalAction move_ease_inout_back1 = move_ease_inout1.reverse();
        
            IntervalAction move_ease_inout2 = EaseElasticInOut.action(move.copy(), 0.45f);
            IntervalAction move_ease_inout_back2 = move_ease_inout2.reverse();
        
            IntervalAction move_ease_inout3 = EaseElasticInOut.action(move.copy(), 0.6f);
            IntervalAction move_ease_inout_back3 = move_ease_inout3.reverse();
        
        
            IntervalAction seq1 = Sequence.actions(move_ease_inout1, move_ease_inout_back1);
            IntervalAction seq2 = Sequence.actions(move_ease_inout2, move_ease_inout_back2);
            IntervalAction seq3 = Sequence.actions(move_ease_inout3, move_ease_inout_back3);
        
            tamara.runAction(RepeatForever.action(seq1));
            kathia.runAction(RepeatForever.action(seq2));
            grossini.runAction(RepeatForever.action(seq3));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_in = EaseElasticIn.action(move.copy());
            IntervalAction move_ease_in_back = move_ease_in.reverse();
        
            IntervalAction move_ease_out = EaseElasticOut.action(move.copy());
            IntervalAction move_ease_out_back = move_ease_out.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease_in, move_ease_in_back);
            IntervalAction seq3 = Sequence.actions(move_ease_out, move_ease_out_back);
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
            kathia.runAction(RepeatForever.action(seq3));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_in = EaseBounceIn.action(move.copy());
            IntervalAction move_ease_in_back = move_ease_in.reverse();
        
            IntervalAction move_ease_out = EaseBounceOut.action(move.copy());
            IntervalAction move_ease_out_back = move_ease_out.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease_in, move_ease_in_back);
            IntervalAction seq3 = Sequence.actions(move_ease_out, move_ease_out_back);
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
            kathia.runAction(RepeatForever.action(seq3));
        }
        public String title()
        {
            return "Bounce In - Out actions";
        }
    }

    static class SpriteEaseBounceInOut extends SpriteDemo {
        public void onEnter()
        {
            super.onEnter();
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease = EaseBounceInOut.action(move.copy());
            IntervalAction move_ease_back = move_ease.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease, move_ease_back);
        
            positionForTwo();
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease_in = EaseBackIn.action(move.copy());
            IntervalAction move_ease_in_back = move_ease_in.reverse();
        
            IntervalAction move_ease_out = EaseBackOut.action(move.copy());
            IntervalAction move_ease_out_back = move_ease_out.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease_in, move_ease_in_back);
            IntervalAction seq3 = Sequence.actions(move_ease_out, move_ease_out_back);
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
            kathia.runAction(RepeatForever.action(seq3));
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
        
            IntervalAction move = MoveBy.action(3, 250,0);
            IntervalAction move_back = move.reverse();
        
            IntervalAction move_ease = EaseBackInOut.action(move.copy());
            IntervalAction move_ease_back = move_ease.reverse();
        
            IntervalAction seq1 = Sequence.actions(move, move_back);
            IntervalAction seq2 = Sequence.actions(move_ease, move_ease_back);
        
            positionForTwo();
        
            grossini.runAction(RepeatForever.action(seq1));
            tamara.runAction(RepeatForever.action(seq2));
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
        
        
            // rotate and jump
            IntervalAction jump1 = JumpBy.action(4, -400,0, 100, 4);
            IntervalAction jump2 = jump1.reverse();
            IntervalAction rot1 = RotateBy.action(4, 360*2);
            IntervalAction rot2 = rot1.reverse();
        
            IntervalAction seq3_1 = Sequence.actions(jump2, jump1);
            IntervalAction seq3_2 = Sequence.actions(rot1, rot2);
            IntervalAction spawn = Spawn.actions(seq3_1, seq3_2);
            Action action = Speed.action(RepeatForever.action(spawn), 1.0f);
            action.setTag(kTagAction1);
        
            Action action2 = action.copy();
            Action action3 = action.copy();
        
            action2.setTag(kTagAction1);
            action3.setTag(kTagAction1);
        
            grossini.runAction(action2);
            tamara.runAction( action3);
            kathia.runAction(action);
        
        
            schedule("altertime", 1.0f);
        }
        
        public void altertime(float dt)
        {
            Speed action1 = (Speed)grossini.getAction(kTagAction1);
            Speed action2 = (Speed)tamara.getAction(kTagAction1);
            Speed action3 = (Speed)kathia.getAction(kTagAction1);
        
            action1.setSpeed(ccMacros.CCRANDOM_0_1() * 2);
            action2.setSpeed(ccMacros.CCRANDOM_0_1() * 2);
            action3.setSpeed(ccMacros.CCRANDOM_0_1() * 2);
        
        }
        
        public String title()
        {
            return "Speed action";
        }
    }

//    static class SchedulerTest extends SpriteDemo {
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

}
