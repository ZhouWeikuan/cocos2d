package org.cocos2d.nodes;

import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_COLOR_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_DITHER;
import static javax.microedition.khronos.opengles.GL10.GL_FASTEST;
import static javax.microedition.khronos.opengles.GL10.GL_LEQUAL;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_NICEST;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.microedition.khronos.opengles.GL10.GL_PROJECTION;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCKeyDispatcher;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCLabel.TextAlignment;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.opengl.GLSurfaceView;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.util.CGPointUtil;
import org.cocos2d.utils.CCFormatter;
import org.cocos2d.utils.javolution.TextBuilder;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**Class that creates and handle the main Window and manages how
and when to execute the Scenes.
 
 The CCDirector is also resposible for:
  - initializing the OpenGL ES context
  - setting the OpenGL ES pixel format (default on is RGB565)
  - setting the OpenGL ES buffer depth (default one is 0-bit)
  - setting the projection (default one is 2D)
  - setting the orientation (default one is Protrait)
 
 Since the CCDirector is a singleton, the standard way to use it is by calling:
  - [[CCDirector sharedDirector] xxxx];
 
 The CCDirector also sets the default OpenGL ES context:
  - GL_TEXTURE_2D is enabled
  - GL_VERTEX_ARRAY is enabled
  - GL_COLOR_ARRAY is enabled
  - GL_TEXTURE_COORD_ARRAY is enabled
*/
public class CCDirector implements GLSurfaceView.Renderer {
	private static final String LOG_TAG = CCDirector.class.getSimpleName();
	
    /** @typedef tPixelFormat
      Possible Pixel Formats for the EAGLView
      */
    /** RGB565 pixel format. No alpha. 16-bit. (Default) */
    public static final int kCCPixelFormatRGB565 = 0;
    /** RGBA format. 32-bit. Needed for some 3D effects. It is not as fast as the RGB565 format. */
    public static final int kCCPixelFormatRGBA8888 = 1;
    /** default pixel format */
    public static final int kCCPixelFormatDefault = kCCPixelFormatRGB565;

    /** @typedef tDepthBufferFormat
      Possible DepthBuffer Formats for the EAGLView.
      Use 16 or 24 bit depth buffers if you are going to use real 3D objects.
      */
    /// A Depth Buffer of 0 bits will be used (default)
    public static final int kCCDepthBufferNone = 0;
    /// A depth buffer of 16 bits will be used
    public static final int kCCDepthBuffer16 = 1;
    /// A depth buffer of 24 bits will be used
    public static final int kCCDepthBuffer24 = 2;

    /** @typedef ccDirectorProjection
      Possible OpenGL projections used by director
      */
    /// sets a 2D projection (orthogonal projection)
    public static final int kCCDirectorProjection2D = 1;

    /// sets a 3D projection with a fovy=60, znear=0.5f and zfar=1500.
    public static final int kCCDirectorProjection3D = 2;

    /// it does nothing. But if you are using a custom projection set it this value.
    public static final int kCCDirectorProjectionCustom = 3;

    /// Detault projection is 3D projection
    public static final int kCCDirectorProjectionDefault = kCCDirectorProjection3D;

    /** Sets an OpenGL projection
      @since v0.8.2
      */
    private int projection_ = kCCDirectorProjectionDefault;

    public int getProjection() {
        return projection_;
    }

    public void setProjection(int p) {
        CGSize size = screenSize_;
        switch (p) {
            case kCCDirectorProjection2D:
                gl.glMatrixMode(GL_PROJECTION);
                gl.glLoadIdentity();
                gl.glOrthof(0, size.width, 0, size.height, -1000, 1000);
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity();			
                break;

            case kCCDirectorProjection3D:
//                gl.glViewport(0, 0, (int)size.width, (int)size.height);
                gl.glMatrixMode(GL_PROJECTION);
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, 60, size.width/size.height, 0.5f, 1500.0f);
                
                gl.glMatrixMode(GL_MODELVIEW);	
                gl.glLoadIdentity();
                GLU.gluLookAt(gl, size.width/2, size.height/2, getZEye(),
                          size.width/2, size.height/2, 0,
                          0.0f, 1.0f, 0.0f);			
                break;
                
            case kCCDirectorProjectionCustom:
                // if custom, ignore it. The user is resposible for setting the correct projection
                break;
                
            default:
            	ccMacros.CCLOG(LOG_TAG, "cocos2d: Director: unrecognized projecgtion");
                break;
        }
        projection_ = p;
    }
   

    /** @typedef ccDirectorType
      Possible Director Types.
      @since v0.8.2
      */
    /** Will use a Director that triggers the main loop from an NSTimer object
     *
     * Features and Limitations:
     * - Integrates OK with UIKit objects
     * - It the slowest director
     * - The invertal update is customizable from 1 to 60
     */
    public static final int kCCDirectorTypeNSTimer = 1;

    /** will use a Director that triggers the main loop from a custom main loop.
     *
     * Features and Limitations:
     * - Faster than NSTimer Director
     * - It doesn't integrate well with UIKit objecgts
     * - The interval update can't be customizable
     */
    public static final int kCCDirectorTypeMainLoop = 2;

    /** Will use a Director that triggers the main loop from a thread, but the main loop will be executed on the main thread.
     *
     * Features and Limitations:
     * - Faster than NSTimer Director
     * - It doesn't integrate well with UIKit objecgts
     * - The interval update can't be customizable
     */
    public static final int kCCDirectorTypeThreadMainLoop = 3;

    /** Will use a Director that synchronizes timers with the refresh rate of the display.
     *
     * Features and Limitations:
     * - Faster than NSTimer Director
     * - Only available on 3.1+
     * - Scheduled timers and drawing are synchronizes with the refresh rate of the display
     * - Integrates OK with UIKit objects
     * - The interval update can be 1/60, 1/30, 1/15
     */	
    public static final int kCCDirectorTypeDisplayLink = 4;

    /** Default director is the NSTimer directory */
    public static final int kCCDirectorTypeDefault = kCCDirectorTypeNSTimer;

    /** @typedef ccDeviceOrientation
      Possible device orientations
      */
        /// Device oriented vertically, home button on the bottom
    public static final int kCCDeviceOrientationPortrait = Configuration.ORIENTATION_PORTRAIT;

    /// Device oriented vertically, home button on the top
    ///    kCCDeviceOrientationPortraitUpsideDown = UIDeviceOrientationPortraitUpsideDown,

        /// Device oriented horizontally, home button on the right
    public static final int kCCDeviceOrientationLandscapeLeft = Configuration.ORIENTATION_LANDSCAPE;

    /// Device oriented horizontally, home button on the left
    ///    kCCDeviceOrientationLandscapeRight = UIDeviceOrientationLandscapeRight;

    public static Activity theApp = null;

    public Activity getActivity() {
        return theApp;
    }

    // 60 frames per second
    private static final double kDefaultFPS = 60.0f;

    // Landscape is right or left ?
    // private static final boolean LANDSCAPE_LEFT = false;

    // Fast FPS display. FPS are updated 10 times per second without consuming resources
    // uncomment this line to use the old method that updated
    private static final boolean FAST_FPS_DISPLAY = true;


    private int depthBufferFormat_;
    /** Change depth buffer format of the render buffer.
      Call this class method before attaching it to a UIWindow/UIView
      Default depth buffer: 0 (none).  Supported: kCCDepthBufferNone, kCCDepthBuffer16, and kCCDepthBuffer24

      @deprecated Set the depth buffer format when creating the EAGLView. This method will be removed in v1.0
      */
    public void setDepthBufferFormat(int db) {
	    assert (!isOpenGLAttached()):"Can't change the depth buffer format after the director was initialized";
        depthBufferFormat_ = db;
    }

    /** Pixel format used to create the context */
    private int pixelFormat_;

    public int getPixelFormat() {
        return pixelFormat_;
    }

    /// XXX: missing description
    public float getZEye () {
	    return ( screenSize_.height / 1.1566f );
    }

    /** Uses a new pixel format for the EAGLView.
      Call this class method before attaching it to a UIView
      Default pixel format: kRGB565. Supported pixel formats: kRGBA8 and kRGB565

      @deprecated Set the pixel format when creating the EAGLView. This method will be removed in v1.0
    */
    @Deprecated
    public void setPixelFormat(int p) {
	    assert (!this.isOpenGLAttached()):"Can't change the pixel format after the director was initialized";	
        pixelFormat_ = p;
    }

    /* orientation */
    int	deviceOrientation_;

    /** The device orientattion */
    public int getDeviceOrientation() {
        return deviceOrientation_;
    }

    public void setDeviceOrientation(int orientation) {
        if( deviceOrientation_ != orientation ) {
            deviceOrientation_ = orientation;
            switch( deviceOrientation_) {
                case kCCDeviceOrientationPortrait:
                    theApp.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case kCCDeviceOrientationLandscapeLeft:
                    theApp.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                default:
                    Log.w(LOG_TAG, "Director: Unknown device orientation");
                    break;
            }
        }
    }

    // rotates the screen if an orientation differnent than Portrait is used
    private void applyOrientation(GL10 gl) {
        CGSize s = surfaceSize_;
         float h = s.height / 2;
         float w = s.width / 2;

        // XXX it's using hardcoded values.
        // What if the the screen size changes in the future?
        switch ( deviceOrientation_ ) {
            case kCCDeviceOrientationPortrait:
                // nothing
                break;
            case kCCDeviceOrientationLandscapeLeft:
                // gl.glTranslatef(w, h, 0);
                // gl.glRotatef(-90, 0, 0, 1);
                // gl.glTranslatef(-h, -w, 0);
                break;
        }
    }

    /* display FPS ? */
    private int frames_;
    private float accumDt_;
    private float frameRate_;
	
    CCLabelAtlas FPSLabel_;

    /* is the running scene paused */
    private boolean isPaused;

    /** Whether or not the Director is paused */
    public boolean getIsPaused() {
        return isPaused;
    }

    /* The running CCScene */
    private CCScene runningCCScene_;

    /* will be the next 'runningCCScene' in the next frame */
    private CCScene nextCCScene_;

	/* If YES, then "old" CCScene will receive the cleanup message */
    private boolean	sendCleanupToCCScene_;

    /** Whether or not the replaced CCScene will receive the cleanup message.
      If the new CCScene is pushed, then the old CCScene won't receive the "cleanup" message.
      If the new CCScene replaces the old one, the it will receive the "cleanup" message.
      @since v0.99.0
    */
    public boolean getSendCleanupToScene() {
        return sendCleanupToCCScene_;
    }

    /* scheduled CCScenes */
    private ArrayList<CCScene> CCScenesStack_;

    /* last time the main loop was updated */
    private long lastUpdate_;

    /* delta time since last tick to main loop */
    private float dt;

    /* whether or not the next delta time will be zero */
    private boolean nextDeltaTimeZero_;
    public boolean getNextDeltaTimeZero(){
        return nextDeltaTimeZero_;
    }
    public void setNextDeltaTimeZero(boolean dtz) {
        nextDeltaTimeZero_ = dtz;
    }

    /** The EAGLView, where everything is rendered */
    private GLSurfaceView openGLView_;

    /**
     * The OpenGL view
     */
    public GLSurfaceView getOpenGLView() {
        return openGLView_;
    }
/*
    public void setOpenGLView(GLSurfaceView view) {
        assert view!=null: "EAGView must be non-nil";

        if( view != openGLView_ ) {
            openGLView_ = view;
            
            // set size
            screenSize_ = CGSize.make(view.getWidth(), view.getHeight());
            surfaceSize_ = CGSize.make(screenSize_.width * contentScaleFactor_, screenSize_.height *contentScaleFactor_);
            
            CCTouchDispatcher touchDispatcher = CCTouchDispatcher.sharedDispatcher();
            openGLView_.setTouchDelegate(touchDispatcher);
            touchDispatcher.setDispatchEvents(true);

            if( contentScaleFactor_ != 1 )
                updateContentScaleFactor();

            setGLDefaultValues();
        }
    }*/

	/* screen, different than surface size */
	private CGSize	screenSize_;

	/* screen, different than surface size */
	private CGSize	surfaceSize_;
	
	/* content scale factor */
	private float contentScaleFactor_;
    /** The size in pixels of the surface. It could be different than the screen size.
      High-res devices might have a higher surface size than the screen size.
      In non High-res device the contentScale will be emulated.

    Warning: Emulation of High-Res on iOS < 4 is an EXPERIMENTAL feature.

    @since v0.99.4
    */
    public void setContentScaleFactor(GL10 gl, float scaleFactor) {
        if( scaleFactor != contentScaleFactor_ ) {
            contentScaleFactor_ = scaleFactor;
            surfaceSize_ = CGSize.make( screenSize_.width * scaleFactor, screenSize_.height * scaleFactor );

            if( openGLView_!= null )
                this.updateContentScaleFactor();

            // update projection
            setProjection(projection_);
        }
    }

    public float getContentScaleFactor() {
        return contentScaleFactor_;
    }

	/* contentScaleFactor could be simulated */
	private boolean isContentScaleSupported_;

	private float accumDtForProfiler_;

    /**
     * The current running CCScene. Director can only run one CCScene at the time
     */

    public CCScene getRunningScene() {
        return runningCCScene_;
    }

    /**
     * The FPS value
     */
    // internal timer
    private double animationInterval_;
    private double oldAnimationInterval_;
//    private Timer  animationTimer_;

    public double getAnimationInterval() {
        return animationInterval_;
    }

    public void setAnimationInterval(double interval) {
        animationInterval_ = interval;

//        if (animationTimer_ != null) {
//            stopAnimation();
//            startAnimation();
//        }
    }

    /**
     * Whether or not to display the FPS on the bottom-left corner
     */
	/* display FPS ? */
    private boolean displayFPS;

    public void setDisplayFPS(boolean value) {
        displayFPS = value;
    }

    private static CCDirector _sharedDirector = new CCDirector();

    /** returns a shared instance of the director */
    public static CCDirector sharedDirector() {
//        if (_sharedDirector != null)
            return _sharedDirector;

		//
		// Default Director is TimerDirector
		// 
//        synchronized (CCDirector.class) {
//            if (_sharedDirector == null) {
//                _sharedDirector = new CCDirector();
//            }
//            return _sharedDirector;
//        }
    }

    /** There are 4 types of Director.
      - kCCDirectorTypeNSTimer (default)
      - kCCDirectorTypeMainLoop
      - kCCDirectorTypeThreadMainLoop
      - kCCDirectorTypeDisplayLink

      Each Director has it's own benefits, limitations.
      If you are using SDK 3.1 or newer it is recommed to use the DisplayLink director

      This method should be called before any other call to the director.

      It will return NO if the director type is kCCDirectorTypeDisplayLink and the running SDK is < 3.1. Otherwise it will return YES.

      @since v0.8.2
      */
    /*public static boolean setDirectorType(int directorType) {
        assert _sharedDirector==null 
            : "A Director was alloced. setDirectorType must be the first call to Director";

        
        if( type == CCDirectorTypeDisplayLink ) {
            NSString *reqSysVer = @"3.1";
            NSString *currSysVer = [[UIDevice currentDevice] systemVersion];

            if([currSysVer compare:reqSysVer options:NSNumericSearch] == NSOrderedAscending)
                return NO;
        }

	    switch (directorType) {
		case CCDirectorTypeNSTimer:
            CCTimerDirector.sharedDirector();
			break;
		case CCDirectorTypeDisplayLink:
            CCDisplayLinkDirector.sharedDirector();
			break;
		case CCDirectorTypeMainLoop:
            CCFastDirector.sharedDirector();
			break;
		case CCDirectorTypeThreadMainLoop:
            CCThreadedFastDirector.sharedDirector();
			break;
		default:
			assert false: "Unknown director type";
	    }

    	return true;
    }*/


//	public void useFaseDirector(){
//		assert _sharedDirector == null : "A Director was allocated. setDirectorType must be the first call to Director";
//
//		FastDirector.sharedDirector();
//	}

    protected CCDirector() {
	    ccMacros.CCLOG(LOG_TAG, "cocos2d: " + ccConfig.cocos2dVersion);
//        synchronized (CCDirector.class) {
		    ccMacros.CCLOG(LOG_TAG, "cocos2d: Using Director Type:" + this.getClass());

            // default values
            pixelFormat_ = kCCPixelFormatDefault;
            depthBufferFormat_ = 0;

            //Create a full-screen window

            // CCScenes
            runningCCScene_ = null;
            nextCCScene_ = null;
	
            oldAnimationInterval_ = animationInterval_ = 1.0 / kDefaultFPS;
            CCScenesStack_ = new ArrayList<CCScene>(10);

            // landscape
            deviceOrientation_ = kCCDeviceOrientationPortrait;

            // FPS
            displayFPS = false;
            frames_ = 0;

            // paused?
            isPaused = false;

            // for iphone 4?
            contentScaleFactor_ = 1;

            screenSize_  = CGSize.zero();
        	surfaceSize_ = CGSize.zero();
            isContentScaleSupported_ = false;
//        }
    }

    /** sets the OpenGL default values */
    public void setGLDefaultValues (GL10 gl) {
        // This method SHOULD be called only after openGLView_ was initialized
        assert openGLView_!=null:"openGLView_ must be initialized";

        setAlphaBlending(gl, true);
        setDepthTest(gl, false);
//        setProjection(projection_); is called in onSurfaceChanged()

        // set other opengl default values
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        if (ccConfig.CC_DIRECTOR_FAST_FPS) {
            if (FPSLabel_==null) {
                // CCTexture2DPixelFormat currentFormat = CCTexture2D.defaultAlphaPixelFormat();
                // CCTexture2D.setDefaultAlphaPixelFormat(kCCTexture2DPixelFormat_RGBA4444);
                FPSLabel_ = CCLabelAtlas.label("00.0", "fps_images.png", 16, 24, '.');
                // CCTexture2D.setDefaultAlphaPixelFormat(currentFormat);
                FPSLabel_.setPosition(50, 2);
            }
        }	// CC_DIRECTOR_FAST_FPS
    }

    /*
    public void finalize() {
        ccMacros.CCLOG(LOG_TAG, "cocos2d: deallocing " + this);

        if (ccConfig.CC_DIRECTOR_FAST_FPS) {
            FPSLabel_ = null;
        }

        runningCCScene_ = null;
        CCScenesStack_ = null;

        _sharedDirector = null;
    }*/

    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	CCDirector.gl = gl;
    	surfaceSize_.set(width, height);
        gl.glViewport(0, 0, width, height);
        setProjection(projection_);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	CCDirector.gl = gl;

        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL_DITHER);

        /*
        * Some one-time OpenGL initialization can be made here
        * probably based on features of this particular context
        */
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        setGLDefaultValues(gl); 

    	// reload all GL resources here
    	GLResourceHelper.sharedHelper().reloadResources();    	
    }

    public void onDrawFrame(GL10 gl) {
//        synchronized (this) {
//            this.notify();
//        }
//        Thread.yield();
//        
//        synchronized(this) {
//		if (_sharedDirector == null)
//		return;
		
    	GLResourceHelper.sharedHelper().setInUpdate(true);
    	
		CCTouchDispatcher.sharedDispatcher().update();
		//added by Ishaq 
		CCKeyDispatcher.sharedDispatcher().update();
		drawCCScene(gl);
		
		GLResourceHelper.sharedHelper().setInUpdate(false);
		
		waitForFPS();
//        }
    }    

    private double sleepInterval = 0;
    
	private void waitForFPS() {
		if (animationInterval_ >= dt) {
			sleepInterval = animationInterval_ - dt + sleepInterval;
			SystemClock.sleep( (long)(sleepInterval * 1000) );

		} else {
			sleepInterval = 0;
		}
	}

    /** Draw the CCScene.
      This method is called every frame. Don't call it manually.
      */
    public void drawCCScene (GL10 gl) {
    	
        /* calculate "global" dt */
        calculateDeltaTime();
        
        /* tick before glClear: issue #533 */
        if(!isPaused) {
        	CCScheduler.sharedScheduler().tick(dt);
        }

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        GLResourceHelper.sharedHelper().update(gl);
        
        /* to avoid flickr, nextCCScene MUST be here: after tick and before draw.
         XXX: Which bug is this one. It seems that it can't be reproduced with v0.9 */
        if( nextCCScene_ != null)
        	setNextScene();
        
        gl.glPushMatrix();

        applyOrientation(gl);
        
        // By default enable VertexArray, ColorArray, TextureCoordArray and Texture2D
        ccMacros.CC_ENABLE_DEFAULT_GL_STATES(gl);
        
        /* draw the CCScene */
        if(runningCCScene_ != null)
        	runningCCScene_.visit(gl);
        if( displayFPS )
        	showFPS(gl);

        if (ccConfig.CC_ENABLE_PROFILERS) {
        //    showProfilers();
        }
        
        ccMacros.CC_DISABLE_DEFAULT_GL_STATES(gl);
        
        gl.glPopMatrix();
    	
        /* swap buffers */
        // openGLView_.swapBuffers();
    }

    private void calculateDeltaTime() {
        long now = System.currentTimeMillis();

        // new delta time
        if (nextDeltaTimeZero_) {
            dt = 0;
            nextDeltaTimeZero_ = false;
        } else {
            dt = (now - lastUpdate_) * 0.001f;
            dt = Math.max(0, dt);
        }

        lastUpdate_ = now;
    }
    
    /** returns the size of the OpenGL view in pixels, according to the landspace */
    public CGSize winSize() {
        CGSize s = CGSize.make(screenSize_.width, screenSize_.height);
        /*if( deviceOrientation_ == kCCDeviceOrientationLandscapeLeft) {
            // swap x,y in landscape mode
            float t = s.width;
            s.width = s.height;
            s.height = t;
        }*/

        return s;
    }
    
    public CGSize winSizeRef() {
    	return screenSize_;
    }

    /** returns the display size of the OpenGL view in pixels */
    public CGSize displaySize() {
        return CGSize.make(surfaceSize_.width, surfaceSize_.height);
    }
    
    public boolean getLandscape() {
        return deviceOrientation_ == kCCDeviceOrientationLandscapeLeft;
    }

    public void setLandscape(boolean on) {
        if ( on )
            setDeviceOrientation(kCCDeviceOrientationLandscapeLeft);
        else
            setDeviceOrientation(kCCDeviceOrientationPortrait);
    }

    // Director Integration with a Android view
    // is the view currently attached
    public boolean isOpenGLAttached() {
        return true;
        // return openGLView_.getParent() != null;
    }

    /** detach the cocos2d view from the view/window */
    @Deprecated
    private boolean detach() {

        // detach or attach to a view or a window
        assert isOpenGLAttached():
        "FATAL: Director: Can't detach the OpenGL View, because it is not attached. Attach it first.";

       // remove from the superview
       ViewGroup vg = (ViewGroup)openGLView_.getParent();
       vg.removeView(vg);
       
       assert (!isOpenGLAttached()) :
       "FATAL: Director: Can't detach the OpenGL View, it is still attached to the superview.";


       return true;
    }

    /** attach in UIWindow using the full frame.
      It will create a EAGLView.
      @deprecated set setOpenGLView instead. Will be removed in v1.0
      */
    @Deprecated
    public boolean attachInWindow(View window) {
        return attachInView(window);
        /*
        if([self initOpenGLViewWithView:window withFrame:[window bounds]])
        {
            return YES;
        }
        
        return NO;
        */
    }

    /** attach in UIView using the full frame.
      It will create a EAGLView.

      deprecated set setOpenGLView instead. Will be removed in v1.0
    */
    public boolean attachInView(View view) {

//        CCRect rect = new CCRect(view.getScrollX(), view.getScrollY(), view.getWidth(), view.getHeight());
        WindowManager w = theApp.getWindowManager();
        CGRect rect = CGRect.make(0, 0, w.getDefaultDisplay().getWidth(), w.getDefaultDisplay().getHeight());
        // CGRect rect = CGRect.make(view.getLeft(), view.getBottom(), view.getWidth(), view.getHeight());
        
        return initOpenGLViewWithView(view, rect);

        /*
        if([self initOpenGLViewWithView:view withFrame:[view bounds]])
        {
            return YES;
        }
        
        return NO;
        */
    }

    /** attach in UIView using the given frame.
      It will create a EAGLView and use it.

      @deprecated set setOpenGLView instead. Will be removed in v1.0
    */
    @Deprecated
    public boolean attachInView(View view, CGRect rect) {
        return initOpenGLViewWithView(view, rect);

        /*
        if([self initOpenGLViewWithView:view withFrame:frame])
        {
            return YES;
        }
        
        return NO;
        */
    }
    
    /**
     * attach in UIView using the given frame, and ration.
     * ratio = width / height
     * It is the easiest way to port from iPhone ration 480f/320f 
     * to any android device but not the best.
     * It will create a EAGLView and use it.
     * 
     * @param view
     * @param ration
     * @return
     */
    
    public boolean attachInView(View view, float ration)
    {
    	return initOpenGLViewWithView(view, getAppFrameRect(ration));
    }
    
    private CGRect getAppFrameRect(float targetRatio)
    {
    	 WindowManager w = theApp.getWindowManager();
    	 CGSize size = CGSize.make(w.getDefaultDisplay().getWidth(), w.getDefaultDisplay().getHeight());
         
         float currentRation = size.width / size.height;
         CGSize newSize = CGSize.make(size.width, size.height);
         CGPoint offset = CGPoint.make(0, 0);
         
         if (currentRation > targetRatio)
         {
        	 newSize.width = targetRatio * size.height;
        	 offset.x = (size.width - newSize.width) / 2;
         }
         	else if (currentRation < targetRatio)
         {
         		newSize.height = size.width / targetRatio;
         		offset.y = (size.height - newSize.height) / 2;
         }
         
         CGRect rect = CGRect.make(offset, newSize);
         
         return rect;
    }
    
	public void setScreenSize(float width, float height) {
		screenSize_.set(width, height);
	}
	
    private boolean initOpenGLViewWithView(View view, CGRect rect) {
        surfaceSize_.set(rect.size);
        screenSize_.set(surfaceSize_);
        
//        try {
        if (openGLView_ != view) {
        	openGLView_ = (GLSurfaceView) view;
        	openGLView_.setRenderer(this);
//        	openGLView_.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
//
//            // check if the view is not attached
//            if(isOpenGLAttached())
//            {
//                // the view is already attached
//                throw new OpenGLViewAlreadyAttached("Can't re-attach the OpenGL View, because it is already attached. Detach it first.");
//                return false;
//            }
//
//            // check if the view is not initialized
        if (openGLView_ != null) {
//                openGLView_.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

//                // define the pixel format
//                NSString	*pFormat = kEAGLColorFormatRGB565;
//                GLuint		depthFormat = 0;
//
//                if(pixelFormat_==kRGBA8)
//                    pFormat = kEAGLColorFormatRGBA8;
//
//                // alloc and init the opengl view
//                openGLView_ = [[EAGLView alloc] initWithFrame:rect pixelFormat:pFormat depthFormat:depthFormat preserveBackbuffer:NO];
//
//                // check if the view was alloced and initialized
//                if(openGLView_ == null)
//                {
//                    // the view was not created
//                    throw new OpenGLViewCantInit("Could not alloc and init the OpenGL View.");
//                }
//
//                // set autoresizing enabled when attaching the glview to another view
//                openGLView_.setAutoresizesEAGLSurface(true);
//
//                // set the touch delegate of the glview to self
//                openGLView_.setTouchDelegate(this);
        } else {
//                // set the (new) frame of the glview
//                openGLView_.setFrame(rect);
        }
//
//            // check if the superview has touchs enabled and enable it in our view
//            if([view isUserInteractionEnabled])
//            {
//                [openGLView_ setUserInteractionEnabled:YES];

        CCTouchDispatcher.sharedDispatcher().setDispatchEvents(true);

//            }
//            else
//            {
//                [openGLView_ setUserInteractionEnabled:NO];
//                setEventsEnabled(false);
//            }
//
//            // check if multi touches are enabled and set them
//            if([view isMultipleTouchEnabled])
//            {
//                [openGLView_ setMultipleTouchEnabled:YES];
//            }
//            else
//            {
//                [openGLView_ setMultipleTouchEnabled:NO];
//            }
//
//            // add the glview to his (new) superview
//            [view.addSubview(openGLView_);
//
//            // set the background color of the glview
//            //	[backgroundColor setOpenGLClearColor];
//
//            // check if the glview is attached now
//            if(isOpenGLAttached())
//            {
//                initGLDefaultValues();
//                return true;
//            }
//
//            // the glview is not attached, but it should have been
//            throw new OpenGLViewCantAttach("Can't attach the OpenGL View.");
//
//        } catch (Exception e) {
//        }
        return true;
    }

    public void showProfilers() {
        accumDtForProfiler_ += dt;
        if (accumDtForProfiler_ > 1.0f) {
            accumDtForProfiler_ = 0;
            // CCProfiler.sharedProfiler().displayTimers();
        }
    }

    /** converts a UIKit coordinate to an OpenGL coordinate
      Useful to convert (multi) touchs coordinates to the current layout (portrait or landscape)
      */
    public CGPoint convertToGL(CGPoint uiPoint) {
    	float newX = uiPoint.x / surfaceSize_.width * screenSize_.width;
        float newY = screenSize_.height - uiPoint.y / surfaceSize_.height * screenSize_.height;
        
        CGPoint ret = null;
        switch (deviceOrientation_) {
            case kCCDeviceOrientationPortrait:
                ret = CGPoint.ccp(newX, newY);
            	//ret = CGPoint.ccp(newY, newX);
                break;

            case kCCDeviceOrientationLandscapeLeft:
                // ret = CGPoint.ccp(uiPoint.y, uiPoint.x);
            	ret = CGPoint.ccp(newX, newY);
                break;

            default:
                return ret;
        }

	    if (contentScaleFactor_ != 1 && isContentScaleSupported_ )
		    ret = CGPoint.ccpMult(ret, contentScaleFactor_);
        return ret;
    }
    
    /**
     * Optimizaed version of convertToGL(CGPoint uiPoint).
     * @param uiPoint - point for conversion
     * @param ret - result point
     */
    public void convertToGL(CGPoint uiPoint, CGPoint ret) {
    	convertToGL(uiPoint.x, uiPoint.y, ret);
    }
    
    /**
     * Optimizaed version of convertToGL(CGPoint uiPoint).
     * @param uiX - X coordinate of point for conversion
     * @param uiY - Y coordinate of point for conversion
     * @param ret - result point
     */
    public void convertToGL(float uiX, float uiY, CGPoint ret) {
    	float newX = uiX / surfaceSize_.width * screenSize_.width;
        float newY = screenSize_.height - uiY / surfaceSize_.height * screenSize_.height;
        
        switch (deviceOrientation_) {
            case kCCDeviceOrientationPortrait:
                ret.set(newX, newY);
            	//ret.set(newY, newX);
                break;

            case kCCDeviceOrientationLandscapeLeft:
                // ret = CGPoint.ccp(uiPoint.y, uiPoint.x);
            	ret.set(newX, newY);
                break;
        }

	    if (contentScaleFactor_ != 1 && isContentScaleSupported_ )
		    CGPointUtil.mult(ret, contentScaleFactor_);
    }

    /** converts an OpenGL coordinate to a UIKit coordinate
      Useful to convert node points to window points for calls such as glScissor
    */
    public CGPoint convertToUI(CGPoint glPoint) {
        CGSize winSize = surfaceSize_;
        int oppositeY = (int)(winSize.height - glPoint.y);

        CGPoint uiPoint = null;
        switch ( deviceOrientation_) {
            case kCCDeviceOrientationPortrait:
                uiPoint = CGPoint.ccp(glPoint.x, oppositeY);
            	//uiPoint = CGPoint.ccp(oppositeY, glPoint.x);
                break;

            case kCCDeviceOrientationLandscapeLeft:
                // uiPoint = CGPoint.ccp(glPoint.y, glPoint.x);
            	uiPoint = CGPoint.ccp(glPoint.x, oppositeY);
                break;
            default:
                return null;
        }

        uiPoint = CGPoint.ccpMult(uiPoint, 1/contentScaleFactor_);
        return uiPoint;
    }

    // Director CCScene Management

    /**Enters the Director's main loop with the given CCScene. 
     * Call it to run only your FIRST CCScene.
     * Don't call it if there is already a running CCScene.
    */
    public void runWithScene(CCScene CCScene) {
        assert CCScene != null : "Argument must be non-null";
        assert runningCCScene_ == null : "You can't run a CCScene if another CCScene is running. Use replaceCCScene or pushCCScene instead";

        pushScene(CCScene);
//        startAnimation();
    }

    /** Replaces the running CCScene with a new one. The running CCScene is terminated.
     * ONLY call it if there is a running CCScene.
    */
    public void replaceScene(CCScene CCScene) {
        assert CCScene != null : "Argument must be non-null";

        int index = CCScenesStack_.size();

	    sendCleanupToCCScene_ = true;
        CCScenesStack_.set(index - 1, CCScene);
        nextCCScene_ = CCScene;
    }

    /**Suspends the execution of the running CCScene, pushing it on the stack of suspended CCScenes.
     * The new CCScene will be executed.
     * Try to avoid big stacks of pushed CCScenes to reduce memory allocation. 
     * ONLY call it if there is a running CCScene.
    */
    public void pushScene(CCScene CCScene) {
        assert CCScene != null : "Argument must be non-null";

	    sendCleanupToCCScene_ = false;

        CCScenesStack_.add(CCScene);
        nextCCScene_ = CCScene;
    }

    /**Pops out a CCScene from the queue.
     * This CCScene will replace the running one.
     * The running CCScene will be deleted.
     *   If there are no more CCScenes in the stack the execution is terminated.
     * ONLY call it if there is a running CCScene.
     */
    public void popScene() {
        assert runningCCScene_ != null : "A running CCScene is needed";

        CCScenesStack_.remove(CCScenesStack_.size() - 1);
        int c = CCScenesStack_.size();

        if (c == 0) {
            end();
        } else {
            nextCCScene_ = CCScenesStack_.get(c - 1);
        }
    }

    /** Removes cached all cocos2d cached data.
      It will purge the CCTextureCache, CCSpriteFrameCache, CCBitmapFont cache
      @since v0.99.3
      */
    
    public void purgeCachedData() {
        //CCBitmapFontAtlas .purgeCachedData();
        CCSpriteFrameCache.purgeSharedSpriteFrameCache();
        CCTextureCache.purgeSharedTextureCache();
        
    }

    /** Ends the execution, releases the running CCScene.
      It doesn't remove the OpenGL view from its parent. You have to do it manually.
    */
    public void end() {
//    	synchronized(CCDirector.class) {
//    		if (_sharedDirector == null) {
//    			return;
//    		}
		
		if (runningCCScene_ != null) {
			runningCCScene_.onExit();
			runningCCScene_.cleanup();
			runningCCScene_ = null;
		}
		nextCCScene_ = null;

		// remove all objects.
		// runWithCCScene might be executed after 'end'.
		CCScenesStack_.clear();

		// don't release the event handlers
		// They are needed in case the director is run again
		CCTouchDispatcher.sharedDispatcher().removeAllDelegates();

//		stopAnimation();
		// detach();

		// Purge bitmap cache
		// CCBitmapFontAtlas.purgeCachedData();

		// Purge all managers
		CCSpriteFrameCache.purgeSharedSpriteFrameCache();
		// CCScheduler.purgeSharedScheduler();
		// CCActionManager.purgeSharedManager();
		CCTextureCache.purgeSharedTextureCache();

    		// OpenGL view
//    		openGLView_ = null;
//    		_sharedDirector = null;
//            if (FPSLabel_ != null)
//                FPSLabel_ = null;
//    	}
    }

    public void setNextScene() {
        boolean runningIsTransition = runningCCScene_ instanceof CCTransitionScene;
        boolean newIsTransition = nextCCScene_ instanceof CCTransitionScene;

        // If it is not a transition, call onExit
        if( runningCCScene_ != null && ! newIsTransition ) {
            runningCCScene_.onExit();

            // issue #709. the root node (CCScene) should receive the cleanup message too
            // otherwise it might be leaked.
            if( sendCleanupToCCScene_ )
                runningCCScene_.cleanup();
        }

        runningCCScene_ = nextCCScene_;
        nextCCScene_ = null;

        if( ! runningIsTransition ) {
            runningCCScene_.onEnter();
            runningCCScene_.onEnterTransitionDidFinish();
        }
    }

    /**
     * this should be called from activity when activity pause
     */
    public void onPause() {
    	openGLView_.onPause();
    	pause();
    }

    /**
     * this should be called from activity when activity resume
     */
    public void onResume() {
    	openGLView_.onResume();
    	resume();
    }
    
    /** Pauses the running CCScene.
      The running CCScene will be _drawed_ but all scheduled timers will be paused
      While paused, the draw rate will be 4 FPS to reduce CPU consuption
    */
    public void pause() {
        if (isPaused)
            return;

        oldAnimationInterval_ = animationInterval_;

        // when paused, don't consume CPU
        setAnimationInterval(1 / 4.0);
        isPaused = true;
    }

    /** Resumes the paused CCScene
      The scheduled timers will be activated again.
      The "delta time" will be 0 (as if the game wasn't paused)
    */
    public void resume() {
        if (!isPaused)
            return;

        setAnimationInterval(oldAnimationInterval_);

        lastUpdate_ = System.currentTimeMillis();

        isPaused = false;
        dt = 0;
    }

    /** The main loop is triggered again.
      Call this function only if [stopAnimation] was called earlier
      @warning Dont' call this function to start the main loop. To run the main loop call runWithCCScene
    */
//    public void startAnimation() {
//        assert animationTimer_ != null : "AnimationTimer must be null. Calling startAnimation twice?";
//
//        lastUpdate_ = System.currentTimeMillis();
//
//        animationTimer_ = new Timer();
//
//        animationTimer_.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//            	if (openGLView_ != null) {
//            		openGLView_.requestRender();
//            	}
//            }},
//            0L,
//            (long)(animationInterval_ * 1000L)
//        );
//    }

    /** Stops the animation. Nothing will be drawn. The main loop won't be triggered anymore.
      If you wan't to pause your animation call [pause] instead.
    */
//    public void stopAnimation() {
//    	animationTimer_.cancel();
//        animationTimer_ = null;
//    }

    /** enables/disables OpenGL alpha blending */
    public void setAlphaBlending(GL10 gl, boolean on) {
        if (on) {
            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
        } else {
            gl.glDisable(GL_BLEND);
        }
    }

    /** enables/disables OpenGL depth test */
    public void setDepthTest(GL10 gl, boolean on) {
        if (on){
		    gl.glClearDepthf(1.0f);
            gl.glEnable(GL_DEPTH_TEST);
		    gl.glDepthFunc(GL_LEQUAL);
		    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        } else {
            gl.glDisable(GL_DEPTH_TEST);
        }
    }

    public void setCCTexture2D(GL10 gl, boolean on) {
        if (on)
            gl.glEnable(GL_TEXTURE_2D);
        else
            gl.glDisable(GL_TEXTURE_2D);
    }

    public void updateContentScaleFactor() {
        /*
        // Based on code snippet from: http://developer.apple.com/iphone/prerelease/library/snippets/sp2010/sp28.html
        if (openGLView_.hasFunction(setContentScaleFactor)) {
            // XXX: To avoid compile warning when using Xcode 3.2.2
            typedef void (*CC_CONTENT_SCALE)(id, SEL, float);

            SEL selector = @selector(setContentScaleFactor:);
            CC_CONTENT_SCALE method = (CC_CONTENT_SCALE) [openGLView_ methodForSelector:selector];
            method(openGLView_,selector, contentScaleFactor_);

            // In Xcode 3.2.3 SDK 4.0, use this one:
            //		[openGLView_ setContentScaleFactor: scaleFactor];

            isContentScaleSupported_ = YES;

        } else {
            CCLOG(@"cocos2d: WARNING: calling setContentScaleFactor on iOS < 4. Using fallback mechanism");
            // on pre-4.0 iOS, use bounds/transform
            openGLView_.bounds = CGRectMake(0, 0,
                    openGLView_.bounds.size.width * contentScaleFactor_,
                    openGLView_.bounds.size.height * contentScaleFactor_);
            openGLView_.transform = CGAffineTransformScale(openGLView_.transform, 1 / contentScaleFactor_, 1 / contentScaleFactor_); 

            isContentScaleSupported_ = NO;
        }
        */
    }

    public int[] getConfigSpec() {
        if (mTranslucentBackground) {
            // We want a depth buffer and an getOpacity buffer
            int[] configSpec = {
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_NONE
            };
            return configSpec;
        } else {
            // We want a depth buffer, don't care about the
            // details of the color buffer.
            int[] configSpec = {
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_NONE
            };
            return configSpec;
        }
    }

	private TextBuilder fpsBuilder = new TextBuilder();
	
    private void showFPS(GL10 gl) {

        if (FAST_FPS_DISPLAY) {
            // display the FPS using a LabelAtlas
            // updates the FPS every frame

            frames_++;
            accumDt_ += dt;

            if (accumDt_ > ccConfig.CC_DIRECTOR_FPS_INTERVAL) {
                frameRate_ = frames_ / accumDt_;
                frames_ = 0;
                accumDt_ = 0;
                
                int fpsInt = (int)frameRate_;
                int fpsFract = (int)( (frameRate_ - fpsInt) * 10 );
                
                fpsBuilder.reset();
                fpsBuilder.append(fpsInt);
                fpsBuilder.append('.');
                fpsBuilder.append(fpsFract);
                
                FPSLabel_.setString(fpsBuilder);
            }
            
            FPSLabel_.draw(gl);
        } else {
            // display the FPS using a manually generated Texture (very slow)
            // updates the FPS 3 times per second aprox.

            frames_++;
            accumDt_ += dt;

            if (accumDt_ > ccConfig.CC_DIRECTOR_FPS_INTERVAL) {
                frameRate_ = frames_ / accumDt_;
                frames_ = 0;
                accumDt_ = 0;
            }

            String str = CCFormatter.format("%.2f", frameRate_);
            CCTexture2D texture = new CCTexture2D();
            // no need to register Loader for this subordinate texture
            texture.initWithText(str, CGSize.make(100, 30), TextAlignment.LEFT, "DroidSans", 24);

            // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
            // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
            // Unneeded states: GL_COLOR_ARRAY
            
            gl.glDisableClientState(GL_COLOR_ARRAY);
	        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            gl.glColor4f(224.f / 255, 224.f / 255, 244.f / 255, 200.f / 255);
            texture.drawAtPoint(gl, CGPoint.ccp(5, 2));

            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

            // restore default GL state
            gl.glEnableClientState(GL_COLOR_ARRAY);
        }
    }

    private boolean mTranslucentBackground = false;
    
    public boolean onKeyDown(KeyEvent event){
    	if (!CCKeyDispatcher.sharedDispatcher().getDispatchEvents())
    		return false;
    	CCKeyDispatcher.sharedDispatcher().queueMotionEvent(event);
    	return true;
    }
    
	//added by Ishaq 
	public boolean onKeyUp(KeyEvent event){
    	if (!CCKeyDispatcher.sharedDispatcher().getDispatchEvents())
    		return false;

    	CCKeyDispatcher.sharedDispatcher().queueMotionEvent(event);
    	return true;
    }
	
	//added by Ishaq 
	public void setIsEnableKeyEvent(boolean b){
		CCKeyDispatcher.sharedDispatcher().setDispatchEvents(b);
	}
	
	//added by Ishaq 
	public boolean isEnableKeyEvent(){
		return CCKeyDispatcher.sharedDispatcher().getDispatchEvents();
	}

    public static GL10 gl;
}

