package org.cocos2d.tests;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.particlesystem.ParticleExplosion;
import org.cocos2d.particlesystem.ParticleFire;
import org.cocos2d.particlesystem.ParticleFireworks;
import org.cocos2d.particlesystem.ParticleSystem;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;
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
            DemoExplosion.class,
            DemoFirework.class,
            DemoFire.class,
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

    static abstract class ParticleDemo extends CCColorLayer {
        CCTextureAtlas atlas;
        static final int kTagLabelAtlas = 1;
        ParticleSystem	emitter;
        Sprite background;


        public ParticleDemo() {
            super(new ccColor4B(127,127,127,255));

            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 18);
            label.setPosition(CGPoint.make(s.width / 2, s.height - 30));
            addChild(label);

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

//            MenuItemToggle item4 = MenuItemToggle.item(this,"toggleCallback",
//                                     MenuItemFont.item("Free Movement"),
//                                     MenuItemFont.item("Grouped Movement"));

            CCMenu menu = CCMenu.menu(item1, item2, item3 /*, item4 */);
            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
//            item4.setAnchorPoint(0,0);

            addChild(menu, 100);

            CCLabelAtlas labelAtlas = CCLabelAtlas.label("0000", "fps_images.png", 16, 24, '.');
            addChild(labelAtlas, 100, kTagLabelAtlas);
            labelAtlas.setPosition(CGPoint.make(254,50));

            // moving background
            background = Sprite.sprite("background3.png");
            addChild(background, 5);
            background.setPosition(CGPoint.make(s.width/2, s.height-180));

            CCIntervalAction move = CCMoveBy.action(4, CGPoint.ccp(300, 0));
            CCIntervalAction move_back = move.reverse();
            CCIntervalAction seq = CCSequence.actions(move, move_back);
            background.runAction(CCRepeatForever.action(seq));


            schedule("step");

        }

        public void step(float dt)
        {
            CCLabelAtlas atlas = (CCLabelAtlas) getChild(kTagLabelAtlas);

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
            emitter.setPosition(CGPoint.make(200, 70));
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

    static class DemoExplosion extends ParticleDemo {

        static final int kTagLabelAtlas = 1;
        static final int kTagEmitter = 2;

        @Override
        public void onEnter() {
            super.onEnter();
            emitter = ParticleExplosion.node();
            addChild(emitter, 10, kTagEmitter);

            emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars.png"));
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

            emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars.png"));
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

            emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));
            setEmitterPosition();
        }

        @Override
        public String title() {
            return "Particle: Fire";
        }
    }
}
