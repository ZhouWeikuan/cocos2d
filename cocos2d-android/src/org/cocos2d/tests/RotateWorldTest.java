package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.interval.*;
import org.cocos2d.layers.ColorLayer;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.CGSize;

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

    static class SpriteLayer extends Layer {

        public SpriteLayer() {
            float x, y;

            CGSize size = CCDirector.sharedDirector().winSize();
            x = size.width;
            y = size.height;

            Sprite sprite = Sprite.sprite("grossini.png");
            Sprite spriteSister1 = Sprite.sprite("grossinis_sister1.png");
            Sprite spriteSister2 = Sprite.sprite("grossinis_sister2.png");

            sprite.setScale(1.5f);
            spriteSister1.setScale(1.5f);
            spriteSister2.setScale(1.5f);

            sprite.setPosition(CGPoint.make(x / 2, y / 2));
            spriteSister1.setPosition(CGPoint.make(40, y / 2));
            spriteSister2.setPosition(CGPoint.make(x - 40, y / 2));

            Action rot = RotateBy.action(16, -3600);

            addChild(sprite);
            addChild(spriteSister1);
            addChild(spriteSister2);

            sprite.runAction(rot);

            IntervalAction jump1 = JumpBy.action(4, -400, 0, 100, 4);
            IntervalAction jump2 = jump1.reverse();

            IntervalAction rot1 = RotateBy.action(4, 360 * 2);
            IntervalAction rot2 = rot1.reverse();

            spriteSister1.runAction(Repeat.action(Sequence.actions(jump2, jump1), 5));
            spriteSister2.runAction(Repeat.action(Sequence.actions(jump1.copy(), jump2.copy()), 5));

            spriteSister1.runAction(Repeat.action(Sequence.actions(rot1, rot2), 5));
            spriteSister2.runAction(Repeat.action(Sequence.actions(rot2.copy(), rot1.copy()), 5));
        }
    }

    static class MainLayer extends Layer {

        public MainLayer() {
            float x, y;

            CGSize size = CCDirector.sharedDirector().winSize();
            x = size.width;
            y = size.height;

            CCNode blue = ColorLayer.node(new ccColor4B(0, 0, 255, 255));
            CCNode red = ColorLayer.node(new ccColor4B(255, 0, 0, 255));
            CCNode green = ColorLayer.node(new ccColor4B(0, 255, 0, 255));
            CCNode white = ColorLayer.node(new ccColor4B(255, 255, 255, 255));

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

            Action rot = RotateBy.action(8, 720);

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

        Scene scene = Scene.node();
        scene.addChild(new MainLayer());
        scene.runAction(RotateBy.action(4, -360));

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
}
