package org.cocos2d.opengl;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLU {

    private static FloatBuffer fixedM;

    static {
        ByteBuffer fbb = ByteBuffer.allocateDirect(4 * 4 * 4);
        fbb.order(ByteOrder.nativeOrder());
        fixedM = fbb.asFloatBuffer();

        fixedM.put(0+3*4, 0.0f);

        fixedM.put(1+3*4, 0.0f);

        fixedM.put(2+3*4, 0.0f);

        fixedM.put(3+0*4, 0.0f);
        fixedM.put(3+1*4, 0.0f);
        fixedM.put(3+2*4, 0.0f);
        fixedM.put(3+3*4, 1.0f);

        fixedM.position(0);
    }

    public static void gluPerspective(GL10 gl, float fovy, float aspect, float zNear, float zFar)
    {
        float xmin, xmax, ymin, ymax;

        ymax = zNear * (float)Math.tan(fovy * (float)Math.PI / 360);
        ymin = -ymax;
        xmin = ymin * aspect;
        xmax = ymax * aspect;

        gl.glFrustumf(xmin, xmax,
                    ymin, ymax,
                    zNear, zFar);
    }

    public static void gluLookAt(GL10 gl,
                          float eyex, float eyey, float eyez,
                          float centerx, float centery, float centerz,
                          float upx, float upy, float upz) {
        float[] x= new float[3], y = new float[3], z = new float[3];
        float mag;

        /* Make rotation matrix */

        /* Z vector */
        z[0] = eyex - centerx;
        z[1] = eyey - centery;
        z[2] = eyez - centerz;
        mag = (float)Math.sqrt(z[0] * z[0] + z[1] * z[1] + z[2] * z[2]);
        if (mag != 0.0f) {
            z[0] /= mag;
            z[1] /= mag;
            z[2] /= mag;
        }

        /* Y vector */
        y[0] = upx;
        y[1] = upy;
        y[2] = upz;

        /* X vector = Y cross Z */
        x[0] = y[1] * z[2] - y[2] * z[1];
        x[1] = -y[0] * z[2] + y[2] * z[0];
        x[2] = y[0] * z[1] - y[1] * z[0];

        /* Recompute Y = Z cross X */
        y[0] = z[1] * x[2] - z[2] * x[1];
        y[1] = -z[0] * x[2] + z[2] * x[0];
        y[2] = z[0] * x[1] - z[1] * x[0];

        /* cross product gives area of parallelogram, which is < 1.0 for
         * non-perpendicular unit-length vectors; so normalize x, y here
         */

        mag = (float)Math.sqrt(x[0] * x[0] + x[1] * x[1] + x[2] * x[2]);
        if (mag != 0.0f) {
            x[0] /= mag;
            x[1] /= mag;
            x[2] /= mag;
        }

        mag = (float)Math.sqrt(y[0] * y[0] + y[1] * y[1] + y[2] * y[2]);
        if (mag != 0.0f) {
            y[0] /= mag;
            y[1] /= mag;
            y[2] /= mag;
        }

        fixedM.put(0+0*4, x[0]);
        fixedM.put(0+1*4, x[1]);
        fixedM.put(0+2*4, x[2]);
//      fixedM.put(0+3*4, 0.0f);

        fixedM.put(1+0*4, y[0]);
        fixedM.put(1+1*4, y[1]);
        fixedM.put(1+2*4, y[2]);
//      fixedM.put(1+3*4, 0.0f);

        fixedM.put(2+0*4, z[0]);
        fixedM.put(2+1*4, z[1]);
        fixedM.put(2+2*4, z[2]);
//      fixedM.put(2+3*4, 0.0f);

//      fixedM.put(3+0*4, 0.0f);
//      fixedM.put(3+1*4, 0.0f);
//      fixedM.put(3+2*4, 0.0f);
//      fixedM.put(3+3*4, 1.0f);

        fixedM.position(0);

        gl.glMultMatrixf(fixedM);

        /* Translate Eye to Origin */
        gl.glTranslatef(-eyex, -eyey, -eyez);
    }

}
