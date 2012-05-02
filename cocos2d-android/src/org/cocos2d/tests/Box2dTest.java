package org.cocos2d.tests;

import java.util.Iterator;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

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
// Box2dTest, there is a downloadable demo here:
//   http://code.google.com/p/cocos2d-android-1/downloads/detail?name=cocos2d%20%20and%20jbox2d.3gp&can=2&q=#makechanges
//
public class Box2dTest extends Activity {
    // private static final String LOG_TAG = JBox2DTest.class.getSimpleName();
    
	static {
        System.loadLibrary("gdx");
	}
	
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
		CCDirector director = CCDirector.sharedDirector();
		director.attachInView(mGLSurfaceView);
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		setContentView(mGLSurfaceView);
		
        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);

        CCScene scene = CCScene.node();
        scene.addChild(new Box2DTestLayer());

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


    //
    // Demo of calling integrating Box2D physics engine with cocos2d sprites
    // a cocos2d example
    // http://code.google.com/p/cocos2d-iphone
    //
    // by Steve Oldmeadow
    //
    static class Box2DTestLayer extends CCLayer {
        public static final int kTagTileMap = 1;
        public static final int kTagSpriteManager = 1;
        public static final int kTagAnimation1 = 1;

		// Pixel to meters ratio. Box2D uses meters as the unit for measurement.
		// This ratio defines how many pixels correspond to 1 Box2D "meter"
		// Box2D is optimized for objects of 1x1 meter therefore it makes sense
		// to define the ratio so that your most common object type is 1x1 meter.
        protected static final float PTM_RATIO = 32.0f;
        
        // Simulation space should be larger than window per Box2D recommendation.
        protected static final float BUFFER = 1.0f;
        
        //FPS for the PhysicsWorld to sync to
        protected static final float FPS = (float)CCDirector.sharedDirector().getAnimationInterval();
        private static float rdelta = 0;
        
        protected final World bxWorld;
        
        public Box2DTestLayer() {
        	super();
        	
            this.setIsTouchEnabled(true);
        	this.setIsAccelerometerEnabled(true);
            
        	CGSize s = CCDirector.sharedDirector().winSize();

      		// Define the gravity vector.
        	Vector2 gravity = new Vector2(9.8f, -9.8f);

        	float scaledWidth = s.width/PTM_RATIO;
            float scaledHeight = s.height/PTM_RATIO;

//        	Vector2 lower = new Vector2(-BUFFER, -BUFFER);
//        	Vector2 upper = new Vector2(scaledWidth+BUFFER, scaledHeight+BUFFER);
        	        	
        	bxWorld = new World(gravity, true);
        	bxWorld.setContinuousPhysics(true);

    		// Define the ground body.
            BodyDef bxGroundBodyDef = new BodyDef();
            bxGroundBodyDef.position.set(0.0f, 0.0f);
    		
    		// Call the body factory which allocates memory for the ground body
    		// from a pool and creates the ground box shape (also from a pool).
    		// The body is also added to the world.
            Body groundBody = bxWorld.createBody(bxGroundBodyDef);

         // Define the ground box shape.
            EdgeShape groundBox = new EdgeShape();

            Vector2 bottomLeft = new Vector2(0f,0f);
            Vector2 topLeft = new Vector2(0f,scaledHeight);
            Vector2 topRight = new Vector2(scaledWidth,scaledHeight);
            Vector2 bottomRight = new Vector2(scaledWidth,0f);
            
    		// bottom
    		groundBox.set( bottomLeft, bottomRight );
    		groundBody.createFixture(groundBox,0);
    		
    		// top
    		groundBox.set( topLeft, topRight );
    		groundBody.createFixture(groundBox,0);
    		
    		// left
    		groundBox.set( topLeft, bottomLeft );
    		groundBody.createFixture(groundBox,0);
    		
    		// right
    		groundBox.set( topRight, bottomRight );
    		groundBody.createFixture(groundBox,0);
                                    
            //Set up sprite
             CCSpriteSheet mgr = CCSpriteSheet.spriteSheet("blocks.png", 150);
             addChild(mgr, 0, kTagSpriteManager);
    		
            addNewSpriteWithCoords(CGPoint.ccp(s.width / 2.0f, s.height / 2.0f));
            
            CCLabel label = CCLabel.makeLabel("Tap screen", "DroidSans", 32);
            label.setPosition(CGPoint.make(s.width / 2f, s.height - 50f));
            label.setColor(new ccColor3B(0, 0, 255));
            addChild(label);
        }

        private UpdateCallback tickCallback = new UpdateCallback() {
			
			@Override
			public void update(float d) {
				tick(d);
			}
		};
        
		@Override
		public void onEnter() {
			super.onEnter();
			
			// start ticking (for physics simulation)
			schedule(tickCallback);
		}

		@Override
		public void onExit() {
			super.onExit();
			
			// stop ticking (for physics simulation)			
			unschedule(tickCallback);
		}

		private void addNewSpriteWithCoords(CGPoint pos) {
      		CCSpriteSheet sheet = (CCSpriteSheet) getChildByTag(kTagSpriteManager);

    		//We have a 64x64 sprite sheet with 4 different 32x32 images.  The following code is
    		//just randomly picking one of the images
    		int idx = (ccMacros.CCRANDOM_0_1() > .5 ? 0:1);
    		int idy = (ccMacros.CCRANDOM_0_1() > .5 ? 0:1);
   
//    		CCSprite sprite = CCSprite.sprite("blocks.png", CGRect.make(32 * idx,32 * idy,32,32));
//    		this.addChild(sprite);
    		CCSprite sprite = CCSprite.sprite(sheet, CGRect.make(32 * idx,32 * idy,32,32));
    		sheet.addChild(sprite);
    		
    		sprite.setPosition(pos);    		

    		// Define the dynamic body.
    		//Set up a 1m squared box in the physics world
    		BodyDef bodyDef = new BodyDef();
    		bodyDef.type = BodyType.DynamicBody;
    		bodyDef.position.set(pos.x/PTM_RATIO, pos.y/PTM_RATIO);
    		
    		// Define another box shape for our dynamic body.
    		PolygonShape dynamicBox = new PolygonShape();
    		dynamicBox.setAsBox(.5f, .5f);//These are mid points for our 1m box
//    		dynamicBox.density = 1.0f;
//            dynamicBox.friction = 0.3f;
    		
        	synchronized (bxWorld) {
        		// Define the dynamic body fixture and set mass so it's dynamic.
        		Body body = bxWorld.createBody(bodyDef);
        		body.setUserData(sprite);
        		
        		FixtureDef fixtureDef = new FixtureDef();
        		fixtureDef.shape = dynamicBox;	
        		fixtureDef.density = 1.0f;
        		fixtureDef.friction = 0.3f;
        		body.createFixture(fixtureDef);
        	}        	
        }
		

        public synchronized void tick(float delta) {
        	if ((rdelta += delta) < FPS) return;
        	
        	// It is recommended that a fixed time step is used with Box2D for stability
        	// of the simulation, however, we are using a variable time step here.
        	// You need to make an informed choice, the following URL is useful
        	// http://gafferongames.com/game-physics/fix-your-timestep/
        	
        	// Instruct the world to perform a simulation step. It is
        	// generally best to keep the time step and iterations fixed.
        	synchronized (bxWorld) {
        		bxWorld.step(FPS, 8, 1);
        	}
	        	
        	rdelta = 0;
        	
        	// Iterate over the bodies in the physics world
        	Iterator<Body> it = bxWorld.getBodies();
        	while(it.hasNext()) {
        		Body b = it.next();
        		Object userData = b.getUserData();
        		
        		if (userData != null && userData instanceof CCSprite) {
        			//Synchronize the Sprites position and rotation with the corresponding body
        			final CCSprite sprite = (CCSprite)userData;
        			final Vector2 pos = b.getPosition();
        			sprite.setPosition(pos.x * PTM_RATIO, pos.y * PTM_RATIO);
        			sprite.setRotation(-1.0f * ccMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));
        		}	
        	}
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint location = CCDirector.sharedDirector()
            	.convertToGL(CGPoint.make(event.getX(), event.getY()));

            addNewSpriteWithCoords(location);
 
            return CCTouchDispatcher.kEventHandled;
        }

        static float prevX=0, prevY=0;
        
        Vector2 gravity = new Vector2();
        
		@Override
	    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {

			//#define kFilterFactor 0.05f
			float kFilterFactor  = 1.0f;	// don't use filter. the code is here just as an example

			float accX = (float) accelX * kFilterFactor + (1- kFilterFactor)* prevX;
			float accY = (float) accelY * kFilterFactor + (1- kFilterFactor)* prevY;
			
			prevX = accX;
			prevY = accY;		
			
			// no filtering being done in this demo (just magnify the gravity a bit)
			gravity.set( accY * 9.8f, accX * -9.8f );
			bxWorld.setGravity( gravity );			
		}
		
    }
}
