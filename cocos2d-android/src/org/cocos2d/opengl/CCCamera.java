package org.cocos2d.opengl;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccMacros;
import org.cocos2d.utils.CCFormatter;

import android.opengl.GLU;

/** 
    A Camera is used in every CocosNode.
    Useful to look at the object from different views.
    The OpenGL gluLookAt() function is used to locate the
    camera.

    If the object is transformed by any of the scale, rotation or
    position attributes, then they will override the camera.
 
	IMPORTANT: Either your use the camera or the rotation/scale/position properties. You can't use both.
    World coordinates won't work if you use the camera.

    Limitations:
 
     - Some nodes, like CCParallaxNode, CCParticle uses world node coordinates, and they won't work properly if you move them (or any of their ancestors)
       using the camera.
*/
public class CCCamera {
    private float eyeX;
    private float eyeY;
    private float eyeZ;

    private float centerX;
    private float centerY;
    private float centerZ;

    private float upX;
    private float upY;
    private float upZ;

    private boolean dirty;

    /** whether of not the camera is dirty */
    public boolean getDirty() {
        return dirty;
    }

    public void setDirty(boolean value) {
        dirty = value;
    }

    public CCCamera() {
        restore();
    }

    public String toString() {
        return new CCFormatter().format("<%s = %08X | center = (%.2f,%.2f,%.2f)>", this.getClass(), this, centerX, centerY, centerZ);
    }

    /** sets the camera in the defaul position */
    public void restore() {
        eyeX = 0;
        eyeY = 0;
        eyeZ = CCCamera.getZEye();

        centerX = 0;
        centerY = 0;
        centerZ = 0.0f;

        upX = 0.0f;
        upY = 1.0f;
        upZ = 0.0f;

        dirty = false;
    }


    /** Sets the camera using gluLookAt using its eye, center and up_vector */
    public void locate(GL10 gl) {
        if( dirty ) {
            GLU.gluLookAt(gl, eyeX, eyeY, eyeZ,
                    centerX, centerY, centerZ,
                    upX, upY, upZ);
        }
    }

    /** returns the Z eye */
    public static float getZEye() {
        return ccMacros.FLT_EPSILON;
        // CGSize s = CCDirector.sharedDirector().displaySize();
        // return (s.height / 1.1566f);
    }

    /** sets the eye values */
    public void setEye(float x, float y, float z) {
        eyeX = x;
        eyeY = y;
        eyeZ = z;
        dirty = true;
    }

    /** sets the center values */
    public void setCenter(float x, float y, float z) {
        centerX = x;
        centerY = y;
        centerZ = z;
        dirty = true;
    }

    /** sets the up values */
    public void setUp(float x, float y, float z) {
        upX = x;
        upY = y;
        upZ = z;
        dirty = true;
    }

    /** get the eye vector values */
    public void getEye(float x[], float y[], float z[]) {
        x[0] = eyeX;
        y[0] = eyeY;
        z[0] = eyeZ;
    }

    /** get the center vector values */
    public void getCenter(float x[], float y[], float z[]) {
        x[0] = centerX;
        y[0] = centerY;
        z[0] = centerZ;
    }

    /** get the up vector values */
    public void getUp(float x[], float y[], float z[]) {
        x[0] = upX;
        y[0] = upY;
        z[0] = upZ;
    }
}

