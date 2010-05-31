package org.cocos2d.tests;

import org.cocos2d.layers.Layer;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.nodes.*;
import org.cocos2d.types.*;
import org.cocos2d.events.TouchDispatcher;
import org.cocos2d.box2d.GLESDebugDraw;
import org.jbox2d.common.Vec2;

import org.box2d.dynamics.BBWorld;
import org.box2d.collision.shapes.*;
import org.box2d.dynamics.BBBody;
import static org.box2d.dynamics.BBBody.*;
import static org.box2d.dynamics.BBWorldCallbacks.*;
import static org.box2d.dynamics.BBFixture.*;
import org.box2d.common.BBVec2;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.app.Activity;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;

public class Box2DTest extends Activity {
    // private static final String LOG_TAG = ClickAndMoveTest.class.getSimpleName();

    // private static final boolean DEBUG = true;

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
        Director.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        Director.sharedDirector().setLandscape(false);

        // show FPS
        Director.sharedDirector().setDisplayFPS(true);

        // frames per second
        Director.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = Scene.node();
        scene.addChild(new Box2DTestLayer(), 0);

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


    static class Box2DTestLayer extends Layer {
        BBWorld world;
        GLESDebugDraw m_debugDraw;

        //Pixel to metres ratio. Box2D uses metres as the unit for measurement.
        //This ratio defines how many pixels correspond to 1 Box2D "metre"
        //Box2D is optimized for objects of 1x1 metre therefore it makes sense
        //to define the ratio so that your most common object type is 1x1 metre.
        static final int PTM_RATIO = 32;

            static final int kTagTileMap = 1;
            static final int kTagSpriteManager = 1;
            static final int kTagAnimation1 = 1;

        public Box2DTestLayer()
        {

            setIsTouchEnabled(true);
    //		setIsAccelerometerEnabled = true;

            CCSize screenSize = Director.sharedDirector().winSize();
    //		Log.w("Screen width %0.2f screen height %0.2f", screenSize.width,screenSize.height);

            // Define the gravity vector.
            BBVec2 gravity = new BBVec2();
            gravity.set(0.0f, -10.0f);

            // Do we want to let bodies sleep?
            boolean doSleep = true;

            // Construct a world object, which will hold and simulate the rigid bodies.
            world = new BBWorld(gravity, doSleep);

//            world.setContinuousPhysics(true); MISSING?

            m_debugDraw = new GLESDebugDraw( PTM_RATIO );
            world.SetDebugDraw(m_debugDraw);

            int flags = 0;
            flags += BBDebugDraw.e_shapeBit;
            flags += BBDebugDraw.e_jointBit;
            flags += BBDebugDraw.e_aabbBit;
            flags += BBDebugDraw.e_pairBit;
            flags += BBDebugDraw.e_centerOfMassBit;
            m_debugDraw.setFlags(flags);


            // Define the ground body.
            BBBodyDef groundBodyDef = new BBBodyDef();
            groundBodyDef.position.set(0, 0); // bottom-left corner

            // Call the body factory which allocates memory for the ground body
            // from a pool and creates the ground box shape (also from a pool).
            // The body is also added to the world.
            BBBody groundBody = world.createBody(groundBodyDef);

            // Define the ground box shape.
            BBPolygonShape groundBox = new BBPolygonShape();

            // bottom
            groundBox.setAsEdge(new BBVec2(0,0), new BBVec2(screenSize.width/PTM_RATIO,0));
            groundBody.createFixture(groundBox);

            // top
            groundBox.setAsEdge(new BBVec2(0,screenSize.height/PTM_RATIO), new BBVec2(screenSize.width/PTM_RATIO, screenSize.height/PTM_RATIO));
            groundBody.createFixture(groundBox);

            // left
            groundBox.setAsEdge(new BBVec2(0,screenSize.height/PTM_RATIO), new BBVec2(0,0));
            groundBody.createFixture(groundBox);

            // right
            groundBox.setAsEdge(new BBVec2(screenSize.width/PTM_RATIO,screenSize.height/PTM_RATIO), new BBVec2(screenSize.width/PTM_RATIO,0));
            groundBody.createFixture(groundBox);


            //set up sprite

            AtlasSpriteManager mgr = new AtlasSpriteManager("blocks.png", 150);
            addChild(mgr, 0, kTagSpriteManager);

            addNewSprite(screenSize.width/2, screenSize.height/2);

            Label label = Label.label("Tap screen", "DroidSans", 32);
            addChild(label, 0);
            label.setColor(new CCColor3B(0,0,255));
            label.setPosition( screenSize.width/2, screenSize.height-50);

            schedule("tick");
        }

        @Override
        public void draw(GL10 gl) {
            super.draw(gl);
            gl.glEnableClientState(GL_VERTEX_ARRAY);
            world.DrawDebugData(gl);
            gl.glDisableClientState(GL_VERTEX_ARRAY);
        }

        private void addNewSprite(float x, float y)
        {
            //Log.w("Add sprite %0.2f x %02.f", x, y);
            AtlasSpriteManager mgr = (AtlasSpriteManager)getChild(kTagSpriteManager);

            //We have a 64x64 sprite sheet with 4 different 32x32 images.  The following code is
            //just randomly picking one of the images
            int idx = (CCMacros.CCRANDOM_0_1() > .5 ? 0:1);
            int idy = (CCMacros.CCRANDOM_0_1() > .5 ? 0:1);
            AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(32 * idx,32 * idy,32,32), mgr);
            mgr.addChild(sprite);

            sprite.setPosition( x, y);

            // Define the dynamic body.
            //set up a 1m squared box in the physics world
            BBBodyDef bodyDef = new BBBodyDef();
            bodyDef.position.set(x/PTM_RATIO, y/PTM_RATIO);
            bodyDef.userData = sprite;
            BBBody body = world.createBody(bodyDef);

            // Define another box shape for our dynamic body.
            BBPolygonShape dynamicBox = new BBPolygonShape();
            dynamicBox.setAsBox(.5f, .5f);//These are mid points for our 1m box

            // Define the dynamic body fixture.
            BBFixtureDef fixtureDef = new BBFixtureDef();
            fixtureDef.shape = dynamicBox;
            fixtureDef.density = 1.0f;
            fixtureDef.friction = 0.3f;
            body.createFixture(fixtureDef);
        }

        public void tick(float dt)
        {
            //It is recommended that a fixed time step is used with Box2D for stability
            //of the simulation, however, we are using a variable time step here.
            //You need to make an informed choice, the following URL is useful
            //http://gafferongames.com/game-physics/fix-your-timestep/

            int velocityIterations = 8;
            int positionIterations = 1;

            // Instruct the world to perform a single step of simulation. It is
            // generally best to keep the time step and iterations fixed.
            world.Step(dt, velocityIterations, positionIterations);

            //Iterate over the bodies in the physics world
            for (BBBody b = world.GetBodyList(); b != null; b = b.getNext())
            {
                if (b.getUserData() != null) {
                    //synchronize the AtlasSprites position and rotation with the corresponding body
                    AtlasSprite myActor = (AtlasSprite)b.getUserData();
                    myActor.setPosition( b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
                    myActor.setRotation(-1 * CCMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));
                }
            }
        }

        public boolean ccTouchesEnded(MotionEvent event)
        {
            //Add a new body/atlas sprite at the touched location
            CCPoint location = CCPoint.make(event.getX(), event.getY());

            location = Director.sharedDirector().convertCoordinate(location.x, location.y);

            addNewSprite(location.x, location.y);
            return TouchDispatcher.kEventHandled;
        }

		@Override
	    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
			// no filtering being done in this demo (just magnify the gravity a bit)
			BBVec2 gravity = new BBVec2( accelX * -2.00f, accelY * -2.00f );
			world.SetGravity(gravity);			
		}
    }

}
