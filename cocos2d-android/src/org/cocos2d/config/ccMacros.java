package org.cocos2d.config;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.GLSurfaceView;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

/**
 @file
 cocos2d helper macros
 */
public class ccMacros {

    /*
     * if COCOS2D_DEBUG is not defined, or if it is 0 then
     *	all CCLOGXXX macros will be disabled
     *
     * if COCOS2D_DEBUG==1 then:
     *		CCLOG() will be enabled
     *		CCLOGERROR() will be enabled
     *		CCLOGINFO()	will be disabled
     *
     * if COCOS2D_DEBUG==2 or higher then:
     *		CCLOG() will be enabled
     *		CCLOGERROR() will be enabled
     *		CCLOGINFO()	will be enabled 
     */
    public static final void CCLOG(final String logName, final String logStr) {
        if (ccConfig.COCOS2D_DEBUG >= 1) {
            Log.d(logName, logStr);
        }
    }

    public static final void CCLOGINFO(final String logName, final String logStr) {
        if (ccConfig.COCOS2D_DEBUG >= 1) {
            Log.i(logName, logStr);
        }
    }

    public static final void CCLOGERROR(final String logName, final String logStr) {
        if (ccConfig.COCOS2D_DEBUG >= 2) {
            Log.e(logName, logStr);
        }
    }

    public static final float FLT_EPSILON = 0.000001f;
    public static final int INT_MIN = -2147483648;
    public static final int CC_MAX_PARTICLE_SIZE = 64;
    
    /// java doesn't support swap primitive types.
    /// public static void CC_SWAP(T x, T y);

    /** @def CCRANDOM_MINUS1_1
      returns a random float between -1 and 1
    */
    public static final float CCRANDOM_MINUS1_1() {
        return (float) Math.random() * 2.0f - 1.0f;
    }

    /** @def CCRANDOM_0_1
      returns a random float between 0 and 1
    */
    public static final float CCRANDOM_0_1() {
        return (float) Math.random();
    }

    /** @def M_PI_2
            Math.PI divided by 2
    */
    public static final float M_PI_2 = (float)(Math.PI / 2);
    

    /** @def CC_DEGREES_TO_RADIANS
        converts degrees to radians
    */
    public static final float CC_DEGREES_TO_RADIANS(float angle) {
        return (angle / 180.0f * (float) Math.PI);
    }

    /** @def CC_RADIANS_TO_DEGREES
        converts radians to degrees
    */
    public static final float CC_RADIANS_TO_DEGREES(float angle) {
        return (angle / (float) Math.PI * 180.0f);
    }

    /** @def CC_ENABLE_DEFAULT_GL_STATES
      GL states that are enabled:
      - GL_TEXTURE_2D
      - GL_VERTEX_ARRAY
      - GL_TEXTURE_COORD_ARRAY
      - GL_COLOR_ARRAY
      */
    public static final void CC_ENABLE_DEFAULT_GL_STATES(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    /** @def CC_DISABLE_DEFAULT_GL_STATES 
      Disable default GL states:
      - GL_TEXTURE_2D
      - GL_VERTEX_ARRAY
      - GL_TEXTURE_COORD_ARRAY
      - GL_COLOR_ARRAY
      */
    public static final void CC_DISABLE_DEFAULT_GL_STATES(GL10 gl) {
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    /** @def CC_DIRECTOR_INIT
      - Initializes an EAGLView with 0-bit depth format, and RGB565 render buffer.
      - The EAGLView view will have multiple touches disabled.
      - It will create a UIWindow and it will assign it the 'window' variable. 'window' must be declared before calling this marcro.
      - It will parent the EAGLView to the created window
      - If the firmware >= 3.1 it will create a Display Link Director. Else it will create an NSTimer director.
      - It will try to run at 60 FPS.
      - The FPS won't be displayed.
      - The orientation will be portrait.
      - It will connect the director with the EAGLView.

    IMPORTANT: If you want to use another type of render buffer (eg: RGBA8)
    or if you want to use a 16-bit or 24-bit depth buffer, you should NOT
    use this macro. Instead, you should create the EAGLView manually.

    @since v0.99.4
    */
    // not supported yet...
    public static final void CC_DIRECTOR_INIT(Activity app) {
    	CCDirector director = CCDirector.sharedDirector();
    	director.setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
    	director.setDisplayFPS(false);
    	director.setAnimationInterval(1.0/60);
    	
    	CCGLSurfaceView sv = new CCGLSurfaceView(app);
    	
    	director.attachInView(sv);
    	app.setContentView(sv);
    }

    /** @def CC_DIRECTOR_END
      Stops and removes the director from memory.
      Removes the EAGLView from its parent

      @since v0.99.4
      */
    // do nothing yet...
    public static final void CC_DIRECTOR_END() {
        CCDirector director = CCDirector.sharedDirector();
        GLSurfaceView view = director.getOpenGLView();
        ViewGroup vg = (ViewGroup)view.getParent();
        vg.removeView(view);
        director.end();
    }

}

