package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.interval.*;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.*;
import org.cocos2d.events.CCTouchDispatcher;

public class AtlasSpriteTest extends Activity {
    // private static final String LOG_TAG = AtlasSpriteTest.class.getSimpleName();

    private static final boolean DEBUG = true;

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

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Atlas1.class,
            Atlas2.class,
            Atlas3.class,
            Atlas4.class,
            Atlas5.class,
            Atlas6.class,
    };

    static Layer nextAction() {
        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;
        return restartAction();
    }

    static Layer backAction() {
        sceneIdx--;
        if (sceneIdx < 0)
            sceneIdx += transitions.length;
        return restartAction();
    }

    static Layer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (Layer) c.newInstance();
        } catch (Exception e) {
            if (DEBUG) e.printStackTrace();
            return null;
        }
    }

    static abstract class AtlasSpriteDemo extends Layer {

        public static final int kTagTileMap = 1;
        public static final int kTagSpriteManager = 1;
        public static final int kTagAnimation1 = 1;

        public static final int kTagSprite1 = 1;
        public static final int kTagSprite2 = 2;
        public static final int kTagSprite3 = 3;
        public static final int kTagSprite4 = 4;
        public static final int kTagSprite5 = 5;
        public static final int kTagSprite6 = 6;
        public static final int kTagSprite7 = 7;
        public static final int kTagSprite8 = 8;

        public AtlasSpriteDemo() {
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

        public abstract String title();
    }

    static class Atlas1 extends AtlasSpriteDemo {

        public Atlas1() {

            isTouchEnabled_ = true;

            float x, y;

            CCSize s = Director.sharedDirector().winSize();
            x = s.width;
            y = s.height;

            AtlasSpriteManager mgr = new AtlasSpriteManager("grossini_dance_atlas.png", 50);
            addChild(mgr, 0, kTagSpriteManager);

            addNewSprite(CCPoint.ccp(x / 2, y / 2));

        }

        private void addNewSprite(CCPoint pos) {
            AtlasSpriteManager mgr = (AtlasSpriteManager) getChild(kTagSpriteManager);

            float rnd = ccMacros.CCRANDOM_0_1() * 1400.0f / 100.0f;
            int idx = (int) rnd;
            int x = (idx % 5) * 85;
            int y = (idx / 5) * 121;

            AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(x, y, 85, 121), mgr);
            mgr.addChild(sprite);

            sprite.setPosition(pos.x, pos.y);

            IntervalAction action;
            float r = ccMacros.CCRANDOM_0_1();

            if (r < 0.33)
                action = ScaleBy.action(3, 2);
            else if (r < 0.66)
                action = RotateBy.action(3, 360);
            else
                action = Blink.action(1, 3);
            IntervalAction action_back = action.reverse();
            IntervalAction seq = Sequence.actions(action, action_back);

            sprite.runAction(RepeatForever.action(seq));
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CCPoint location = Director.sharedDirector().convertCoordinate(event.getX(), event.getY());

            addNewSprite(location);

            return CCTouchDispatcher.kEventHandled;
        }

        @Override
        public String title() {
            return "AtlasSprite (Tap Screen)";
        }
    }

    static class Atlas2 extends AtlasSpriteDemo {

        public Atlas2() {

            CCTexture2D.saveTexParameters();
            CCTexture2D.setAliasTexParameters();

            AtlasSpriteManager mgr = new AtlasSpriteManager("grossini_dance_atlas.png", 50);
            addChild(mgr, 0, kTagSpriteManager);

            CCTexture2D.restoreTexParameters();

            AtlasSprite sprite1 = AtlasSprite.sprite(CCRect.make(0, 0, 85, 121), mgr);
            AtlasSprite sprite2 = AtlasSprite.sprite(CCRect.make(0, 0, 85, 121), mgr);
            AtlasSprite sprite3 = AtlasSprite.sprite(CCRect.make(0, 0, 85, 121), mgr);

            AtlasAnimation animation = new AtlasAnimation("dance", 0.2f);
            for (int i = 0; i < 14; i++) {
                int x = i % 5;
                int y = i / 5;
                animation.addFrame(CCRect.make(x * 85, y * 121, 85, 121));
            }

            mgr.addChild(sprite1);
            mgr.addChild(sprite2);
            mgr.addChild(sprite3);

            CCSize s = Director.sharedDirector().winSize();
            sprite1.setPosition(s.width / 2, s.height / 2);
            sprite2.setPosition(s.width / 2 - 100, s.height / 2);
            sprite3.setPosition(s.width / 2 + 100, s.height / 2);

            IntervalAction action1 = Animate.action(animation);
            IntervalAction action2 = action1.copy();
            IntervalAction action3 = action1.copy();

            sprite1.scale(0.5f);
            sprite2.scale(1.0f);
            sprite3.scale(1.5f);

            sprite1.runAction(action1);
            sprite2.runAction(action2);
            sprite3.runAction(action3);
        }

        @Override
        public String title() {
            return "AtlasSprite Animation";
        }
    }

    static class Atlas3 extends AtlasSpriteDemo {

        public Atlas3() {

            // small capacity. Testing resizing.
            // Don't use capacity=1 in your real game. It is expensive to resize the capacity
            AtlasSpriteManager mgr = new AtlasSpriteManager("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            AtlasSprite sprite1 = AtlasSprite.sprite(CCRect.make(85 * 0, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite2 = AtlasSprite.sprite(CCRect.make(85 * 1, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite3 = AtlasSprite.sprite(CCRect.make(85 * 2, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite4 = AtlasSprite.sprite(CCRect.make(85 * 3, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite5 = AtlasSprite.sprite(CCRect.make(85 * 0, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite6 = AtlasSprite.sprite(CCRect.make(85 * 1, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite7 = AtlasSprite.sprite(CCRect.make(85 * 2, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite8 = AtlasSprite.sprite(CCRect.make(85 * 3, 121 * 1, 85, 121), mgr);

            CCSize s = Director.sharedDirector().winSize();
            sprite1.setPosition((s.width / 5) * 1, (s.height / 3) * 1);
            sprite2.setPosition((s.width / 5) * 2, (s.height / 3) * 1);
            sprite3.setPosition((s.width / 5) * 3, (s.height / 3) * 1);
            sprite4.setPosition((s.width / 5) * 4, (s.height / 3) * 1);
            sprite5.setPosition((s.width / 5) * 1, (s.height / 3) * 2);
            sprite6.setPosition((s.width / 5) * 2, (s.height / 3) * 2);
            sprite7.setPosition((s.width / 5) * 3, (s.height / 3) * 2);
            sprite8.setPosition((s.width / 5) * 4, (s.height / 3) * 2);

            IntervalAction action = FadeIn.action(2);
            IntervalAction action_back = action.reverse();
            Action fade = RepeatForever.action(Sequence.actions(action, action_back));

            IntervalAction tintred = TintBy.action(2, 0, -255, -255);
            IntervalAction tintred_back = tintred.reverse();
            Action red = RepeatForever.action(Sequence.actions(tintred, tintred_back));

            IntervalAction tintgreen = TintBy.action(2,  -255 , 0,  -255);
            IntervalAction tintgreen_back = tintgreen.reverse();
            Action green = RepeatForever.action(Sequence.actions(tintgreen, tintgreen_back));

            IntervalAction tintblue = TintBy.action(2, -255,  -255, 0);
            IntervalAction tintblue_back = tintblue.reverse();
            Action blue = RepeatForever.action(Sequence.actions(tintblue, tintblue_back));


            sprite5.runAction(red);
            sprite6.runAction(green);
            sprite7.runAction(blue);
            sprite8.runAction(fade);

            // late add: test dirtyColor and dirtyPosition
            mgr.addChild(sprite1, 0, kTagSprite1);
            mgr.addChild(sprite2, 0, kTagSprite2);
            mgr.addChild(sprite3, 0, kTagSprite3);
            mgr.addChild(sprite4, 0, kTagSprite4);
            mgr.addChild(sprite5, 0, kTagSprite5);
            mgr.addChild(sprite6, 0, kTagSprite6);
            mgr.addChild(sprite7, 0, kTagSprite7);
            mgr.addChild(sprite8, 0, kTagSprite8);

            schedule("removeAndAddSprite", 2);
        }

        // this function test if remove and add works as expected:
        //  color array and vertex array should be reindexed
        public void removeAndAddSprite(float dt) {
            CocosNode mgr = getChild(kTagSpriteManager);
            CocosNode sprite = mgr.getChild(kTagSprite5);

            mgr.removeChild(sprite, false);
            mgr.addChild(sprite, 0, kTagSprite5);
        }

        @Override
        public String title() {
            return "AtlasSprite: Color & Opacity";
        }
    }

    static class Atlas4 extends AtlasSpriteDemo {
        int dir;

        public Atlas4() {
            dir = 1;

            // small capacity. Testing resizing.
            // Don't use capacity=1 in your real game. It is expensive to resize the capacity
            AtlasSpriteManager mgr = new AtlasSpriteManager("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            CCSize s = Director.sharedDirector().winSize();

            for (int i = 0; i < 5; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(85 * 0, 121 * 1, 85, 121), mgr);
                sprite.setPosition(50 + i * 40, s.height / 2);
                mgr.addChild(sprite, i);
            }

            for (int i = 5; i < 10; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(85 * 1, 121 * 0, 85, 121), mgr);
                sprite.setPosition(50 + i * 40, s.height / 2);
                mgr.addChild(sprite, 14 - i);
            }

            AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(85 * 3, 121 * 0, 85, 121), mgr);
            mgr.addChild(sprite, -1, kTagSprite1);
            sprite.setPosition(s.width / 2, s.height / 2 - 20);
            sprite.setScaleX(6);
            sprite.setColor(new ccColor3B(255, 0, 0));

            schedule("reorderSprite", 1);
        }

        public void reorderSprite(float dt) {
            CocosNode mgr = getChild(kTagSpriteManager);
            CocosNode sprite = mgr.getChild(kTagSprite1);

            int z = sprite.getZOrder();

            if (z < -1)
                dir = 1;
            if (z > 10)
                dir = -1;

            z += dir * 3;

            mgr.reorderChild(sprite, z);

        }

        @Override
        public String title() {
            return "AtlasSprite: Z Order";
        }
    }

    static class Atlas5 extends AtlasSpriteDemo {

        public Atlas5() {
            // small capacity. Testing resizing.
            // Don't use capacity=1 in your real game. It is expensive to resize the capacity
            AtlasSpriteManager mgr = new AtlasSpriteManager("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            CCSize s = Director.sharedDirector().winSize();

            IntervalAction rotate = RotateBy.action(10, 360);
            Action action = RepeatForever.action(rotate);

            for (int i = 0; i < 3; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(85 * i, 121 * 1, 85, 121), mgr);
                sprite.setPosition(60 + i * 100, s.height / 2);


                Sprite point = Sprite.sprite("r1.png");
                point.scale(0.25f);
                point.setPosition(sprite.getPositionX(), sprite.getPositionY());
                addChild(point, 1);

                switch (i) {
                    case 0:
                        sprite.setAnchorPoint(0, 0);
                        break;
                    case 1:
                        sprite.setAnchorPoint(0.5f, 0.5f);
                        break;
                    case 2:
                        sprite.setAnchorPoint(1, 1);
                        break;
                }
                point.setPosition(sprite.getPositionX(), sprite.getPositionY());

                Action copy = action.copy();
                sprite.runAction(copy);
                mgr.addChild(sprite, i);
            }
        }

        @Override
        public String title() {
            return "AtlasSprite: Anchor Point";
        }
    }


    static class Atlas6 extends AtlasSpriteDemo {

        public Atlas6() {
            // small capacity. Testing resizing
            // Don't use capacity=1 in your real game. It is expensive to resize the capacity
            AtlasSpriteManager mgr = new AtlasSpriteManager("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            CCSize s = Director.sharedDirector().winSize();

            mgr.setRelativeAnchorPoint(false);
            mgr.setAnchorPoint(0.5f, 0.5f);


            // AtlasSprite actions
            IntervalAction rotate = RotateBy.action(5, 360);
            Action action = RepeatForever.action(rotate);

            // AtlasSpriteManager actions
            IntervalAction rotate_back = rotate.reverse();
            IntervalAction rotate_seq = Sequence.actions(rotate, rotate_back);
            Action rotate_forever = RepeatForever.action(rotate_seq);

            IntervalAction scale = ScaleBy.action(5, 1.5f);
            IntervalAction scale_back = scale.reverse();
            IntervalAction scale_seq = Sequence.actions(scale, scale_back);
            Action scale_forever = RepeatForever.action(scale_seq);


            for (int i = 0; i < 3; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(85 * i, 121 * 1, 85, 121), mgr);
                sprite.setPosition(90 + i * 150, s.height / 2);

                sprite.runAction(action.copy());
                mgr.addChild(sprite, i);
            }

            mgr.runAction(scale_forever);
            mgr.runAction(rotate_forever);
        }

        @Override
        public String title() {
            return "AtlasSpriteManager Transformation";
        }
    }
}
