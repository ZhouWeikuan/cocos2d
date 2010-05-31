package org.cocos2d.tests;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.layers.ColorLayer;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemFont;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.TextureManager;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.transitions.FlipXTransition;
import org.cocos2d.transitions.SlideInTTransition;
import org.cocos2d.transitions.TransitionScene;
import org.cocos2d.types.CCColor4B;
import org.cocos2d.events.TouchDispatcher;


public class SceneTest extends Activity {
    private static final String LOG_TAG = SceneTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);

        applicationDidFinishLaunching(this, mGLSurfaceView);
    }

    static class Layer1 extends Layer {

        public Layer1() {
            MenuItemFont item1 = MenuItemFont.item("Test pushScene", this, "onPushScene");
            MenuItemFont item2 = MenuItemFont.item("Test pushScene w/transition", this, "onPushSceneTran");
            MenuItemFont item3 = MenuItemFont.item("Quit", this, "onQuit");

            Menu menu = Menu.menu(item1, item2, item3);
            menu.alignItemsVertically();

            addChild(menu);
        }

        public void onPushScene() {
            Scene scene = Scene.node();
            scene.addChild(new Layer2(), 0);
            Director.sharedDirector().pushScene(scene);
        }

        public void onPushSceneTran() {
            Scene scene = Scene.node();
            scene.addChild(new Layer2(), 0);
            Director.sharedDirector().pushScene(SlideInTTransition.transition(1, scene));
        }


        public void onQuit() {
            Director.sharedDirector().popScene();
        }

        public void onVoid() {
        }
    }

    static class Layer2 extends Layer {
        public Layer2() {
            MenuItemFont item1 = MenuItemFont.item("Replace Scene", this, "onReplaceScene");
            MenuItemFont item2 = MenuItemFont.item("Replace Scene Transition", this, "onReplaceSceneTransition");
            MenuItemFont item3 = MenuItemFont.item("Go Back", this, "onGoBack");

            Menu menu = Menu.menu(item1, item2, item3);
            menu.alignItemsVertically();

            addChild(menu);
        }

        public void onGoBack() {
            Director.sharedDirector().popScene();
        }

        public void onReplaceScene() {
            Scene scene = Scene.node();
            scene.addChild(new Layer3(), 0);
            Director.sharedDirector().replaceScene(scene);
        }

        public void onReplaceSceneTransition() {
            Scene s = Scene.node();
            s.addChild(new Layer3(), 0);
            Director.sharedDirector().replaceScene(FlipXTransition.transition(2.0f, s, TransitionScene.Orientation.kOrientationLeftOver));
        }
    }

    static class Layer3 extends ColorLayer {
        public Layer3() {
            super(new CCColor4B(0, 0, 255, 255));

            isTouchEnabled_ = true;

            Label label = Label.label("Touch to pop scene", "DroidSans", 32);
            addChild(label);
            float width = Director.sharedDirector().winSize().width;
            float height = Director.sharedDirector().winSize().height;
            label.setPosition(width / 2, height / 2);
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event) {
            Director.sharedDirector().popScene();
            return TouchDispatcher.kEventHandled;
        }
    }


    // CLASS IMPLEMENTATIONS
    public void applicationDidFinishLaunching(Context context, View view) {

        // attach the OpenGL view to a window
        Director.sharedDirector().attachInView(view);

        // set landscape mode
        Director.sharedDirector().setLandscape(false);

        // show FPS
        Director.sharedDirector().setDisplayFPS(false);

        // frames per second
        Director.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = Scene.node();
        scene.addChild(new Layer1(), 0);

        // Make the Scene active
        Director.sharedDirector().runWithScene(scene);

    }

    // getting a call, pause the game
    public void applicationWillResignActive(Context context) {
        Director.sharedDirector().pause();
    }

    // call got rejected
    public void applicationDidBecomeActive(Context context) {
        Director.sharedDirector().resume();
    }

    // purge memroy
    public void applicationDidReceiveMemoryWarning(Context context) {
        TextureManager.sharedTextureManager().removeAllTextures();
    }

    // next delta time will be zero
    public void applicationSignificantTimeChange(Context context) {
        //	Director.sharedDirector().setNextDeltaTimeZero(true);
    }
}
