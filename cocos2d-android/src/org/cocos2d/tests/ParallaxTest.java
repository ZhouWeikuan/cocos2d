package org.cocos2d.tests;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCParallaxNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTileMapAtlas;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class ParallaxTest extends Activity {
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

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Parallax1.class,
            Parallax2.class,
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

    static abstract class ParallaxDemo extends CCLayer {
        CCTextureAtlas atlas;

        static final int kTagNode = 1;
        static final int kTagGrossini = 2;

        public ParallaxDemo() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 18);
            label.setPosition(CGPoint.make(s.width / 2, s.height - 30));
            addChild(label, 1);

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

        public abstract String title();
    }

    static class Parallax1 extends ParallaxDemo {

        public Parallax1() {

            // Top Layer, a simple image
            CCSprite cocosImage = CCSprite.sprite("powered.png");
            // scale the image (optional)
            cocosImage.setScale(2.5f);
            // change the transform anchor point to 0,0 (optional)
            cocosImage.setAnchorPoint(CGPoint.make(0,0));


            // Middle layer: a Tile map atlas
            CCTileMapAtlas tilemap = CCTileMapAtlas.tilemap("tiles.png", "levelmap.tga", 16, 16);

            // change the transform anchor to 0,0 (optional)
            tilemap.setAnchorPoint(CGPoint.make(0, 0));

            // Aliased images
            //	tilemap.texture.setAliasTexParameters();


            // background layer: another image
            CCSprite background = CCSprite.sprite("background.png");
            // scale the image (optional)
            background.setScale(1.5f);
            // change the transform anchor point (optional)
            background.setAnchorPoint(CGPoint.make(0,0));


            // create a void node, a parent node
            CCParallaxNode voidNode = CCParallaxNode.node();

            // NOW add the 3 layers to the 'void' node

            // background image is moved at a ratio of 0.4x, 0.5y
            voidNode.addChild(background, -1, 0.4f, 0.5f, 0, 0);

            // tiles are moved at a ratio of 2.2x, 1.0y
            voidNode.addChild(tilemap, 1, 2.2f, 1.0f, 0, -200);

            // top image is moved at a ratio of 3.0x, 2.5y
            voidNode.addChild(cocosImage, 2, 3.0f, 2.5f, 200, 800);


            // now create some actions that will move the 'void' node
            // and the children of the 'void' node will move at different
            // speed, thus, simulation the 3D environment
            CCIntervalAction goUp = CCMoveBy.action(4, CGPoint.make(0,-500));
            CCIntervalAction goDown = goUp.reverse();
            CCIntervalAction go = CCMoveBy.action(8, CGPoint.make(-1000, 0));
            CCIntervalAction goBack = go.reverse();
            CCIntervalAction seq = CCSequence.actions(
                      goUp,
                      go,
                      goDown,
                      goBack);
            voidNode.runAction(CCRepeatForever.action(seq));

            addChild(voidNode);

        }

        @Override
        public String title() {
            return "Parallax: parent and 3 children";
        }
    }

    static class Parallax2 extends ParallaxDemo {
        CGPoint previousLocation = new CGPoint();

        public Parallax2() {
            setIsTouchEnabled(true);

            // Top Layer, a simple image
            CCSprite cocosImage = CCSprite.sprite("powered.png");
            // scale the image (optional)
            cocosImage.setScale(2.5f);
            // change the transform anchor point to 0,0 (optional)
            cocosImage.setAnchorPoint(CGPoint.make(0,0));


            // Middle layer: a Tile map atlas
            CCTileMapAtlas tilemap = CCTileMapAtlas.tilemap("tiles.png", "levelmap.tga", 16, 16);

            // change the transform anchor to 0,0 (optional)
            tilemap.setAnchorPoint(CGPoint.make(0, 0));

            // Aliased images
            //tilemap.texture.setAliasTexParameters();


            // background layer: another image
            CCSprite background = CCSprite.sprite("background.png");
            // scale the image (optional)
            background.setScale(1.5f);
            // change the transform anchor point (optional)
            background.setAnchorPoint(CGPoint.make(0,0));


            // create a void node, a parent node
            CCParallaxNode voidNode = CCParallaxNode.node();

            // NOW add the 3 layers to the 'void' node

            // background image is moved at a ratio of 0.4x, 0.5y
            voidNode.addChild(background, -1, 0.4f, 0.5f, 0, 0);

            // tiles are moved at a ratio of 1.0, 1.0y
            voidNode.addChild(tilemap, 1, 1.0f, 1.0f, 0, -200);

            // top image is moved at a ratio of 3.0x, 2.5y
            voidNode.addChild(cocosImage, 2, 3.0f, 2.5f, 200, 1000);
            addChild(voidNode, 0, kTagNode);
        }

        @Override
        public void registerWithTouchDispatcher()
        {
            System.out.println("ParallaxTest: registerWithTouchDispatcher");
            CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
        }


        @Override
        public boolean ccTouchesBegan(MotionEvent event)
        {
            previousLocation.set(event.getX(), event.getY());
            return CCTouchDispatcher.kEventHandled;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event)
        {
            return CCTouchDispatcher.kEventHandled;
        }

        @Override
        public boolean ccTouchesCancelled(MotionEvent event)
        {
            return CCTouchDispatcher.kEventIgnored;
        }

        @Override
        public boolean ccTouchesMoved(MotionEvent event)
        {
            CGPoint diff = CGPoint.zero();

            CGPoint touchLocation = CGPoint.make(event.getX(), event.getY());

            CGPoint location = CCDirector.sharedDirector().convertToGL(touchLocation);
            CGPoint prevLocation = CCDirector.sharedDirector().convertToGL(previousLocation);

            diff.x = location.x-prevLocation.x;
            diff.y = location.y-prevLocation.y;

            CCNode node = getChildByTag(kTagNode);
            node.setPosition(CGPoint.ccpAdd(node.getPosition(), diff));

            previousLocation = touchLocation;
            
            return CCTouchDispatcher.kEventHandled;
        }


        @Override
        public String title() {
            return "Parallax: drag screen";
        }
    }
}
