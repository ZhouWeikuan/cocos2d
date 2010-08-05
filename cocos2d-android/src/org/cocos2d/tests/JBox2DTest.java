package org.cocos2d.tests;

import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.AtlasSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.EdgeChainDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/** 
 * A test that demonstrates basic JBox2D integration by using AtlasSprites connected to physics bodies.
 * <br/>
 * <br/>
 * This implementation is based on the original Box2DTest (from cocos2d-iphone) but using the JBox2D
 * library and adjusting for differences with the new API as well as some differences in sensitivity
 * (and such) that were observed when testing on the Android platform.
 * 
 * @author Ray Cardillo
 */
public class JBox2DTest extends Activity {
    // private static final String LOG_TAG = JBox2DTest.class.getSimpleName();
    
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
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);

        Scene scene = Scene.node();
        scene.addChild(new JBox2DTestLayer());

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

    static class JBox2DTestLayer extends CCLayer {
        public static final int kTagSpriteManager = 1;
    	
		// Pixel to meters ratio. Box2D uses meters as the unit for measurement.
		// This ratio defines how many pixels correspond to 1 Box2D "meter"
		// Box2D is optimized for objects of 1x1 meter therefore it makes sense
		// to define the ratio so that your most common object type is 1x1 meter.
        protected static final float PTM_RATIO = 32.0f;
        
        // Simulation space should be larger than window per Box2D recommendation.
        protected static final float BUFFER = 1.0f;
        
        protected final World bxWorld;
        
        public JBox2DTestLayer() {
            this.isTouchEnabled_ = true;
        	this.isAccelerometerEnabled_ = true;

            CGSize s = CCDirector.sharedDirector().winSize();
            float scaledWidth = s.width/PTM_RATIO;
            float scaledHeight = s.height/PTM_RATIO;

        	Vec2 lower = new Vec2(-BUFFER, -BUFFER);
        	Vec2 upper = new Vec2(scaledWidth+BUFFER, scaledHeight+BUFFER);
        	Vec2 gravity = new Vec2(0.0f, -10.0f);
        	
        	bxWorld = new World(new AABB(lower, upper), gravity, true);
                    	
        	// TODO: JBox2D debug drawing is a bit different now.  We could add debug drawing the old way, but looks like that is going away soon.
        	/*
        	GLESDebugDraw debugDraw = new GLESDebugDraw( PTM_RATIO );        	
            bxWorld.setDebugDraw(debugDraw);

            int flags = 0;
            flags |= DebugDraw.e_shapeBit;
            flags |= DebugDraw.e_jointBit;
            flags |= DebugDraw.e_aabbBit;
            flags |= DebugDraw.e_pairBit;
            flags |= DebugDraw.e_centerOfMassBit;
            debugDraw.setFlags(flags);
            */
            
            BodyDef bxGroundBodyDef = new BodyDef();
            bxGroundBodyDef.position.set(0.0f, 0.0f);
            
            Body bxGroundBody = bxWorld.createBody(bxGroundBodyDef);

            EdgeChainDef bxGroundOutsideEdgeDef = new EdgeChainDef();
            bxGroundOutsideEdgeDef.addVertex(new Vec2(0f,0f));
            bxGroundOutsideEdgeDef.addVertex(new Vec2(0f,scaledHeight));
            bxGroundOutsideEdgeDef.addVertex(new Vec2(scaledWidth,scaledHeight));
            bxGroundOutsideEdgeDef.addVertex(new Vec2(scaledWidth,0f));            
            bxGroundOutsideEdgeDef.addVertex(new Vec2(0f,0f));
            bxGroundBody.createShape(bxGroundOutsideEdgeDef);
            
            CCSpriteFrameCache mgr = new CCSpriteFrameCache("blocks.png", 150);
            addChild(mgr, 0, kTagSpriteManager);

            addNewSpriteWithCoords(CGPoint.ccp(s.width / 2.0f, s.height / 2.0f));
            
            CCLabel label = CCLabel.makeLabel("Tap screen", "DroidSans", 32);
            label.setPosition(CGPoint.make(s.width / 2f, s.height - 50f));
            label.setColor(new ccColor3B(0, 0, 255));
            addChild(label);
        }

		@Override
		public void onEnter() {
			super.onEnter();
			
			// start ticking (for physics simulation)
			schedule("tick");
		}

		@Override
		public void onExit() {
			super.onExit();
			
			// stop ticking (for physics simulation)			
			unschedule("tick");
		}

		private void addNewSpriteWithCoords(CGPoint pos) {
            CCSpriteFrameCache mgr = (CCSpriteFrameCache) getChild(kTagSpriteManager);

        	// We have a 64x64 sprite sheet with 4 different 32x32 images.  The following code is
        	// just randomly picking one of the images
        	int idx = (ccMacros.CCRANDOM_0_1() > 0.5f ? 0:1);
        	int idy = (ccMacros.CCRANDOM_0_1() > 0.5f ? 0:1);
            AtlasSprite sprite = AtlasSprite.sprite(CGRect.make(idx * 32f, idy * 32f, 32f, 32f), mgr);
            mgr.addChild(sprite);

            sprite.setPosition(pos);

        	// Define the dynamic body.
        	// Set up a 1m squared box in the physics world
        	BodyDef bxSpriteBodyDef = new BodyDef();
        	bxSpriteBodyDef.userData = sprite;
        	bxSpriteBodyDef.position.set(pos.x/PTM_RATIO, pos.y/PTM_RATIO);
        	bxSpriteBodyDef.linearDamping = 0.3f;
        	
        	// Define another box shape for our dynamic body.
        	PolygonDef bxSpritePolygonDef = new PolygonDef();
        	bxSpritePolygonDef.setAsBox(0.5f, 0.5f);
        	bxSpritePolygonDef.density = 1.0f;
        	bxSpritePolygonDef.friction = 0.3f;

        	synchronized (bxWorld) {
        		// Define the dynamic body fixture and set mass so it's dynamic.
        		Body bxSpriteBody = bxWorld.createBody(bxSpriteBodyDef);
	    		bxSpriteBody.createShape(bxSpritePolygonDef);
	    		bxSpriteBody.setMassFromShapes();
        	}
        }

        public synchronized void tick(float delta) {
        	// It is recommended that a fixed time step is used with Box2D for stability
        	// of the simulation, however, we are using a variable time step here.
        	// You need to make an informed choice, the following URL is useful
        	// http://gafferongames.com/game-physics/fix-your-timestep/
        	
        	// Instruct the world to perform a simulation step. It is
        	// generally best to keep the time step and iterations fixed.
        	synchronized (bxWorld) {
        		bxWorld.step(delta, 10);
        	}
	        	
        	// Iterate over the bodies in the physics world
        	for (Body b = bxWorld.getBodyList(); b != null; b = b.getNext()) {
        		Object userData = b.getUserData();
        		
        		if (userData != null && userData instanceof AtlasSprite) {
        			// Synchronize the AtlasSprite position and rotation with the corresponding body
        			AtlasSprite sprite = (AtlasSprite)userData;
        			sprite.setPosition(CGPoint.make(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO));
        			sprite.setRotation(-1.0f * ccMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));
        		}	
        	}
        }
        
        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint location = CCDirector.sharedDirector().convertCoordinate(event.getX(), event.getY());

            addNewSpriteWithCoords(location);
 
            return CCTouchDispatcher.kEventHandled;
        }

		@Override
	    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
			// no filtering being done in this demo (just magnify the gravity a bit)
			Vec2 gravity = new Vec2( accelX * -2.00f, accelY * -2.00f );
			bxWorld.setGravity( gravity );			
		}
    }
}
