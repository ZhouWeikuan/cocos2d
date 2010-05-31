package org.cocos2d.layers;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCColor4B;
import org.cocos2d.types.CCMacros;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ColorLayer extends Layer implements CocosNode.CocosNodeRGBA, CocosNode.CocosNodeSize {
    protected CCColor3B color_;
    protected int opacity_;

    private FloatBuffer squareVertices_;
    private ByteBuffer squareColors_;

    public static ColorLayer node(CCColor4B color) {
        return new ColorLayer(color, Director.sharedDirector().winSize().width, Director.sharedDirector().winSize().height);
    }

    public static ColorLayer node(CCColor4B color, float w, float h) {
        return new ColorLayer(color, w, h);
    }

    protected ColorLayer(CCColor4B color) {
        this(color, Director.sharedDirector().winSize().width, Director.sharedDirector().winSize().height);
    }

    protected ColorLayer(CCColor4B color, float w, float h) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        squareVertices_ = vbb.asFloatBuffer();

        squareColors_ = ByteBuffer.allocateDirect(4 * 4);

        color_ = new CCColor3B(color.r, color.g, color.b);
        opacity_ = color.a;

        for (int i = 0; i < (4 * 2); i++) {
            squareVertices_.put(i, 0);
        }
        squareVertices_.position(0);

        updateColor();
        setContentSize(w, h);
    }

    private void updateColor() {
        for (int i = 0; i < squareColors_.limit(); i++) {
            switch (i % 4) {
                case 0:
                    squareColors_.put(i, (byte) color_.r);
                    break;
                case 1:
                    squareColors_.put(i, (byte) color_.g);
                    break;
                case 2:
                    squareColors_.put(i, (byte) color_.b);
                    break;
                default:
                    squareColors_.put(i, (byte) opacity_);
            }
            squareColors_.position(0);
        }
    }

    @Override
    public void draw(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);


        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices_);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, squareColors_);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        if (opacity_ != 255)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);


        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if (opacity_ != 255)
            gl.glBlendFunc(CCMacros.CC_BLEND_SRC, CCMacros.CC_BLEND_DST);

        // Clear the vertex and color arrays
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

    }


    public CCColor3B getColor() {
        return new CCColor3B(color_.r, color_.g, color_.b);
    }

    // Color Protocol
    public void setColor(CCColor3B color) {
        color_.r = color.r;
        color_.g = color.g;
        color_.b = color.b;
        updateColor();
    }

    // Opacity Protocol

    public void setOpacity(int o) {
        opacity_ = o;
        updateColor();
    }

    public int getOpacity() {
        return opacity_;
    }

    // Size protocol

    @Override
    public float getWidth() {
        return squareVertices_.get(2);
    }

    @Override
    public float getHeight() {
        return squareVertices_.get(5);
    }

    @Override
    public void setContentSize(float w, float h) {

        // Layer default ctor calls setContentSize priot to nio alloc
        if (squareVertices_ != null) {
            squareVertices_.put(2, w);
            squareVertices_.put(5, h);
            squareVertices_.put(6, w);
            squareVertices_.put(7, h);
        }

        super.setContentSize(w, h);
    }

    public void changeWidthAndHeight(float w, float h) {
        setContentSize(w, h);
    }

    public void changeWidth(float w) {
        setContentSize(w, getHeight());
    }

    public void changeHeight(float h) {
        setContentSize(getWidth(), h);
    }

}

