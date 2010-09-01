package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.*;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
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

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Atlas1.class,
            Atlas2.class,
            Atlas3.class,
            Atlas4.class,
            Atlas5.class,
            Atlas6.class,
    };

    static CCLayer nextAction() {
        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;
        return restartAction();
    }

    static CCLayer backAction() {
        sceneIdx--;
        if (sceneIdx < 0)
            sceneIdx += transitions.length;
        return restartAction();
    }

    static CCLayer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (CCLayer) c.newInstance();
        } catch (Exception e) {
            if (DEBUG) e.printStackTrace();
            return null;
        }
    }

    static abstract class AtlasSpriteDemo extends CCLayer {

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
            addChild(menu, 1);
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

    static class Atlas1 extends AtlasSpriteDemo {

        public Atlas1() {

            isTouchEnabled_ = true;

            float x, y;

            CGSize s = CCDirector.sharedDirector().winSize();
            x = s.width;
            y = s.height;

            CCSpriteFrameCache mgr = new CCSpriteFrameCache("grossini_dance_atlas.png", 50);
            addChild(mgr, 0, kTagSpriteManager);

            addNewSprite(CGPoint.ccp(x / 2, y / 2));

        }

        private void addNewSprite(CGPoint pos) {
            CCSpriteFrameCache mgr = (CCSpriteFrameCache) getChild(kTagSpriteManager);

            float rnd = ccMacros.CCRANDOM_0_1() * 1400.0f / 100.0f;
            int idx = (int) rnd;
            int x = (idx % 5) * 85;
            int y = (idx / 5) * 121;

            AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(x, y, 85, 121), mgr);
            mgr.addChild(sprite);

            sprite.setPosition(pos);

            CCIntervalAction action;
            float r = ccMacros.CCRANDOM_0_1();

            if (r < 0.33)
                action = CCScaleBy.action(3, 2);
            else if (r < 0.66)
                action = CCRotateBy.action(3, 360);
            else
                action = CCBlink.action(1, 3);
            CCIntervalAction action_back = action.reverse();
            CCIntervalAction seq = CCSequence.actions(action, action_back);

            sprite.runAction(CCRepeatForever.action(seq));
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint location = CCDirector.sharedDirector().convertCoordinate(event.getX(), event.getY());

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

            CCSpriteFrameCache mgr = new CCSpriteFrameCache("grossini_dance_atlas.png", 50);
            addChild(mgr, 0, kTagSpriteManager);

            CCTexture2D.restoreTexParameters();

            AtlasSprite sprite1 = AtlasSprite.sprite(CGRect.make(0, 0, 85, 121), mgr);
            AtlasSprite sprite2 = AtlasSprite.sprite(CGRect.make(0, 0, 85, 121), mgr);
            AtlasSprite sprite3 = AtlasSprite.sprite(CGRect.make(0, 0, 85, 121), mgr);

            AtlasAnimation animation = new AtlasAnimation("dance", 0.2f);
            for (int i = 0; i < 14; i++) {
                int x = i % 5;
                int y = i / 5;
                animation.addFrame(CGRect.make(x * 85, y * 121, 85, 121));
            }

            mgr.addChild(sprite1);
            mgr.addChild(sprite2);
            mgr.addChild(sprite3);

            CGSize s = CCDirector.sharedDirector().winSize();
            sprite1.setPosition(CGPoint.make(s.width / 2, s.height / 2));
            sprite2.setPosition(CGPoint.make(s.width / 2 - 100, s.height / 2));
            sprite3.setPosition(CGPoint.make(s.width / 2 + 100, s.height / 2));

            CCIntervalAction action1 = CCAnimate.action(animation);
            CCIntervalAction action2 = action1.copy();
            CCIntervalAction action3 = action1.copy();

            sprite1.setScale(0.5f);
            sprite2.setScale(1.0f);
            sprite3.setScale(1.5f);

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
            CCSpriteFrameCache mgr = new CCSpriteFrameCache("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            AtlasSprite sprite1 = AtlasSprite.sprite(CGRect.make(85 * 0, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite2 = AtlasSprite.sprite(CGRect.make(85 * 1, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite3 = AtlasSprite.sprite(CGRect.make(85 * 2, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite4 = AtlasSprite.sprite(CGRect.make(85 * 3, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite5 = AtlasSprite.sprite(CGRect.make(85 * 0, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite6 = AtlasSprite.sprite(CGRect.make(85 * 1, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite7 = AtlasSprite.sprite(CGRect.make(85 * 2, 121 * 1, 85, 121), mgr);
            AtlasSprite sprite8 = AtlasSprite.sprite(CGRect.make(85 * 3, 121 * 1, 85, 121), mgr);

            CGSize s = CCDirector.sharedDirector().winSize();
            sprite1.setPosition(CGPoint.make((s.width / 5) * 1, (s.height / 3) * 1));
            sprite2.setPosition(CGPoint.make((s.width / 5) * 2, (s.height / 3) * 1));
            sprite3.setPosition(CGPoint.make((s.width / 5) * 3, (s.height / 3) * 1));
            sprite4.setPosition(CGPoint.make((s.width / 5) * 4, (s.height / 3) * 1));
            sprite5.setPosition(CGPoint.make((s.width / 5) * 1, (s.height / 3) * 2));
            sprite6.setPosition(CGPoint.make((s.width / 5) * 2, (s.height / 3) * 2));
            sprite7.setPosition(CGPoint.make((s.width / 5) * 3, (s.height / 3) * 2));
            sprite8.setPosition(CGPoint.make((s.width / 5) * 4, (s.height / 3) * 2));

            CCIntervalAction action = CCFadeIn.action(2);
            CCIntervalAction action_back = action.reverse();
            CCAction fade = CCRepeatForever.action(CCSequence.actions(action, action_back));

            CCIntervalAction tintred = CCTintBy.action(2, ccColor3B.ccc3(0, -255, -255));
            CCIntervalAction tintred_back = tintred.reverse();
            CCAction red = CCRepeatForever.action(CCSequence.actions(tintred, tintred_back));

            CCIntervalAction tintgreen = CCTintBy.action(2, ccColor3B.ccc3(-255 , 0,  -255));
            CCIntervalAction tintgreen_back = tintgreen.reverse();
            CCAction green = CCRepeatForever.action(CCSequence.actions(tintgreen, tintgreen_back));

            CCIntervalAction tintblue = CCTintBy.action(2, ccColor3B.ccc3(-255,  -255, 0));
            CCIntervalAction tintblue_back = tintblue.reverse();
            CCAction blue = CCRepeatForever.action(CCSequence.actions(tintblue, tintblue_back));


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
            CCNode mgr = getChild(kTagSpriteManager);
            CCNode sprite = mgr.getChild(kTagSprite5);

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
            CCSpriteFrameCache mgr = new CCSpriteFrameCache("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            CGSize s = CCDirector.sharedDirector().winSize();

            for (int i = 0; i < 5; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(85 * 0, 121 * 1, 85, 121), mgr);
                sprite.setPosition(CGPoint.make(50 + i * 40, s.height / 2));
                mgr.addChild(sprite, i);
            }

            for (int i = 5; i < 10; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(85 * 1, 121 * 0, 85, 121), mgr);
                sprite.setPosition(CGPoint.make(50 + i * 40, s.height / 2));
                mgr.addChild(sprite, 14 - i);
            }

            AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(85 * 3, 121 * 0, 85, 121), mgr);
            mgr.addChild(sprite, -1, kTagSprite1);
            sprite.setPosition(CGPoint.make(s.width / 2, s.height / 2 - 20));
            sprite.setScaleX(6);
            sprite.setColor(new ccColor3B(255, 0, 0));

            schedule("reorderSprite", 1);
        }

        public void reorderSprite(float dt) {
            CCNode mgr = getChild(kTagSpriteManager);
            CCNode sprite = mgr.getChild(kTagSprite1);

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
            CCSpriteFrameCache mgr = new CCSpriteFrameCache("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            CGSize s = CCDirector.sharedDirector().winSize();

            CCIntervalAction rotate = CCRotateBy.action(10, 360);
            CCAction action = CCRepeatForever.action(rotate);

            for (int i = 0; i < 3; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(85 * i, 121 * 1, 85, 121), mgr);
                sprite.setPosition(CGPoint.make(60 + i * 100, s.height / 2));


                CCSprite point = CCSprite.sprite("r1.png");
                point.setScale(0.25f);
                point.setPosition(sprite.getPosition());
                addChild(point, 1);

                switch (i) {
                    case 0:
                        sprite.setAnchorPoint(CGPoint.zero());
                        break;
                    case 1:
                        sprite.setAnchorPoint(CGPoint.make(0.5f, 0.5f));
                        break;
                    case 2:
                        sprite.setAnchorPoint(CGPoint.make(1, 1));
                        break;
                }
                point.setPosition(sprite.getPosition());

                CCAction copy = action.copy();
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
            CCSpriteFrameCache mgr = new CCSpriteFrameCache("grossini_dance_atlas.png", 1);
            addChild(mgr, 0, kTagSpriteManager);

            CGSize s = CCDirector.sharedDirector().winSize();

            mgr.setRelativeAnchorPoint(false);
            mgr.setAnchorPoint(CGPoint.make(0.5f, 0.5f));


            // AtlasSprite actions
            CCIntervalAction rotate = CCRotateBy.action(5, 360);
            CCAction action = CCRepeatForever.action(rotate);

            // AtlasSpriteManager actions
            CCIntervalAction rotate_back = rotate.reverse();
            CCIntervalAction rotate_seq = CCSequence.actions(rotate, rotate_back);
            CCAction rotate_forever = CCRepeatForever.action(rotate_seq);

            CCIntervalAction scale = CCScaleBy.action(5, 1.5f);
            CCIntervalAction scale_back = scale.reverse();
            CCIntervalAction scale_seq = CCSequence.actions(scale, scale_back);
            CCAction scale_forever = CCRepeatForever.action(scale_seq);


            for (int i = 0; i < 3; i++) {
                AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(85 * i, 121 * 1, 85, 121), mgr);
                sprite.setPosition(CGPoint.make(90 + i * 150, s.height / 2));

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
