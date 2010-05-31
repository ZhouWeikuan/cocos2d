package org.cocos2d.box2d;

import static org.box2d.dynamics.BBWorldCallbacks.*;
import org.box2d.common.BBVec2;
import org.box2d.common.BBTransform;
import static org.box2d.collision.BBCollision.*;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLESDebugDraw extends BBDebugDraw {
    float mRatio;

    public GLESDebugDraw() {
        this(1.0f);
    }

    public GLESDebugDraw( float ratio ) {
        mRatio = ratio;
    }

    public void drawPolygon(GL10 gl, final BBVec2[] old_vertices, int vertexCount, final BBColor color) {

        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * vertexCount);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();

        for( int i=0;i< vertexCount;i++) {
            vertices.put(old_vertices[i].x * mRatio);
            vertices.put(old_vertices[i].y * mRatio);
        }
        vertices.position(0);

        gl.glColor4f(color.r, color.g, color.b,1);
        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glDrawArrays(GL_LINE_LOOP, 0, vertexCount);

    }

    public void drawSolidPolygon(GL10 gl, final BBVec2[] old_vertices, int vertexCount, final BBColor color) {
        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * vertexCount);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();

        for( int i=0;i< vertexCount;i++) {
            vertices.put(old_vertices[i].x * mRatio);
            vertices.put(old_vertices[i].y * mRatio);
        }
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);

        gl.glColor4f(color.r, color.g, color.b,0.5f);
        gl.glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

        gl.glColor4f(color.r, color.g, color.b,1);
        gl.glDrawArrays(GL_LINE_LOOP, 0, vertexCount);
    }

    public void drawCircle(GL10 gl, final BBVec2 center, float radius, final BBColor color) {
        final float k_segments = 16.0f;
        int vertexCount = 16;
        final float k_increment = 2.0f * (float)Math.PI / k_segments;
        float theta = 0.0f;

        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * vertexCount);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();

        for( int i=0;i< vertexCount;i++) {
            BBVec2 v = new BBVec2((float)Math.cos(theta), (float)Math.sin(theta)).mul(radius).add(center);
            vertices.put(v.x * mRatio);
            vertices.put(v.y * mRatio);
            theta += k_increment;
        }
        vertices.position(0);

        gl.glColor4f(color.r, color.g, color.b, 1.0f);
        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);

        gl.glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);
    }

    public void drawSolidCircle(GL10 gl, final BBVec2 center, float radius, final BBVec2 axis, final BBColor color) {
        float k_segments = 16.0f;
        int vertexCount=16;
        final float k_increment = 2.0f * (float)Math.PI / k_segments;
        float theta = 0.0f;

        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * vertexCount);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();
        for (int i = 0; i < vertexCount; ++i) {
            BBVec2 v = new BBVec2((float)Math.cos(theta), (float)Math.sin(theta)).mul(radius).add(center);
            vertices.put(v.x * mRatio);
            vertices.put(v.y * mRatio);
            theta += k_increment;
        }
        vertices.position(0);

        gl.glColor4f(color.r, color.g, color.b,0.5f);
        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);
        gl.glColor4f(color.r, color.g, color.b,1);
        gl.glDrawArrays(GL_LINE_LOOP, 0, vertexCount);

        // Draw the axis line
        drawSegment(gl, center, center.add(axis.mul(radius)), color);
    }

    public void drawSegment(GL10 gl, final BBVec2 p1, final BBVec2 p2, final BBColor color) {

        gl.glColor4f(color.r, color.g, color.b,1);

        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * 2);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();

        vertices.put(p1.x * mRatio);
        vertices.put(p1.y * mRatio);
        vertices.put(p2.x * mRatio);
        vertices.put(p2.y * mRatio);
        vertices.position(0);


        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glDrawArrays(GL_LINES, 0, 2);
    }

    public void drawXForm(GL10 gl, final BBTransform xf) {}

    void DrawTransform(GL10 gl, final BBTransform xf) {
        BBVec2 p1 = xf.position, p2;
        final float k_axisScale = 0.4f;

        p2 = p1.add(xf.R.col1.mul(k_axisScale));
        drawSegment(gl, p1, p2, new BBColor(1,0,0));

        p2 = p1.add(xf.R.col2.mul(k_axisScale));
        drawSegment(gl, p1,p2, new BBColor(0,1,0));
    }

    public void DrawPoint(GL10 gl, final BBVec2 p, float size, final BBColor color) {

        gl.glColor4f(color.r, color.g, color.b,1);

        gl.glPointSize(size);

        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * 1);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();
        vertices.put(p.x * mRatio);
        vertices.put(p.y * mRatio);
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glDrawArrays(GL_POINTS, 0, 1);
        gl.glPointSize(1.0f);
    }

    public void DrawString(GL10 gl, int x, int y, String... string) {
        // NOT IMPLEMENTED
    }

    public void DrawAABB(GL10 gl, BBAABB aabb, final BBColor color) {
        int vertexCount=4;

        gl.glColor4f(color.r, color.g, color.b,1);

        ByteBuffer fb = ByteBuffer.allocateDirect(4 * 2 * 4);
        fb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = fb.asFloatBuffer();

        vertices.put(aabb.lowerBound.x * mRatio);
        vertices.put(aabb.lowerBound.y * mRatio);
        vertices.put(aabb.upperBound.x * mRatio);
        vertices.put(aabb.lowerBound.y * mRatio);
        vertices.put(aabb.upperBound.x * mRatio);
        vertices.put(aabb.upperBound.y * mRatio);
        vertices.put(aabb.lowerBound.x * mRatio);
        vertices.put(aabb.upperBound.y * mRatio);
        vertices.position(0);

        gl.glVertexPointer(2, GL_FLOAT, 0, vertices);
        gl.glDrawArrays(GL_LINE_LOOP, 0, 8);
    }

}
