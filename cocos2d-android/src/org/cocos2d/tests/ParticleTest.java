package org.cocos2d.tests;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemFont;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCParallaxNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.particlesystem.CCParticleExplosion;
import org.cocos2d.particlesystem.CCParticleFire;
import org.cocos2d.particlesystem.CCParticleFireworks;
import org.cocos2d.particlesystem.CCParticleFlower;
import org.cocos2d.particlesystem.CCParticleGalaxy;
import org.cocos2d.particlesystem.CCParticleMeteor;
import org.cocos2d.particlesystem.CCParticleRain;
import org.cocos2d.particlesystem.CCParticleSmoke;
import org.cocos2d.particlesystem.CCParticleSnow;
import org.cocos2d.particlesystem.CCParticleSpiral;
import org.cocos2d.particlesystem.CCParticleSun;
import org.cocos2d.particlesystem.CCParticleSystem;
import org.cocos2d.particlesystem.CCPointParticleSystem;
import org.cocos2d.particlesystem.CCQuadParticleSystem;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.utils.javolution.MathLib;
import org.cocos2d.utils.javolution.TextBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class ParticleTest extends Activity {
    // private static final String LOG_TAG = AtlasSpriteTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mGLSurfaceView = new CCGLSurfaceView(this);
        setContentView(mGLSurfaceView);
        
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
    public void onStart() {
        super.onStart();

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
        //CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
		DemoFlower.class,
		DemoGalaxy.class,
		DemoFirework.class,
		DemoSpiral.class,
		DemoSun.class,
		DemoMeteor.class,
		DemoFire.class,
		DemoSmoke.class,
		DemoExplosion.class,
		DemoSnow.class,
		DemoRain.class,
		DemoBigFlower.class,
		DemoRotFlower.class,
		DemoModernArt.class,
		DemoRing.class,

		ParallaxParticle.class,

		/* we can't work on plist files now
		ParticleDesigner1.class,
		ParticleDesigner2.class,
		ParticleDesigner3.class,
		ParticleDesigner4.class,
		ParticleDesigner5.class,
		ParticleDesigner6.class,
		ParticleDesigner7.class,
		ParticleDesigner8.class,
		ParticleDesigner9.class,
		ParticleDesigner10.class,
		ParticleDesigner11.class, */

		RadiusMode1.class,
		RadiusMode2.class,
		// Issue704.class,
		
		// Issue872.class,
		// Issue870.class,
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
            e.printStackTrace();
            return null;
        }
    }

    static abstract class ParticleDemo extends CCColorLayer implements UpdateCallback {
        CCTextureAtlas atlas;
        static final int kTagLabelAtlas = 1;
        CCParticleSystem	emitter;
        CCSprite background;


        public ParticleDemo() {
            super(new ccColor4B(127,127,127,255));

            this.setIsTouchEnabled(true);
            
            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 18);
            label.setPosition(CGPoint.make(s.width / 2, s.height - 50));
            addChild(label, 100);
            
            String subtitle = subtitle();
    		if( subtitle != null) {
    			CCLabel l = CCLabel.makeLabel(subtitle, "DroidSerif", 16);
    			addChild(l, 100);
    			l.setPosition(CGPoint.ccp(s.width/2, s.height-80));
    		}

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenuItemToggle item4 = CCMenuItemToggle.item(this,"toggleCallback",
                                     CCMenuItemFont.item("Free Movement"),
                                     CCMenuItemFont.item("Grouped Movement"));

            CCMenu menu = CCMenu.menu(item1, item2, item3, item4);
            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
            item4.setPosition(CGPoint.ccp(0, 100));
            item4.setAnchorPoint(CGPoint.ccp(0,0));

            addChild(menu, 100);

            CCLabelAtlas labelAtlas = CCLabelAtlas.label("0000", "fps_images.png", 16, 24, '.');
            addChild(labelAtlas, 100, kTagLabelAtlas);
            labelAtlas.setPosition(CGPoint.make(s.width-66, 50));

            // moving background
            background = CCSprite.sprite("background3.png");
            addChild(background, 5);
            background.setPosition(CGPoint.make(s.width/2, s.height-180));

            CCIntervalAction move = CCMoveBy.action(4, CGPoint.ccp(300, 0));
            CCIntervalAction move_back = move.reverse();
            CCIntervalAction seq = CCSequence.actions(move, move_back);
            background.runAction(CCRepeatForever.action(seq));

            this.scheduleUpdate();
        }
        
        @Override
        public boolean ccTouchesBegan(MotionEvent e){
        	// claim the touch
        	return true;
        }
        
        @Override
        public boolean ccTouchesMoved(MotionEvent e) {
        	return true;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent e) {
        	CGPoint location = CGPoint.ccp(e.getX(), e.getY());
        	CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(location);

        	CGPoint pos = CGPoint.zero();
        	
        	if( background != null)
        		pos = background.convertToWorldSpace(0, 0);
        	emitter.setPosition(CGPoint.ccpSub(convertedLocation, pos));
        	return true;
        }

        private TextBuilder particleCountString = new TextBuilder();
        
        // String.format("%4d", emitter.getParticleCount())
        private TextBuilder getParticleCountString(int c) {
        	int len = MathLib.digitLength(c);
        	
        	int zeros = 4 - len;
        	
        	while (zeros-- > 0) {
				particleCountString.append('0');
			}
        	particleCountString.append(c);
        	
        	return particleCountString;
        }

        public void update(float dt) {
        	CCLabelAtlas atlas = (CCLabelAtlas) getChildByTag(kTagLabelAtlas);

        	particleCountString.reset();
        	
//        	String str = String.format("%4d", emitter.getParticleCount());
        	atlas.setString(getParticleCountString(emitter.getParticleCount()));
        }

        public void toggleCallback(Object sender) {
            if( emitter.getPositionType() == CCParticleSystem.kCCPositionTypeGrouped )
                emitter.setPositionType(CCParticleSystem.kCCPositionTypeFree);
            else
                emitter.setPositionType(CCParticleSystem.kCCPositionTypeGrouped);
        }

        public void restartCallback(Object sender) {
//            Scene s = Scene.node();
//            s.addChild(restartAction());
//            Director.sharedDirector().replaceScene(s);
            emitter.resetSystem();
        }

        public void setEmitterPosition() {
        	if( CGPoint.equalToPoint( emitter.getCenterOfGravity(), CGPoint.zero() ) ) 
        		emitter.setPosition(CGPoint.make(200, 70));
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

        public String title() {
        	return "No title";
        }
        
        public String subtitle() {
        	return "Tap the screen";
        }
    }

    static class DemoFire extends ParticleDemo {
    	public void onEnter() {
    		super.onEnter();
    		emitter = CCParticleFire.node();
    		background.addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));
    		CGPoint p = emitter.getPosition();
    		emitter.setPosition(CGPoint.ccp(p.x, 100));

    		setEmitterPosition();
    	}

    	public String title() {
    		return "ParticleFire";
    	}
    }


    static class DemoExplosion extends ParticleDemo {

        static final int kTagLabelAtlas = 1;
        static final int kTagEmitter = 2;

        @Override
        public void onEnter() {
            super.onEnter();
            emitter = CCParticleExplosion.node();
            addChild(emitter, 10);

            emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars_grayscale.png"));
    		emitter.setAutoRemoveOnFinish(true);

            setEmitterPosition();            
        }

        @Override
        public String title() {
            return "ParticleExplosion";
        }
    }

    static class DemoFirework extends ParticleDemo {

        static final int kTagLabelAtlas = 1;
        static final int kTagEmitter = 2;

        @Override
        public void onEnter() {
            super.onEnter();
            emitter = CCParticleFireworks.node();
            background.addChild(emitter, 10);

            emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars.png"));
            emitter.setBlendAdditive(true);
            setEmitterPosition();
        }

        @Override
        public String title() {
            return "ParticleFireworks";
        }
    }

    static class DemoSun extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		emitter = CCParticleSun.node();
    		background.addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title()
    	{
    		return "ParticleSun";
    	}
    }

    static class DemoGalaxy extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		emitter = CCParticleGalaxy.node();
    		background.addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

    		setEmitterPosition();
    	}

    	@Override
    	public String title() {
    		return "ParticleGalaxy";
    	}
    }

    static class DemoFlower extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		emitter = CCParticleFlower.node();
    		background.addChild(emitter, 10);
    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars_grayscale.png"));

    		setEmitterPosition();
    	}

    	@Override
    	public String title() {
    		return "ParticleFlower";
    	}
    }

    static class DemoBigFlower extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		
    		emitter = new CCQuadParticleSystem(50);
    		background.addChild(emitter, 10);
    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars_grayscale.png"));

    		// duration
    		emitter.setDuration(CCParticleSystem.kCCParticleDurationInfinity);

    		// Gravity Mode: gravity
    		emitter.setGravity(CGPoint.zero());

    		// Set "Gravity" mode (default one)
    		emitter.setEmitterMode(CCParticleSystem.kCCParticleModeGravity);

    		// Gravity Mode: speed of particles
    		emitter.setSpeed(160);
    		emitter.setSpeedVar(20);

    		// Gravity Mode: radial
    		emitter.setRadialAccel(-120);
    		emitter.setRadialAccelVar(0);

    		// Gravity Mode: tagential
    		emitter.setTangentialAccel(30);
    		emitter.setTangentialAccelVar(0);

    		// angle
    		emitter.setAngle(90);
    		emitter.setAngleVar(360);

    		// emitter position
    		emitter.setPosition(CGPoint.ccp(160,240));
    		emitter.setPosVar(CGPoint.zero());

    		// life of particles
    		emitter.setLife(4);
    		emitter.setLifeVar(1);

    		// spin of particles
    		emitter.setStartSpin(0);
    		emitter.setStartSpinVar(0);
    		emitter.setEndSpin(0);
    		emitter.setEndSpinVar(0);

    		// color of particles
    		ccColor4F startColor = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColorVar(startColorVar);

    		ccColor4F endColor = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);
    		emitter.setEndColor(endColor);

    		ccColor4F endColorVar = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);	
    		emitter.setEndColorVar(endColorVar);

    		// size, in pixels
    		emitter.setStartSize(80.0f);
    		emitter.setStartSizeVar(40.0f);
    		emitter.setEndSize(CCParticleSystem.kCCParticleStartSizeEqualToEndSize);

    		// emits per second
    		emitter.setEmissionRate( emitter.getTotalParticles()/emitter.getLife());

    		// additive
    		emitter.setBlendAdditive(true);

    		setEmitterPosition();
    	}

    	@Override
    	public String title() {
    		return "Big Particles";
    	}
    }

    static class DemoRotFlower extends ParticleDemo {

    	@Override
    	public void onEnter() {
    		super.onEnter();
    		emitter = new CCQuadParticleSystem(300);
    		background.addChild(emitter, 10);
    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars2_grayscale.png"));

    		// duration
    		emitter.setDuration(CCParticleSystem.kCCParticleDurationInfinity);

    		// Set "Gravity" mode (default one)
    		emitter.setEmitterMode(CCParticleSystem.kCCParticleModeGravity);

    		// Gravity mode: gravity
    		emitter.setGravity(CGPoint.zero());

    		// Gravity mode: speed of particles
    		emitter.setSpeed(160);
    		emitter.setSpeedVar(20);

    		// Gravity mode: radial
    		emitter.setRadialAccel(-120);
    		emitter.setRadialAccelVar(0);

    		// Gravity mode: tagential
    		emitter.setTangentialAccel(30);
    		emitter.setTangentialAccelVar(0);

    		// emitter position
    		emitter.setPosition(CGPoint.ccp(160,240));
    		emitter.setPosVar(CGPoint.zero());

    		// angle
    		emitter.setAngle(90);
    		emitter.setAngleVar(360);

    		// life of particles
    		emitter.setLife(3);
    		emitter.setLifeVar(1);

    		// spin of particles
    		emitter.setStartSpin(0);
    		emitter.setStartSpinVar(0);
    		emitter.setEndSpin(0);
    		emitter.setEndSpinVar(2000);

    		// color of particles
    		ccColor4F startColor = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColorVar(startColorVar);

    		ccColor4F endColor = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);
    		emitter.setEndColor(endColor);

    		ccColor4F endColorVar = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);	
    		emitter.setEndColorVar(endColorVar);

    		// size, in pixels
    		emitter.setStartSize(30.0f);
    		emitter.setStartSizeVar(00.0f);
    		emitter.setEndSize(CCParticleSystem.kCCParticleStartSizeEqualToEndSize);

    		// emits per second
    		emitter.setEmissionRate(emitter.getTotalParticles()/emitter.getLife());

    		// additive
    		emitter.setBlendAdditive(false);

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "Spinning Particles";
    	}
    }

    static class DemoMeteor extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		emitter = CCParticleMeteor.node();
    		background.addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "ParticleMeteor";
    	}
    }

    static class DemoSpiral extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		emitter = CCParticleSpiral.node();
    		background.addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "ParticleSpiral";
    	}
    }


    static class DemoSmoke extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		
    		emitter = CCParticleSmoke.node();
    		background.addChild(emitter, 10);

    		CGPoint p = emitter.getPosition();
    		emitter.setPosition(CGPoint.ccp( p.x, 100));

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "ParticleSmoke";
    	}
    }

    static class DemoSnow extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		
    		emitter = CCParticleSnow.node();
    		background.addChild(emitter, 10);

    		CGPoint p = emitter.getPosition();
    		emitter.setPosition(CGPoint.ccp( p.x, p.y-110));
    		emitter.setLife(3);
    		emitter.setLifeVar(1);

    		// gravity
    		emitter.setGravity(CGPoint.ccp(0,-10));

    		// speed of particles
    		emitter.setSpeed(130);
    		emitter.setSpeedVar(30);


    		ccColor4F startColor = emitter.getStartColor();
    		startColor.r = 0.9f;
    		startColor.g = 0.9f;
    		startColor.b = 0.9f;
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = emitter.getStartColorVar();
    		startColorVar.b = 0.1f;
    		emitter.setStartColorVar(startColorVar);

    		emitter.setEmissionRate(emitter.getTotalParticles()/emitter.getLife());

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("snow.png"));

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "ParticleSnow";
    	}
    }

    static class DemoRain extends ParticleDemo {
    	@Override
    	public void onEnter(){
    		super.onEnter();
    		
    		emitter = CCParticleRain.node();
    		background.addChild(emitter, 10);

    		CGPoint p = emitter.getPosition();
    		emitter.setPosition(CGPoint.ccp( p.x, p.y-100));
    		emitter.setLife(4);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "ParticleRain";
    	}
    }

    static class DemoModernArt extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();
    		
    		emitter = new CCPointParticleSystem(1000);
    		background.addChild(emitter, 10);

    		CGSize s = CCDirector.sharedDirector().winSize();

    		// duration
    		emitter.setDuration(CCParticleSystem.kCCParticleDurationInfinity);

    		// Gravity mode
    		emitter.setEmitterMode(CCParticleSystem.kCCParticleModeGravity);

    		// Gravity mode: gravity
    		emitter.setGravity(CGPoint.ccp(0,0));

    		// Gravity mode: radial
    		emitter.setRadialAccel(70);
    		emitter.setRadialAccelVar(10);

    		// Gravity mode: tagential
    		emitter.setTangentialAccel(80);
    		emitter.setTangentialAccelVar(0);

    		// Gravity mode: speed of particles
    		emitter.setSpeed(50);
    		emitter.setSpeedVar(10);

    		// angle
    		emitter.setAngle(0);
    		emitter.setAngleVar(360);

    		// emitter position
    		emitter.setPosition(CGPoint.ccp( s.width/2, s.height/2));
    		emitter.setPosVar(CGPoint.zero());

    		// life of particles
    		emitter.setLife(2.0f);
    		emitter.setLifeVar(0.3f);

    		// emits per frame
    		emitter.setEmissionRate(emitter.getTotalParticles()/emitter.getLife());

    		// color of particles
    		ccColor4F startColor = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColorVar(startColorVar);

    		ccColor4F endColor = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);
    		emitter.setEndColor(endColor);

    		ccColor4F endColorVar = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);	
    		emitter.setEndColorVar(endColorVar);

    		// size, in pixels
    		emitter.setStartSize(1.0f);
    		emitter.setStartSizeVar(1.0f);
    		emitter.setEndSize(32.0f);
    		emitter.setEndSizeVar(8.0f);

    		// texture
    		//    	emitter.texture = [[TextureCache sharedTextureCache] addImage:@"fire-grayscale.png"];

    		// additive
    		emitter.setBlendAdditive(false);

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title()
    	{
    		return "Varying size";
    	}
    }

    static class DemoRing extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();
    		emitter = CCParticleFlower.node(500);
    		background.addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars_grayscale.png"));
    		emitter.setLifeVar(0);
    		emitter.setLife(10);
    		emitter.setSpeed(100);
    		emitter.setSpeedVar(0);
    		emitter.setEmissionRate(10000);

    		setEmitterPosition();
    	}
    	
    	@Override
    	public String title() {
    		return "Ring Demo";
    	}
    }

    static class ParallaxParticle extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		background.getParent().removeChild(background, true);
    		background = null;

    		CCParallaxNode p = CCParallaxNode.node();
    		addChild(p, 5);

    		CCSprite p1 = CCSprite.sprite("background3.png");
    		background = p1;

    		CCSprite p2 = CCSprite.sprite("background3.png");

    		p.addChild(p1, 1, 0.5f, 1f, 0f, 250f);
    		p.addChild(p2, 2, 1.5f, 1f, 0f, 50f);

    		emitter = CCParticleFlower.node(500);
    		p1.addChild(emitter, 10);
    		emitter.setPosition(CGPoint.ccp(250,200));

    		CCParticleSystem par = CCParticleSun.node(250);
    		p2.addChild(par, 10);
    		par = null;

    		CCMoveBy move = CCMoveBy.action(4, CGPoint.ccp(300,0));
    		CCMoveBy move_back = move.reverse();
    		CCSequence seq = CCSequence.actions(move, move_back);
    		p.runAction(CCRepeatForever.action(seq));	
    	}
    	
    	@Override
    	public String title() {
    		return "Parallax + Particles";
    	}
    }

    static class ParticleDesigner1 extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/SpookyPeas.plist");
    		addChild(emitter, 10);
    	}
    	@Override
    	public String title() {
    		return "PD: Spooky Peas";
    	}
    }

    static class ParticleDesigner2 extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/SpinningPeas.plist");
    		addChild(emitter, 10);

    		// custom spinning
    		emitter.setStartSpin(0);
    		emitter.setStartSpin(360);
    		emitter.setEndSpin(720);
    		emitter.setEndSpinVar(360);	
    	}
    	@Override
    	public String title() {
    		return "PD: Spinning Peas";
    	}
    }


    static class ParticleDesigner3 extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/LavaFlow.plist");
    		addChild(emitter, 10);
    	}
    	
    	@Override
    	public String title() {
    		return "PD: Lava Flow";
    	}
    }

    static class ParticleDesigner4 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		emitter = CCQuadParticleSystem.particleWithFile("Particles/ExplodingRing.plist");
    		addChild(emitter, 10);

    		removeChild(background, true);
    		background = null;
    	}
    	@Override
    	public String title()
    	{
    		return "PD: Exploding Ring";
    	}
    }

    static class ParticleDesigner5 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/Comet.plist");
    		addChild(emitter, 10);
    	}
    	@Override
    	public String title()
    	{
    		return "PD: Comet";
    	}
    }

    static class ParticleDesigner6 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/BurstPipe.plist");
    		addChild(emitter, 10);
    	}
    	@Override
    	public String title()
    	{
    		return "PD: Burst Pipe";
    	}
    }

    static class ParticleDesigner7 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/BoilingFoam.plist");
    		addChild(emitter, 10);
    	}
    	@Override
    	public String title()
    	{
    		return "PD: Boiling Foam";
    	}
    }

    static class ParticleDesigner8 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/Flower.plist");
    		addChild(emitter, 10);
    	}

    	@Override
    	public String title() {
    		return "PD: Flower";
    	}
    }

    static class ParticleDesigner9 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/Spiral.plist");
    		addChild(emitter, 10);
    	}

    	@Override
    	public String title()
    	{
    		return "PD: Blur Spiral";
    	}
    }

    static class ParticleDesigner10 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/Galaxy.plist");
    		addChild(emitter, 10);
    	}

    	@Override
    	public String title() {
    		return "PD: Galaxy";
    	}
    	
    	@Override
    	public String subtitle() {
    		return "Testing radial & tangential accel";
    	}
    }

    static class ParticleDesigner11 extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/debian.plist");
    		addChild(emitter, 10);
    	}

    	@Override
    	public String title() {
    		return "PD: Debian";
    	}
    	
    	@Override
    	public String subtitle() {
    		return "Testing radial & tangential accel";
    	}
    }

    static class RadiusMode1 extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = new CCQuadParticleSystem(200);
    		addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars_grayscale.png"));

    		// duration
    		emitter.setDuration(CCParticleSystem.kCCParticleDurationInfinity);

    		// radius mode
    		emitter.setEmitterMode(CCParticleSystem.kCCParticleModeRadius);

    		// radius mode: start and end radius in pixels
    		emitter.setStartRadius(0);
    		emitter.setStartRadiusVar(0);
    		emitter.setEndRadius(160);
    		emitter.setEndRadiusVar(0);

    		// radius mode: degrees per second
    		emitter.setRotatePerSecond(180);
    		emitter.setRotatePerSecondVar(0);


    		// angle
    		emitter.setAngle(90);
    		emitter.setAngleVar(0);

    		// emitter position
    		CGSize size = CCDirector.sharedDirector().winSize();
    		emitter.setPosition(CGPoint.ccp( size.width/2, size.height/2));
    		emitter.setPosVar(CGPoint.zero());

    		// life of particles
    		emitter.setLife(5);
    		emitter.setLifeVar(0);

    		// spin of particles
    		emitter.setStartSpin(0);
    		emitter.setStartSpinVar(0);
    		emitter.setEndSpin(0);
    		emitter.setEndSpinVar(0);

    		// color of particles
    		ccColor4F startColor = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColorVar(startColorVar);

    		ccColor4F endColor = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);
    		emitter.setEndColor(endColor);

    		ccColor4F endColorVar = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);	
    		emitter.setEndColorVar(endColorVar);

    		// size, in pixels
    		emitter.setStartSize(32);
    		emitter.setStartSizeVar(0);
    		emitter.setEndSize(CCParticleSystem.kCCParticleStartSizeEqualToEndSize);

    		// emits per second
    		emitter.setEmissionRate(emitter.getTotalParticles()/emitter.getLife());

    		// additive
    		emitter.setBlendAdditive(false);
    	}
    	
    	@Override
    	public String title() {
    		return "Radius Mode: Spiral";
    	}
    }

    static class RadiusMode2 extends ParticleDemo {
    	@Override
    	public void onEnter() {
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = new CCQuadParticleSystem(200);
    		addChild(emitter, 10);

    		emitter.setTexture(CCTextureCache.sharedTextureCache().addImage("stars_grayscale.png"));

    		// duration
    		emitter.setDuration(CCParticleSystem.kCCParticleDurationInfinity);

    		// radius mode
    		emitter.setEmitterMode(CCParticleSystem.kCCParticleModeRadius);

    		// radius mode: 100 pixels from center
    		emitter.setStartRadius(100);
    		emitter.setStartRadiusVar(0);
    		emitter.setEndRadius(CCParticleSystem.kCCParticleStartRadiusEqualToEndRadius);
    		emitter.setEndRadiusVar(0);	// not used when start == end

    		// radius mode: degrees per second
    		// 45 * 4 seconds of life = 180 degrees
    		emitter.setRotatePerSecond(45);
    		emitter.setRotatePerSecondVar(0);

    		// angle
    		emitter.setAngle(90);
    		emitter.setAngleVar(0);

    		// emitter position
    		CGSize size = CCDirector.sharedDirector().winSize();
    		emitter.setPosition(CGPoint.ccp( size.width/2, size.height/2));
    		emitter.setPosVar(CGPoint.zero());

    		// life of particles
    		emitter.setLife(4);
    		emitter.setLifeVar(0);

    		// spin of particles
    		emitter.setStartSpin(0);
    		emitter.setStartSpinVar(0);
    		emitter.setEndSpin(0);
    		emitter.setEndSpinVar(0);

    		// color of particles
    		ccColor4F startColor = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColorVar(startColorVar);

    		ccColor4F endColor = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);
    		emitter.setEndColor(endColor);

    		ccColor4F endColorVar = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);	
    		emitter.setEndColorVar(endColorVar);

    		// size, in pixels
    		emitter.setStartSize(32);
    		emitter.setStartSizeVar(0);
    		emitter.setEndSize(CCParticleSystem.kCCParticleStartSizeEqualToEndSize);

    		// emits per second
    		emitter.setEmissionRate(emitter.getTotalParticles()/emitter.getLife());

    		// additive
    		emitter.setBlendAdditive(false);

    	}
    	@Override
    	public String title()
    	{
    		return "Radius Mode: Semi Circle";
    	}
    }

    static class Issue704 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = new CCQuadParticleSystem(100);
    		addChild(emitter, 10);
    		emitter.setDuration(CCParticleSystem.kCCParticleDurationInfinity);

    		// radius mode
    		emitter.setEmitterMode(CCParticleSystem.kCCParticleModeRadius);

    		// radius mode: 50 pixels from center
    		emitter.setStartRadius(50);
    		emitter.setStartRadiusVar(0);
    		emitter.setEndRadius(CCParticleSystem.kCCParticleStartRadiusEqualToEndRadius);
    		emitter.setEndRadiusVar(0);	// not used when start == end

    		// radius mode: degrees per second
    		// 45 * 4 seconds of life = 180 degrees
    		emitter.setRotatePerSecond(0);
    		emitter.setRotatePerSecondVar(0);

    		// angle
    		emitter.setAngle(90);
    		emitter.setAngleVar(0);

    		// emitter position
    		CGSize size = CCDirector.sharedDirector().winSize();
    		emitter.setPosition(CGPoint.ccp(size.width/2, size.height/2));
    		emitter.setPosVar(CGPoint.zero());

    		// life of particles
    		emitter.setLife (5);
    		emitter.setLifeVar(0);

    		// spin of particles
    		emitter.setStartSpin (0);
    		emitter.setStartSpinVar (0);
    		emitter.setEndSpin( 0);
    		emitter.setEndSpinVar (0);

    		// color of particles
    		ccColor4F startColor = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColor(startColor);

    		ccColor4F startColorVar = new ccColor4F(0.5f, 0.5f, 0.5f, 1.0f);
    		emitter.setStartColorVar(startColorVar);

    		ccColor4F endColor = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);
    		emitter.setEndColor(endColor);

    		ccColor4F endColorVar = new ccColor4F(0.1f, 0.1f, 0.1f, 0.2f);	
    		emitter.setEndColorVar(endColorVar);

    		// size, in pixels
    		emitter.setStartSize(16);
    		emitter.setStartSizeVar(0);
    		emitter.setEndSize(CCParticleSystem.kCCParticleStartSizeEqualToEndSize);

    		// emits per second
    		emitter.setEmissionRate( emitter.getTotalParticles()/emitter.getLife());

    		// additive
    		emitter.setBlendAdditive(false);

    		CCIntervalAction rot = CCRotateBy.action(16, 360);
    		emitter.runAction(CCRepeatForever.action(rot));
    	}

    	@Override
    	public String title() {
    		return "Issue 704. Free + Rot";
    	}
    	
    	@Override
    	public String subtitle() {
    		return "Emitted particles should not rotate";
    	}
    }

    static class Issue872 extends ParticleDemo {
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		emitter = CCQuadParticleSystem.particleWithFile("Particles/Upsidedown.plist");
    		addChild(emitter, 10);
    	}

    	@Override
    	public String title() {
    		return "Issue 872. UpsideDown";
    	}
    	
    	@Override
    	public String subtitle()
    	{
    		return "Particles should NOT be Upside Down. M should appear, not W.";
    	}
    }

    static class Issue870 extends ParticleDemo {
    	int index;
    	
    	@Override
    	public void onEnter()
    	{
    		super.onEnter();

    		setColor(ccColor3B.ccBLACK);
    		removeChild(background, true);
    		background = null;

    		CCQuadParticleSystem system = (CCQuadParticleSystem) CCQuadParticleSystem.particleWithFile("Particles/SpinningPeas.plist");

    		system.setTexture(CCTextureCache.sharedTextureCache().addImage("particles.png"), CGRect.make(0,0,32,32));
    		addChild(system, 10);

    		emitter = system;

    		index = 0;

    		schedule("updateQuads", 2);
    	}

    	public void updateQuads(float dt) {
    		index = (index + 1) % 4;
    		CGRect rect = CGRect.make(index*32, 0,32,32);

    		CCQuadParticleSystem system = (CCQuadParticleSystem) emitter;
    		system.setTexture(emitter.getTexture(), rect);
    	}

    	@Override
    	public String title()
    	{
    		return "Issue 870. SubRect";
    	}
    	
    	@Override
    	public String subtitle()
    	{
    		return "Every 2 seconds the particle should change";
    	}
    }
}
