package org.cocos2d.nodes;

import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_DITHER;
import static javax.microedition.khronos.opengles.GL10.GL_FASTEST;
import static javax.microedition.khronos.opengles.GL10.GL_FLAT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.microedition.khronos.opengles.GL10.GL_PROJECTION;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_VERTEX_ARRAY;

import java.util.ArrayList;
import java.util.Timer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.ActionManager;
import org.cocos2d.actions.Scheduler;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.Camera;
import org.cocos2d.opengl.GLU;
import org.cocos2d.transitions.TransitionScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.CCFormatter;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class Director implements GLSurfaceView.Renderer {
    private static final String LOG_TAG = Director.class.getSimpleName();

    public static Activity me = null;

    public Activity getActivity() {
        return me;
    }

    private int width_, height_;

    private static final double kDefaultFPS = 60.0f;    // 60 frames per second

    // Landscape is right or left ?
    // private static final boolean LANDSCAPE_LEFT = false;

    // Fast FPS display. FPS are updated 10 times per second without consuming resources
    // uncomment this line to use the old method that updated
    private static final boolean FAST_FPS_DISPLAY = true;

    /** Possible OpenGL projections used by director */

    // sets a 2D projection (orthogonal projection)
    public static final int CCDirectorProjection2D = 1;

    // sets a 3D projection with a fovy=60, znear=0.5f and zfar=1500.
    public static final int CCDirectorProjection3D = 2;

    // it does nothing. But if you are using a custom projection set it this value.
    public static final int CCDirectorProjectionCustom = 3;

    // Detault projection is 2D projection
    // TODO Change to 3D
    public static final int CCDirectorProjectionDefault = CCDirectorProjection2D;

    private int projection_;


    /** Possible device orientations */

	// Device oriented vertically, home button on the bottom
	public static final int CCDeviceOrientationPortrait = 1;
	/// Device oriented vertically, home button on the top
    public static final int CCDeviceOrientationPortraitUpsideDown = 2;
	/// Device oriented horizontally, home button on the right
    public static final int CCDeviceOrientationLandscapeLeft = 3;
	/// Device oriented horizontally, home button on the left
    public static final int CCDeviceOrientationLandscapeRight = 4;

    // internal timer
    private Timer animationTimer;
    private double oldAnimationInterval;

    // private int pixelFormat_;

    /* orientation */
    int	deviceOrientation_;

    /* display FPS ? */
    private int frames;
    private float accumDt;
    private float frameRate;

    LabelAtlas fpsLabel;

    /* is the running scene paused */
    private boolean paused;

    /* The running scene */
    private Scene runningScene_;

    /* will be the next 'runningScene' in the next frame */
    private Scene nextScene;

    /* scheduled scenes */
    private ArrayList<Scene> scenesStack_;

    /* last time the main loop was updated */
    private long lastUpdate;
    /* delta time since last tick to main loop */
    private float dt;
    /* whether or not the next delta time will be zero */
    private boolean nextDeltaTimeZero_;

    private GLSurfaceView openGLView_;

    /**
     * The current running Scene. Director can only run one Scene at the time
     */
    public Scene runningScene() {
        return runningScene_;
    }

    /**
     * The FPS value
     */
    private double animationInterval;

    public void getAnimationInterval(double interval) {
        animationInterval = interval;
    }

    /**
     * Whether or not to display the FPS on the bottom-left corner
     */
    private boolean displayFPS;

    /**
     * The OpenGL view
     */
    private GLSurfaceView getOpenGLView() {
        return openGLView_;
    }

    /**
     * Pixel format used to create the context
     */
    // private PixelFormat pixelFormat;

    /**
     * whether or not the next delta time will be zero
     */
    // private boolean nextDeltaTimeZero;

    public void setDisplayFPS(boolean value) {
        displayFPS = value;
    }

    private static Director _sharedDirector;

    public static Director sharedDirector() {
        synchronized (Director.class) {
            if (_sharedDirector == null) {
                _sharedDirector = new Director();
            }
            return _sharedDirector;
        }
    }

//	public void useFaseDirector(){
//		assert _sharedDirector == null : "A Director was allocated. setDirectorType must be the first call to Director";
//
//		FastDirector.sharedDirector();
//	}

    protected Director() {
        synchronized (Director.class) {

            //Create a full-screen window

            // default values
            // pixelFormat_ = PixelFormat.RGB_565;

            // scenes
            runningScene_ = null;
            nextScene = null;

            oldAnimationInterval = animationInterval = 1.0 / kDefaultFPS;
            scenesStack_ = new ArrayList<Scene>(10);

            // landscape
            deviceOrientation_ = CCDeviceOrientationPortrait;

            // FPS
            displayFPS = false;
            frames = 0;

            // paused?
            paused = false;

        }
    }

    private void initGLDefaultValues(GL10 gl) {
//        assert openGLView_ != null : "openGLView_ must be initialized";

        setAlphaBlending(gl, true);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        setDepthTest(gl, false);

        gl.glDisable(GL_LIGHTING);

        gl.glShadeModel(GL_FLAT);
//
//        setCCTexture2D(gl, true);

        // set other opengl default values
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);

        if (FAST_FPS_DISPLAY) {
            fpsLabel = new LabelAtlas("00.0", "fps_images.png", 16, 24, '.');
            fpsLabel.setPosition(CGPoint.make(50, 2));
        }

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        width_ = width;
        height_ = height;
        gl.glViewport(0, 0, width, height);
        setDefaultProjection(gl);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

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


        initGLDefaultValues(gl);
    }

    public void onDrawFrame(GL10 gl) {

        synchronized (this) {
            this.notify();
        }
        Thread.yield();

        /* clear window */
        gl.glClear(GL_COLOR_BUFFER_BIT /*| GL_DEPTH_BUFFER_BIT*/);

        /* calculate "global" dt */
        calculateDeltaTime();
        if (!paused)
            Scheduler.sharedScheduler().tick(dt);


        /* to avoid flicker, nextScene MUST be here: after tick and before draw */
        if (nextScene != null) {
            setNextScene();
        }

        gl.glPushMatrix();

        applyLandscape(gl);

        /* draw the scene */
        if (runningScene_ != null)
            runningScene_.visit(gl);
        if (displayFPS)
            showFPS(gl);

        gl.glPopMatrix();

    }

    private void calculateDeltaTime() {
        long now = System.currentTimeMillis();

        // new delta time
        if (nextDeltaTimeZero_) {
            dt = 0;
            nextDeltaTimeZero_ = false;
        } else {
            dt = (now - lastUpdate) / 1000.0f;
            dt = Math.max(0, dt);
        }

        lastUpdate = now;
    }

    public void setPixelFormat(int format) {
        // pixelFormat_ = format;
    }
                       
    private void setDefaultProjection(GL10 gl) {
        setprojection(gl, CCDirectorProjectionDefault);
//      set2Dprojection(gl);
//      set3Dprojection(gl);
    }

    private void setprojection(GL10 gl, int projection) {
        switch (projection) {
            case CCDirectorProjection2D:
                gl.glMatrixMode(GL_PROJECTION);
                gl.glLoadIdentity();
                gl.glOrthof(0, width_, 0, height_, -1, 1);

//                gl.glTranslatef(0, height_, 0);
//                gl.glScalef(1.0f, -1.0f, 1.0f);

                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
                break;

            case CCDirectorProjection3D:
                gl.glViewport(0, 0, width_, height_);
                gl.glMatrixMode(GL_PROJECTION);
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, 60, height_, 0.5f, 1500.0f);

                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity();
                GLU.gluLookAt(gl, width_ / 2, height_ / 2, Camera.getZEye(),
                          width_ / 2, height_ / 2, 0, 0.0f, 1.0f, 0.0f);
                break;

            case CCDirectorProjectionCustom:
                // if custom, ignore it.
                // The user is responsible for setting the correct projection
                break;

            default:
                Log.w(LOG_TAG, "cocos2d: Director: Unrecognized projection");
                break;
        }

        projection_ = projection;

    }

    public int getProjection() {
        return projection_;
    }

    private void set2Dprojection(GL10 gl) {
        setprojection(gl, CCDirectorProjection2D);

//        gl.glMatrixMode(GL_PROJECTION);
//        gl.glLoadIdentity();
//        GLU.gluOrtho2D(gl, 0, width_, height_, 0);
//        gl.glTranslatef(0, height_, 0);
//        gl.glScalef(1.0f, -1.0f, 1.0f);
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
    }

    // set a 3d projection matrix
    private void set3Dprojection(GL10 gl) {
        setprojection(gl, CCDirectorProjection3D);

//        gl.glMatrixMode(GL_PROJECTION);
//        gl.glLoadIdentity();
//        GLU.gluPerspective(gl, 60, width_ / height_, 0.5f, 1500.0f);
//
//        gl.glMatrixMode(GL_MODELVIEW);
//        gl.glLoadIdentity();
//        GLU.gluLookAt(gl, width_ / 2, height_ / 2, Camera.getZEye(),
//                width_ / 2, height_ / 2, 0, 0.0f, 1.0f, 0.0f);
    }

    public CGSize winSize() {
        CGSize s = CGSize.make(width_, height_);
        if( deviceOrientation_ == CCDeviceOrientationLandscapeLeft || deviceOrientation_ == CCDeviceOrientationLandscapeRight ) {
            // swap x,y in landscape mode
            s.width = height_;
            s.height = width_;
        }
        return s;
    }

    public CGSize displaySize() {
        return CGSize.make(width_, height_);
    }

    public boolean getLandscape() {
        return deviceOrientation_ == CCDeviceOrientationLandscapeLeft;
    }

    public void setLandscape(boolean on) {
        if ( on )
            setDeviceOrientation(CCDeviceOrientationLandscapeLeft);
        else
            setDeviceOrientation(CCDeviceOrientationPortrait);
    }

    public int getDeviceOrientation() {
        return deviceOrientation_;
    }

    public void setDeviceOrientation(int orientation) {
        if( deviceOrientation_ != orientation ) {
            deviceOrientation_ = orientation;
            switch( deviceOrientation_) {
                case CCDeviceOrientationPortrait:
                    break;
                case CCDeviceOrientationPortraitUpsideDown:
                    break;
                case CCDeviceOrientationLandscapeLeft:
                    break;
                case CCDeviceOrientationLandscapeRight:
                    break;
                default:
                    Log.w(LOG_TAG, "Director: Unknown device orientation");
                    break;
            }
        }
    }


    private void applyLandscape(GL10 gl) {

        switch ( deviceOrientation_ ) {
            case CCDeviceOrientationPortrait:
                // nothing
                break;
            case CCDeviceOrientationPortraitUpsideDown:
                // upside down
                gl.glTranslatef(160,240,0);
                gl.glRotatef(180,0,0,1);
                gl.glTranslatef(-160,-240,0);
                break;
            case CCDeviceOrientationLandscapeRight:
                gl.glTranslatef(160,240,0);
                gl.glRotatef(90,0,0,1);
                gl.glTranslatef(-240,-160,0);
                break;
            case CCDeviceOrientationLandscapeLeft:
                gl.glTranslatef(160,240,0);
                gl.glRotatef(-90,0,0,1);
                gl.glTranslatef(-240,-160,0);
                break;
        }

    }

    // Director Integration with a Android view
    // is the view currently attached

    public boolean isOpenGLAttached() {
        return true;
    }

    // detach or attach to a view or a window
    private boolean detach() {
        try {
//            // check if the view is attached
//            if (!isOpenGLAttached()) {
//                // the view is not attached
//                throw new OpenGLViewNotAttachedException("Can't detach the OpenGL View, because it is not attached. Attach it first.");
//            }
//
//            // remove from the superview
//        openGLView_.removeFromSuperview();
//
//            // check if the view is not attached anymore
//            if (!isOpenGLAttached()) {
//                return true;
//            }
//
//            // the view is still attached
//            throw new OpenGLViewCantDetachException("Can't detach the OpenGL View, it is still attached to the superview.");

            me.finish();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean attachInWindow(View window) {
        return attachInView(window);
    }

    public boolean attachInView(View view) {

//        CCRect rect = new CCRect(view.getScrollX(), view.getScrollY(), view.getWidth(), view.getHeight());
        WindowManager w = me.getWindowManager();
        CGRect rect = CGRect.make(0, 0, w.getDefaultDisplay().getWidth(), w.getDefaultDisplay().getHeight());

        return initOpenGLViewWithView(view, rect);
    }

    public boolean attachInView(View view, CGRect rect) {
        return initOpenGLViewWithView(view, rect);
    }

    private boolean initOpenGLViewWithView(View view, CGRect rect) {
        width_ = (int) rect.size.width;
        height_ = (int) rect.size.height;
//        try {
        openGLView_ = (GLSurfaceView) view;
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


    public CGPoint convertCoordinate(float x, float y) {
        return convertToGL(x, y);
    }

    public CGPoint convertToGL(float uiPointX, float uiPointY) {
        float newY = height_ - uiPointY;
        float newX = width_ - uiPointX;

        switch ( deviceOrientation_) {
            case CCDeviceOrientationPortrait:
                return CGPoint.ccp(uiPointX, newY);

            case CCDeviceOrientationPortraitUpsideDown:
                return CGPoint.ccp(newX, uiPointY);

            case CCDeviceOrientationLandscapeLeft:
                return CGPoint.ccp(uiPointY, uiPointX);

            case CCDeviceOrientationLandscapeRight:
                return CGPoint.ccp(newY, newX);
            }
        return null;
    }
    
    public CGPoint convertToUI(CGPoint glPoint) {
    	
        CGSize winSize = winSize();
        int oppositeX = (int)(winSize.width - glPoint.x);
        int oppositeY = (int)(winSize.height - glPoint.y);
        switch ( deviceOrientation_) {
            case CCDeviceOrientationPortrait:
                return CGPoint.ccp(glPoint.x, glPoint.y);

            case CCDeviceOrientationPortraitUpsideDown:
                return CGPoint.ccp(oppositeX, oppositeY);

            case CCDeviceOrientationLandscapeLeft:
                return CGPoint.ccp(glPoint.y, oppositeX);

            case CCDeviceOrientationLandscapeRight:
                return CGPoint.ccp(oppositeY, glPoint.x);
        }

        return null;
    }

    // Director Scene Management

    public void runWithScene(Scene scene) {
        assert scene != null : "Argument must be non-null";

        assert runningScene_ == null : "You can't run a scene if another scene is running. Use replaceScene or pushScene instead";

        pushScene(scene);
        startAnimation();
    }

    public void replaceScene(Scene scene) {
        assert scene != null : "Argument must be non-null";

        int index = scenesStack_.size();

        scenesStack_.set(index - 1, scene);
        nextScene = scene;
    }

    public void pushScene(Scene scene) {
        assert scene != null : "Argument must be non-null";

        scenesStack_.add(scene);
        nextScene = scene;
    }

    public void popScene() {
        assert runningScene_ != null : "A running scene is needed";

        scenesStack_.remove(scenesStack_.size() - 1);
        int c = scenesStack_.size();

        if (c == 0) {
            end();
        } else {
            nextScene = scenesStack_.get(c - 1);
        }
    }

    public void end() {
        // remove all objects.
        // runWithScene might be executed after 'end'.
        scenesStack_.clear();

        if (runningScene_ != null)
            runningScene_.onExit();

        runningScene_.cleanup();

        runningScene_ = null;
        nextScene = null;

        CCTouchDispatcher.sharedDispatcher().removeAllDelegates();

        scenesStack_.clear();

        stopAnimation();
        detach();

        ActionManager.sharedManager().removeAllActions();
        TextureManager.sharedTextureManager();

        // OpenGL view
        openGLView_ = null;


    }

    public void setNextScene() {

        boolean runningIsTransition = runningScene_ instanceof TransitionScene;
        boolean newIsTransition = nextScene instanceof TransitionScene;

        // If it is not a transition, call onExit
        if( runningScene_ != null && ! newIsTransition )
            runningScene_.onExit();

        runningScene_ = nextScene;
        nextScene = null;

        if( ! runningIsTransition ) {
            runningScene_.onEnter();
            runningScene_.onEnterTransitionDidFinish();
        }
    }

    public void pause() {
        if (paused)
            return;

        oldAnimationInterval = animationInterval;

        // when paused, don't consume CPU
        setAnimationInterval(1 / 4.0);
        paused = true;
    }

    public void resume() {
        if (!paused)
            return;

        setAnimationInterval(oldAnimationInterval);

        lastUpdate = System.currentTimeMillis();

        paused = false;
        dt = 0;
    }

    public void startAnimation() {
        assert animationTimer != null : "AnimationTimer must be null. Calling startAnimation twice?";

        lastUpdate = System.currentTimeMillis();

        animationTimer = new Timer();

//        animationTimer.scheduleAtFixedRate(new TimerTask() {
//            public void run() { mainLoop(); } }, 0L, (long)(animationInterval * 1000L));
    }

    public void stopAnimation() {
//        animationTimer.cancel();
        animationTimer = null;
    }

    public void setAnimationInterval(double interval) {
        animationInterval = interval;

        if (animationTimer != null) {
            stopAnimation();
            startAnimation();
        }
    }

    public void setAlphaBlending(GL10 gl, boolean on) {
        if (on)
            gl.glEnable(GL_BLEND);
        else
            gl.glDisable(GL_BLEND);
    }

    public void setDepthTest(GL10 gl, boolean on) {
        if (on)
            gl.glEnable(GL_DEPTH_TEST);
        else
            gl.glDisable(GL_DEPTH_TEST);
    }

    public void setCCTexture2D(GL10 gl, boolean on) {
        if (on)
            gl.glEnable(GL_TEXTURE_2D);
        else
            gl.glDisable(GL_TEXTURE_2D);
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

    private void showFPS(GL10 gl) {

        if (FAST_FPS_DISPLAY) {
            frames++;

            accumDt += dt;

            if (accumDt > 0.1) {
                frameRate = frames / accumDt;
                frames = 0;
                accumDt = 0;
            }

            fpsLabel.setString(new CCFormatter().format("%.1f", frameRate));
            fpsLabel.draw(gl);
        } else {
            frames++;
            accumDt += dt;

            if (accumDt > 0.3) {
                frameRate = frames / accumDt;
                frames = 0;
                accumDt = 0;
            }

            CCTexture2D texture = new CCTexture2D(new CCFormatter().format("%.2f", frameRate), "DroidSans", 24);
            gl.glEnable(GL_TEXTURE_2D);
            gl.glEnableClientState(GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

            gl.glColor4f(224 / 255f, 224 / 255f, 244 / 255f, 200 / 255f);
            texture.drawAtPoint(gl, CGPoint.ccp(5, 2));

            gl.glDisable(GL_TEXTURE_2D);
            gl.glDisableClientState(GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }
    }

    private boolean mTranslucentBackground = false;

}
