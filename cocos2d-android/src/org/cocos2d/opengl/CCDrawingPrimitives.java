package org.cocos2d.opengl;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_LINES;
import static javax.microedition.khronos.opengles.GL10.GL_LINE_LOOP;
import static javax.microedition.khronos.opengles.GL10.GL_LINE_STRIP;
import static javax.microedition.khronos.opengles.GL10.GL_POINTS;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.utils.FastFloatBuffer;

/**
 @file
 Drawing OpenGL ES primitives.
  - drawPoint
  - drawLine
  - drawPoly
  - drawCircle
 
 You can change the color, width and other property by calling the
 glColor4ub(), glLineWitdh(), glPointSize().
 
 @warning These functions draws the Line, Point, Polygon, immediately. They aren't batched. If you are going to make a game that depends on these primitives, I suggest creating a batch.
*/
public class CCDrawingPrimitives {
	
	private static FastFloatBuffer tmpFloatBuf;
	
	private static FastFloatBuffer getVertices(int size) {
		if(tmpFloatBuf == null || tmpFloatBuf.capacity() < size) {
	        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * size);
	        vbb.order(ByteOrder.nativeOrder());
	        tmpFloatBuf = FastFloatBuffer.createBuffer(vbb);
		}
		tmpFloatBuf.rewind();
		return tmpFloatBuf;
	}
	
    /** draws a point given x and y coordinate */
    public static void ccDrawPoint(GL10 gl, CGPoint pnt) {
//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 1);
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * 1);

        vertices.put(pnt.x);
        vertices.put(pnt.y);
        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        gl.glDrawArrays(GL_POINTS, 0, 1);

        // restore default state
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	
    }

    /** draws an array of points.
      @since v0.7.2
    */
    public static void ccDrawPoints(GL10 gl, CGPoint points[], int numberOfPoints) {
//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * numberOfPoints);
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            vertices.put(points[i].x);
            vertices.put(points[i].y);
        }
        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        gl.glDrawArrays(GL_POINTS, 0, numberOfPoints);

        // restore default state
        gl.glEnableClientState(GL_COLOR_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL_TEXTURE_2D);	
    }

    /** draws a line given the origin and destination point */
    public static void ccDrawLine(GL10 gl, CGPoint origin, CGPoint destination) {
//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 2);
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * 2);

        vertices.put(origin.x);
        vertices.put(origin.y);
        vertices.put(destination.x);
        vertices.put(destination.y);
        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        gl.glDrawArrays(GL_LINES, 0, 2);

        // restore default state
        gl.glEnableClientState(GL_COLOR_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL_TEXTURE_2D);	
    }

    public static void ccDrawRect(GL10 gl, CGRect rect) {
        CGPoint[] poli = new CGPoint[4];

        poli[0] = CGPoint.ccp(rect.origin.x, rect.origin.y);
        poli[1] = CGPoint.ccp(rect.origin.x + rect.size.width, rect.origin.y);
        poli[2] = CGPoint.ccp(rect.origin.x + rect.size.width, rect.origin.y + rect.size.height);
        poli[3] = CGPoint.ccp(rect.origin.x, rect.origin.y + rect.size.height);

        ccDrawPoly(gl, poli, poli.length, true);
    }

    /**
     * draws a poligon given a pointer to CGPoint coordiantes and the number of vertices.
     * The polygon can be closed or open
    */
    public static void ccDrawPoly(GL10 gl, CGPoint poli[], int numberOfPoints, boolean closePolygon) {
//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * numberOfPoints);
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            vertices.put(poli[i].x);
            vertices.put(poli[i].y);
        }
        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        if (closePolygon)
            gl.glDrawArrays(GL_LINE_LOOP, 0, numberOfPoints);
        else
            gl.glDrawArrays(GL_LINE_STRIP, 0, numberOfPoints);

        // restore default state
        gl.glEnableClientState(GL_COLOR_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL_TEXTURE_2D);	
    }

    /** draws a circle given the center, radius and number of segments. */
    public static void ccDrawCircle(GL10 gl, CGPoint center, float r, float a,
            int segments, boolean drawLineToCenter) {

//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * (segments + 2));
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * (segments + 2));

        int additionalSegment = 1;

        if (drawLineToCenter)
            additionalSegment++;

        final float coef = 2.0f * (float) Math.PI / segments;
        for (int i = 0; i <= segments; i++) {
            float rads = i * coef;
            float j = (float) (r * Math.cos(rads + a) + center.x);
            float k = (float) (r * Math.sin(rads + a) + center.y);

            vertices.put(j);
            vertices.put(k);
        }
        vertices.put(center.x);
        vertices.put(center.y);
        
        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        gl.glDrawArrays(GL_LINE_STRIP, 0, segments + additionalSegment);

        // restore default state
        gl.glEnableClientState(GL_COLOR_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL_TEXTURE_2D);	
    }

    /** draws a quad bezier path since v0.8 */
    public static void ccDrawQuadBezier(GL10 gl, CGPoint origin, CGPoint control,
            CGPoint destination, int segments) {

//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * (segments + 1));
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * (segments + 1));
        
        float t = 0.0f;
        for(int i = 0; i < segments; i++) {
            float x = (float)Math.pow(1 - t, 2) * origin.x + 2.0f * (1 - t) * t * control.x + t * t * destination.x;
            float y = (float)Math.pow(1 - t, 2) * origin.y + 2.0f * (1 - t) * t * control.y + t * t * destination.y;
            vertices.put(x);
            vertices.put(y);
            t += 1.0f / segments;
        }
        vertices.put(destination.x);
        vertices.put(destination.y);

        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        gl.glDrawArrays(GL_LINE_STRIP, 0, segments + 1);

        // restore default state
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	
    }

    /**
     * draws a cubic bezier path
     @since v0.8
    */
    public static void ccDrawCubicBezier(GL10 gl, CGPoint origin, CGPoint control1, CGPoint control2,
            CGPoint destination, int segments) {

//        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * (segments + 1));
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertices = vbb.asFloatBuffer();
        FastFloatBuffer vertices = getVertices(2 * (segments + 1));

        float t = 0;
        for(int i = 0; i < segments; i++)
        {
            float x = (float)Math.pow(1 - t, 3) * origin.x + 3.0f * (float)Math.pow(1 - t, 2) * t * control1.x + 3.0f * (1 - t) * t * t * control2.x + t * t * t * destination.x;
            float y = (float)Math.pow(1 - t, 3) * origin.y + 3.0f * (float)Math.pow(1 - t, 2) * t * control1.y + 3.0f * (1 - t) * t * t * control2.y + t * t * t * destination.y;
            vertices.put(x);
            vertices.put(y);
            t += 1.0f / segments;
        }
        vertices.put(destination.x);
        vertices.put(destination.y);

        vertices.position(0);

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, 
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY, GL_COLOR_ARRAY	
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
        gl.glDrawArrays(GL_LINE_STRIP, 0, segments + 1);
        
        // restore default state
        gl.glEnableClientState(GL_COLOR_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL_TEXTURE_2D);	

    }
    
}

