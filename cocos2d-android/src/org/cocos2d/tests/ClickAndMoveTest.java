package org.cocos2d.tests;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.ActionManager;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.interval.*;
import org.cocos2d.layers.ColorLayer;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccMacros;
import org.cocos2d.types.CGPoint;
import org.cocos2d.events.CCTouchDispatcher;

public class ClickAndMoveTest extends Activity {
    // private static final String LOG_TAG = ClickAndMoveTest.class.getSimpleName();

    // private static final boolean DEBUG = true;

    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new AlertDialog.Builder(this)
                .setTitle("Welcome")
                .setMessage("Click on the screen to move and rotate Grossini")
                .setPositiveButton("Start", null)
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
        scene.addChild(new MainLayer(), 2);

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

        ActionManager.sharedManager().removeAllActions();
        TextureManager.sharedTextureManager().removeAllTextures();
    }

    static class MainLayer extends Layer {
        static final int kTagSprite = 1;

        public MainLayer() {

            isTouchEnabled_ = true;

            Sprite sprite = Sprite.sprite("grossini.png");

            Layer layer = ColorLayer.node(new ccColor4B(255, 255, 0, 255));
            addChild(layer, -1);

            addChild(sprite, 0, kTagSprite);
            sprite.setPosition(CGPoint.make(20, 150));

            sprite.runAction(JumpTo.action(4, 300, 48, 100, 4));

            layer.runAction(RepeatForever.action(Sequence.actions(FadeIn.action(1), FadeOut.action(1))));
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint convertedLocation = Director.sharedDirector().convertCoordinate(event.getX(), event.getY());

            CCNode s = getChild(kTagSprite);
            s.stopAllActions();
            s.runAction(MoveTo.action(1.0f, convertedLocation.x, convertedLocation.y));
           
            CGPoint pnt = s.getPosition();
            float o = convertedLocation.x - pnt.x;
            float a = convertedLocation.y - pnt.y;
            float at = ccMacros.CC_RADIANS_TO_DEGREES((float) Math.atan(o / a));

            if (a < 0) {
                if (o < 0)
                    at = 180 + Math.abs(at);
                else
                    at = 180 - Math.abs(at);
            }

            s.runAction(RotateTo.action(1, at));

            return CCTouchDispatcher.kEventHandled;
        }

    }

}

