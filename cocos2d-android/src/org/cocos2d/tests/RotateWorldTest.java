package org.cocos2d.tests;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCRepeat;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class RotateWorldTest extends Activity {
    // private static final String LOG_TAG = RotateWorldTest.class.getSimpleName();
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

    static class SpriteLayer extends CCLayer {

        public SpriteLayer() {
            float x, y;

            CGSize size = CCDirector.sharedDirector().winSize();
            x = size.width;
            y = size.height;

            CCSprite sprite = CCSprite.sprite("grossini.png");
            CCSprite spriteSister1 = CCSprite.sprite("grossinis_sister1.png");
            CCSprite spriteSister2 = CCSprite.sprite("grossinis_sister2.png");

            sprite.setScale(1.5f);
            spriteSister1.setScale(1.5f);
            spriteSister2.setScale(1.5f);

            sprite.setPosition(CGPoint.make(x / 2, y / 2));
            spriteSister1.setPosition(CGPoint.make(40, y / 2));
            spriteSister2.setPosition(CGPoint.make(x - 40, y / 2));

            CCAction rot = CCRotateBy.action(16, -3600);

            addChild(sprite);
            addChild(spriteSister1);
            addChild(spriteSister2);

            sprite.runAction(rot);

            CCIntervalAction jump1 = CCJumpBy.action(4, CGPoint.make(-400, 0), 100, 4);
            CCIntervalAction jump2 = jump1.reverse();

            CCIntervalAction rot1 = CCRotateBy.action(4, 360 * 2);
            CCIntervalAction rot2 = rot1.reverse();

            spriteSister1.runAction(CCRepeat.action(CCSequence.actions(jump2, jump1), 5));
            spriteSister2.runAction(CCRepeat.action(CCSequence.actions(jump1.copy(), jump2.copy()), 5));

            spriteSister1.runAction(CCRepeat.action(CCSequence.actions(rot1, rot2), 5));
            spriteSister2.runAction(CCRepeat.action(CCSequence.actions(rot2.copy(), rot1.copy()), 5));
        }
    }

    static class MainLayer extends CCLayer {

        public MainLayer() {
            float x, y;

            CGSize size = CCDirector.sharedDirector().winSize();
            x = size.width;
            y = size.height;

            CCNode blue = CCColorLayer.node(new ccColor4B(0, 0, 255, 255));
            CCNode red = CCColorLayer.node(new ccColor4B(255, 0, 0, 255));
            CCNode green = CCColorLayer.node(new ccColor4B(0, 255, 0, 255));
            CCNode white = CCColorLayer.node(new ccColor4B(255, 255, 255, 255));

            blue.setScale(0.5f);
            blue.setPosition(CGPoint.make(-x / 4, -y / 4));
            blue.addChild(new SpriteLayer());

            red.setScale(0.5f);
            red.setPosition(CGPoint.make(x / 4, -y / 4));

            green.setScale(0.5f);
            green.setPosition(CGPoint.make(-x / 4, y / 4));

            white.setScale(0.5f);
            white.setPosition(CGPoint.make(x / 4, y / 4));

            addChild(blue, -1);
            addChild(white);
            addChild(green);
            addChild(red);

            CCAction rot = CCRotateBy.action(8, 720);

            blue.runAction(rot);
            red.runAction(rot.copy());
            green.runAction(rot.copy());
            white.runAction(rot.copy());
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
        scene.addChild(new MainLayer());
        scene.runAction(CCRotateBy.action(4, -360));

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
