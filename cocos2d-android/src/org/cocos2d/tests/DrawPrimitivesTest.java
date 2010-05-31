package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.interval.RotateBy;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.TextureManager;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.Primitives;
import org.cocos2d.types.CCMacros;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

import javax.microedition.khronos.opengles.GL10;

public class DrawPrimitivesTest extends Activity {
    // private static final String LOG_TAG = DrawPrimitivesTest.class.getSimpleName();
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
        scene.addChild(nextAction());
        scene.runAction(RotateBy.action(4, -360));

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
            Test1.class,
    };

    public static Layer nextAction() {

        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;

        return restartAction();
    }

    public static Layer backAction() {
        sceneIdx--;
        int total = transitions.length;
        if (sceneIdx < 0)
            sceneIdx += total;

        return restartAction();
    }

    public static Layer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (Layer) c.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static class TestDemo extends Layer {
        public TestDemo() {
            CCSize s = Director.sharedDirector().winSize();

            MenuItemImage item1 = MenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            MenuItemImage item2 = MenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            MenuItemImage item3 = MenuItemImage.item("f1.png", "f2.png", this, "nextCallbackk");

            Menu menu = Menu.menu(item1, item2, item3);

            menu.setPosition(0, 0);
            item1.setPosition(s.width / 2 - 100, 30);
            item2.setPosition(s.width / 2, 30);
            item3.setPosition(s.width / 2 + 100, 30);
            addChild(menu, -1);
        }

        public void restartCallback() {
            boolean landscape = Director.sharedDirector().getLandscape();
            Director.sharedDirector().setLandscape(!landscape);

            Scene s = Scene.node();
            s.addChild(restartAction());
            Director.sharedDirector().replaceScene(s);
        }

        public void nextCallback() {
            boolean landscape = Director.sharedDirector().getLandscape();
            Director.sharedDirector().setLandscape(!landscape);

            Scene s = Scene.node();
            s.addChild(nextAction());
            Director.sharedDirector().replaceScene(s);
        }

        public void backCallback() {
            boolean landscape = Director.sharedDirector().getLandscape();
            Director.sharedDirector().setLandscape(!landscape);

            Scene s = Scene.node();
            s.addChild(backAction());
            Director.sharedDirector().replaceScene(s);
        }

        String title() {
            return "No title";
        }
    }

    public static class Test1 extends TestDemo {
        public static Layer node() {
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
            CCSize s = Director.sharedDirector().winSize();


            // draw a simple line
            // The default state is:
            // Line Width: 1
            // color: 255,255,255,255 (white, non-transparent)
            // Anti-Aliased
            gl.glEnable(GL10.GL_LINE_SMOOTH);
            
            Primitives.drawLine(gl, CCPoint.ccp(0, 0), CCPoint.ccp(s.width, s.height));

            // line: color, width, aliased
            // glLineWidth > 1 and GL_LINE_SMOOTH are not compatible
            // GL_SMOOTH_LINE_WIDTH_RANGE = (1,1) on iPhone
            gl.glDisable(GL10.GL_LINE_SMOOTH);
            gl.glLineWidth(5.0f);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            Primitives.drawLine(gl, CCPoint.ccp(0, s.height), CCPoint.ccp(s.width, 0));

            // TIP:
            // If you are going to use always the same color or width, you don't
            // need to call it before every draw
            //
            // Remember: OpenGL is a state-machine.

            // draw big point in the center
            gl.glPointSize(64);
            gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
            Primitives.drawPoint(gl, s.width / 2, s.height / 2);

            // draw 4 small points
            CCPoint points[] = {CCPoint.ccp(60, 60), CCPoint.ccp(70, 70), CCPoint.ccp(60, 70), CCPoint.ccp(70, 60)};
            gl.glPointSize(4);
            gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
            Primitives.drawPoints(gl, points, 4);

            // draw a green circle with 10 segments
            gl.glLineWidth(16);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            Primitives.drawCircle(gl, s.width / 2, s.height / 2, 100, 0, 10, false);

            // draw a green circle with 50 segments with line to center
            gl.glLineWidth(2);
            gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
            Primitives.drawCircle(gl, s.width / 2, s.height / 2, 50, CCMacros.CC_DEGREES_TO_RADIANS(90), 50, true);

            // open yellow poly
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glLineWidth(10);
            CCPoint vertices[] = {CCPoint.ccp(0, 0), CCPoint.ccp(50, 50), CCPoint.ccp(100, 50), CCPoint.ccp(100, 100), CCPoint.ccp(50, 100)};
            Primitives.drawPoly(gl, vertices, 5, false);

            // closed purple poly
            gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
            gl.glLineWidth(2);
            CCPoint vertices2[] = {CCPoint.ccp(30, 130), CCPoint.ccp(30, 230), CCPoint.ccp(50, 200)};
            Primitives.drawPoly(gl, vertices2, 3, true);

            // draw quad bezier path
            Primitives.drawQuadBezier(gl, 0,s.height, s.width/2,s.height/2, s.width, s.height, 50);

            // draw cubic bezier path
            Primitives.drawCubicBezier(gl, s.width/2, s.height/2, s.width/2+30, s.height/2+50,
                    s.width/2+60, s.height/2-50, s.width, s.height/2,100);


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
