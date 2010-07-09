package org.cocos2d.tests;

import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.ColorLayer;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.LabelAtlas;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.nodes.TextureManager;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.TextureAtlas;
import org.cocos2d.particlesystem.ParticleExplosion;
import org.cocos2d.particlesystem.ParticleFire;
import org.cocos2d.particlesystem.ParticleFireworks;
import org.cocos2d.particlesystem.ParticleSystem;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.CCSize;
import org.cocos2d.utils.CCFormatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ParticleTest extends Activity {
    // private static final String LOG_TAG = AtlasSpriteTest.class.getSimpleName();

    private static final boolean DEBUG = true;

    private CCGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("There are known problems with this demo.")
                .setPositiveButton("Ok", null)
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
            DemoExplosion.class,
            DemoFirework.class,
            DemoFire.class,
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

    static abstract class ParticleDemo extends ColorLayer {
        TextureAtlas atlas;
        static final int kTagLabelAtlas = 1;
        ParticleSystem	emitter;
        Sprite background;


        public ParticleDemo() {
            super(new ccColor4B(127,127,127,255));

            CCSize s = Director.sharedDirector().winSize();

            Label label = Label.label(title(), "DroidSans", 18);
            label.setPosition(s.width / 2, s.height - 30);
            addChild(label);

            MenuItemImage item1 = MenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            MenuItemImage item2 = MenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            MenuItemImage item3 = MenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

//            MenuItemToggle item4 = MenuItemToggle.item(this,"toggleCallback",
//                                     MenuItemFont.item("Free Movement"),
//                                     MenuItemFont.item("Grouped Movement"));

            Menu menu = Menu.menu(item1, item2, item3 /*, item4 */);
            menu.setPosition(0, 0);
            item1.setPosition(s.width / 2 - 100, 30);
            item2.setPosition(s.width / 2, 30);
            item3.setPosition(s.width / 2 + 100, 30);
//            item4.setAnchorPoint(0,0);

            addChild(menu, 100);

            LabelAtlas labelAtlas = LabelAtlas.label("0000", "fps_images.png", 16, 24, '.');
            addChild(labelAtlas, 100, kTagLabelAtlas);
            labelAtlas.setPosition(254,50);

            // moving background
            background = Sprite.sprite("background3.png");
            addChild(background, 5);
            background.setPosition(s.width/2, s.height-180);

            IntervalAction move = MoveBy.action(4, 300, 0);
            IntervalAction move_back = move.reverse();
            IntervalAction seq = Sequence.actions(move, move_back);
            background.runAction(RepeatForever.action(seq));


            schedule("step");

        }

        public void step(float dt)
        {
            LabelAtlas atlas = (LabelAtlas) getChild(kTagLabelAtlas);

            String str = new CCFormatter().format("%4d", emitter.getParticleCount());
            atlas.setString(str);
        }


        public void toggleCallback()
        {
            if( emitter.getPositionType() == ParticleSystem.kPositionTypeGrouped )
                emitter.setPositionType(ParticleSystem.kPositionTypeFree);
            else
                emitter.setPositionType(ParticleSystem.kPositionTypeGrouped);
        }

        public void restartCallback() {
//            Scene s = Scene.node();
//            s.addChild(restartAction());
//            Director.sharedDirector().replaceScene(s);
            emitter.resetSystem();
        }

        public void setEmitterPosition()
        {
            emitter.setPosition(200, 70);
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

    static class DemoExplosion extends ParticleDemo {

        static final int kTagLabelAtlas = 1;
        static final int kTagEmitter = 2;

        @Override
        public void onEnter() {
            super.onEnter();
            emitter = ParticleExplosion.node();
            addChild(emitter, 10, kTagEmitter);

            emitter.setTexture(TextureManager.sharedTextureManager().addImage("stars.png"));
            setEmitterPosition();
        }

        @Override
        public String title() {
            return "Particle: Explosion";
        }
    }

    static class DemoFirework extends ParticleDemo {

        static final int kTagLabelAtlas = 1;
        static final int kTagEmitter = 2;

        @Override
        public void onEnter() {
            super.onEnter();
            emitter = ParticleFireworks.node();
            addChild(emitter, 10, kTagEmitter);

            emitter.setTexture(TextureManager.sharedTextureManager().addImage("stars.png"));
            setEmitterPosition();
        }

        @Override
        public String title() {
            return "Particle: Firework";
        }
    }

    static class DemoFire extends ParticleDemo {

        static final int kTagLabelAtlas = 1;
        static final int kTagEmitter = 2;

        @Override
        public void onEnter() {
            super.onEnter();
            emitter = ParticleFire.node();
            addChild(emitter, 10, kTagEmitter);

            emitter.setTexture(TextureManager.sharedTextureManager().addImage("fire.png"));
            setEmitterPosition();
        }

        @Override
        public String title() {
            return "Particle: Fire";
        }
    }
}
