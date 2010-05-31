package org.cocos2d.opengl;

import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCRect;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Primitives {

    public static void drawPoint(GL10 gl, float x, float y) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 1);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        vertices.put(x);
        vertices.put(y);
        
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);

        gl.glDrawArrays(GL_POINTS, 0, 1);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
    }

    public static void drawPoints(GL10 gl, CCPoint points[], int numberOfPoints) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * numberOfPoints);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        for (int i = 0; i < numberOfPoints; i++) {
            vertices.put(points[i].x);
            vertices.put(points[i].y);
        }
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);

        gl.glDrawArrays(GL_POINTS, 0, numberOfPoints);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
    }


    public static void drawLine(GL10 gl, CCPoint origin, CCPoint destination) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 2);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        vertices.put(origin.x);
        vertices.put(origin.y);
        vertices.put(destination.x);
        vertices.put(destination.y);
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);

        gl.glDrawArrays(GL_LINES, 0, 2);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
    }

    public static void drawRect(GL10 gl, CCRect rect) {
        CCPoint[] poli = new CCPoint[4];

        poli[0] = CCPoint.ccp(rect.origin.x, rect.origin.y);
        poli[1] = CCPoint.ccp(rect.origin.x + rect.size.width, rect.origin.y);
        poli[2] = CCPoint.ccp(rect.origin.x + rect.size.width, rect.origin.y + rect.size.height);
        poli[3] = CCPoint.ccp(rect.origin.x, rect.origin.y + rect.size.height);

        drawPoly(gl, poli, poli.length, true);
    }

    public static void drawPoly(GL10 gl, CCPoint poli[], int numberOfPoints, boolean closePolygon) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * numberOfPoints);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        for (int i = 0; i < numberOfPoints; i++) {
            vertices.put(poli[i].x);
            vertices.put(poli[i].y);
        }
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);

        if (closePolygon)
            gl.glDrawArrays(GL_LINE_LOOP, 0, numberOfPoints);
        else
            gl.glDrawArrays(GL_LINE_STRIP, 0, numberOfPoints);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
    }

    public static void drawCircle(GL10 gl, float centerX, float centerY, float r, float a, int segments, boolean drawLineToCenter) {

        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * (segments + 2));
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        int additionalSegment = 1;

        if (drawLineToCenter)
            additionalSegment++;

        final float coef = 2.0f * (float) Math.PI / segments;

        for (int i = 0; i <= segments; i++) {
            float rads = i * coef;
            float j = (float) (r * Math.cos(rads + a) + centerX);
            float k = (float) (r * Math.sin(rads + a) + centerY);

            vertices.put(j);
            vertices.put(k);
        }
        vertices.put(centerX);
        vertices.put(centerY);
        
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);

        gl.glDrawArrays(GL_LINE_STRIP, 0, segments + additionalSegment);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
    }
    
    public static void drawQuadBezier(GL10 gl,float originX, float originY, float controlX, float controlY, float destinationX, float destinationY, int segments)
    {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * (segments + 1));
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();
        
        float t = 0.0f;
        for(int i = 0; i < segments; i++)
        {
            float x = (float)Math.pow(1 - t, 2) * originX + 2.0f * (1 - t) * t * controlX + t * t * destinationX;
            float y = (float)Math.pow(1 - t, 2) * originY + 2.0f * (1 - t) * t * controlY + t * t * destinationY;
            vertices.put(x);
            vertices.put(y);
            t += 1.0f / segments;
        }
        vertices.put(destinationX);
        vertices.put(destinationY);

        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        
        gl.glDrawArrays(GL_LINE_STRIP, 0, segments + 1);
        
        gl.glDisableClientState(GL_VERTEX_ARRAY);
    }
    
    public static void drawCubicBezier(GL10 gl, float originX, float originY, float control1X, float control1Y, float control2X, float control2Y, float destinationX, float destinationY, int segments)
    {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * (segments + 1));
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        float t = 0;
        for(int i = 0; i < segments; i++)
        {
            float x = (float)Math.pow(1 - t, 3) * originX + 3.0f * (float)Math.pow(1 - t, 2) * t * control1X + 3.0f * (1 - t) * t * t * control2X + t * t * t * destinationX;
            float y = (float)Math.pow(1 - t, 3) * originY + 3.0f * (float)Math.pow(1 - t, 2) * t * control1Y + 3.0f * (1 - t) * t * t * control2Y + t * t * t * destinationY;
            vertices.put(x);
            vertices.put(y);
            t += 1.0f / segments;
        }
        vertices.put(destinationX);
        vertices.put(destinationY);

        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        
        gl.glDrawArrays(GL_LINE_STRIP, 0, segments + 1);
        
        gl.glDisableClientState(GL_VERTEX_ARRAY);  
    }
    

}
