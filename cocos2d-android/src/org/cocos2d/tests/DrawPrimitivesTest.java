package org.cocos2d.tests;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCDrawingPrimitives;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class DrawPrimitivesTest extends Activity {
    // private static final String LOG_TAG = DrawPrimitivesTest.class.getSimpleName();
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
        scene.runAction(CCRotateBy.action(4, -360));

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
        // CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Test1.class,
    };

    public static CCLayer nextAction() {

        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;

        return restartAction();
    }

    public static CCLayer backAction() {
        sceneIdx--;
        int total = transitions.length;
        if (sceneIdx < 0)
            sceneIdx += total;

        return restartAction();
    }

    public static CCLayer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (CCLayer) c.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static class TestDemo extends CCLayer {
        public TestDemo() {
            CGSize s = CCDirector.sharedDirector().winSize();

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);

            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));

            addChild(menu, -1);
        }

        /*
         * After setting the screen orientation to landscape,
         *   the Activity will be restarted, so it seems we should not call setLandscape here
         *  this is a bug, we should make full use of android's capability, but not partly.  
         */
        public void restartCallback(Object sender) {
            boolean landscape = CCDirector.sharedDirector().getLandscape();
            CCDirector.sharedDirector().setLandscape(!landscape);

            CCScene s = CCScene.node();
            s.addChild(restartAction());
            CCDirector.sharedDirector().runWithScene(s);
        }

        public void nextCallback(Object sender) {
            boolean landscape = CCDirector.sharedDirector().getLandscape();
            CCDirector.sharedDirector().setLandscape(!landscape);

            CCScene s = CCScene.node();
            s.addChild(nextAction());
            CCDirector.sharedDirector().runWithScene(s);
        }

        public void backCallback(Object sender) {
            boolean landscape = CCDirector.sharedDirector().getLandscape();
            CCDirector.sharedDirector().setLandscape(!landscape);

            CCScene s = CCScene.node();
            s.addChild(backAction());
            CCDirector.sharedDirector().runWithScene(s);
        }

        String title() {
            return "No title";
        }
    }

    public static class Test1 extends TestDemo {
        public static CCLayer node() {
            return new Test1();
        }

        //
        // TIP:
        // Every CocosNode has a "draw" method.
        // In the "draw" method you put all the code that actually draws your node.
        // And Test1 is a subclass of TestDemo, which is a subclass of Layer, which is a subclass of CocosNode.
        //
        // As you can see the drawing primitives aren't CocosNode objects. They are just helper
        // functions that let's you draw basic things like: points, line, polygons and circles.
        //
        //
        // TIP:
        // Don't draw your stuff outside the "draw" method. Otherwise it won't get transformed.
        //
        //
        // TIP:
        // If you want to rotate/translate/scale a circle or any other "primtive", you can do it by rotating
        // the node. eg:
        //    this.rotation = 90;
        //
        public void draw(GL10 gl) {
            CGSize s = CCDirector.sharedDirector().winSize();


            // draw a simple line
            // The default state is:
            // Line Width: 1
            // color: 255,255,255,255 (white, non-transparent)
            // Anti-Aliased
            gl.glEnable(GL10.GL_LINE_SMOOTH);
            
            CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(0, 0), CGPoint.ccp(s.width, s.height));

            // line: color, width, aliased
            // glLineWidth > 1 and GL_LINE_SMOOTH are not compatible
            // GL_SMOOTH_LINE_WIDTH_RANGE = (1,1) on iPhone
            gl.glDisable(GL10.GL_LINE_SMOOTH);
            gl.glLineWidth(5.0f);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(0, s.height), CGPoint.ccp(s.width, 0));

            // TIP:
            // If you are going to use always the same color or width, you don't
            // need to call it before every draw
            //
            // Remember: OpenGL is a state-machine.

            // draw big point in the center
            gl.glPointSize(64);
            gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
            CCDrawingPrimitives.ccDrawPoint(gl, CGPoint.make(s.width / 2, s.height / 2));

            // draw 4 small points
            CGPoint points[] = {CGPoint.ccp(60, 60), CGPoint.ccp(70, 70), CGPoint.ccp(60, 70), CGPoint.ccp(70, 60)};
            gl.glPointSize(4);
            gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
            CCDrawingPrimitives.ccDrawPoints(gl, points, 4);

            // draw a green circle with 10 segments
            gl.glLineWidth(16);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            CCDrawingPrimitives.ccDrawCircle(gl, CGPoint.make(s.width / 2, s.height / 2), 100, 0, 10, false);

            // draw a green circle with 50 segments with line to center
            gl.glLineWidth(2);
            gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
            CCDrawingPrimitives.ccDrawCircle(gl, CGPoint.make(s.width / 2, s.height / 2), 50, ccMacros.CC_DEGREES_TO_RADIANS(90), 50, true);

            // open yellow poly
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glLineWidth(10);
            CGPoint vertices[] = {CGPoint.ccp(0, 0), CGPoint.ccp(50, 50), CGPoint.ccp(100, 50), CGPoint.ccp(100, 100), CGPoint.ccp(50, 100)};
            CCDrawingPrimitives.ccDrawPoly(gl, vertices, 5, false);

            // closed purple poly
            gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
            gl.glLineWidth(2);
            CGPoint vertices2[] = {CGPoint.ccp(30, 130), CGPoint.ccp(30, 230), CGPoint.ccp(50, 200)};
            CCDrawingPrimitives.ccDrawPoly(gl, vertices2, 3, true);

            // draw quad bezier path
            CCDrawingPrimitives.ccDrawQuadBezier(gl, CGPoint.make(0,s.height), CGPoint.make(s.width/2,s.height/2), CGPoint.make(s.width, s.height), 50);

            // draw cubic bezier path
            CCDrawingPrimitives.ccDrawCubicBezier(gl, CGPoint.make(s.width/2, s.height/2), CGPoint.make(s.width/2+30, s.height/2+50),
                    CGPoint.make(s.width/2+60, s.height/2-50), CGPoint.make(s.width, s.height/2),100);


            // restore original values
            gl.glLineWidth(1);
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glPointSize(1);
        }

        public String title() {
            return "draw primitives";
        }
    }

}
