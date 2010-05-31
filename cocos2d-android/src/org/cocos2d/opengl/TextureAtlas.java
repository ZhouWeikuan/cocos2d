package org.cocos2d.opengl;

import org.cocos2d.nodes.TextureManager;
import org.cocos2d.types.CCColor4B;
import org.cocos2d.types.CCQuad2;
import org.cocos2d.types.CCQuad3;
import org.cocos2d.utils.CCFormatter;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * A class that implements a Texture Atlas.
 * Supported features:
 * The atlas file can be a PNG or any other format supported by Texture2D
 * Quads can be udpated in runtime
 * Quads can be added in runtime
 * Quads can be removed in runtime
 * Quads can be re-ordered in runtime
 * The TextureAtlas capacity can be increased or decreased in runtime
 * Color array created on demand
 * The quads are rendered using an OpenGL ES vertex array list
 */
public class TextureAtlas {

    private static final String TAG = TextureAtlas.class.getSimpleName();

    private int totalQuads_;
    private int capacity_;

    private Texture2D texture_;

    private FloatBuffer textureCoordinates;
    private FloatBuffer vertexCoordinates;
    private ByteBuffer colors;
    private ShortBuffer indices;

    private boolean withColorArray_;

    public int getTotalQuads() {
        return totalQuads_;
    }

    public int capacity() {
        return capacity_;
    }

    public Texture2D getTexture() {
        return texture_;
    }

    public void setTexture(Texture2D tex) {
        texture_ = tex;
    }

    public boolean withColorArray() {
        return withColorArray_;
    }


    public TextureAtlas(String file, int n) {
        this(TextureManager.sharedTextureManager().addImage(file), n);
    }

    public TextureAtlas(Texture2D tex, int n) {
        capacity_ = n;

        texture_ = tex;

        withColorArray_ = false;


        ByteBuffer tbb = ByteBuffer.allocateDirect(CCQuad2.size * capacity_ * 4);
        tbb.order(ByteOrder.nativeOrder());
        textureCoordinates = tbb.asFloatBuffer();

        ByteBuffer vbb = ByteBuffer.allocateDirect(CCQuad3.size * capacity_ * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexCoordinates = vbb.asFloatBuffer();

        ByteBuffer isb = ByteBuffer.allocateDirect(6 * capacity_ * 2);
        isb.order(ByteOrder.nativeOrder());
        indices = isb.asShortBuffer();

        initIndices();
    }

    public String toString() {
        return new CCFormatter().format("<%s = %08X | getTotalQuads = %i>", TextureAtlas.class, this, totalQuads_);
    }

    public void initColorArray() {
        if (!withColorArray_) {
            // default color: 255,255,255,255
        	// modify by zt: A Texture in TextureAtlas need four colors for four vertices
            ByteBuffer cbb = ByteBuffer.allocateDirect(4 * capacity_ *CCColor4B.size* 1);
            colors = cbb;
            for (int i = 0; i < 4 * CCColor4B.size * capacity_ * 1; i++) {
                colors.put(i, (byte) 0xff);
            }
            colors.position(0);

            withColorArray_ = true;
        }
    }

    public void initIndices() {
        for (int i = 0; i < capacity_; i++) {
            indices.put((short) (i * 6 + 0), (short) (i * 4 + 0));
            indices.put((short) (i * 6 + 1), (short) (i * 4 + 1));
            indices.put((short) (i * 6 + 2), (short) (i * 4 + 2));

            // inverted index.
            indices.put((short) (i * 6 + 5), (short) (i * 4 + 1));
            indices.put((short) (i * 6 + 4), (short) (i * 4 + 2));
            indices.put((short) (i * 6 + 3), (short) (i * 4 + 3));
        }
        indices.position(0);
    }

    public void updateQuad(CCQuad2 quadT, CCQuad3 quadV, int index) {
        assert (index >= 0 && index < capacity_) : "update quad with texture_: Invalid index";

        totalQuads_ = Math.max(index + 1, totalQuads_);

        putTexCoords(textureCoordinates, quadT.ccQuad2(), index);
        putVertex(vertexCoordinates, quadV.ccQuad3(), index);
    }

    public void updateColor(CCColor4B color, int index) {
        assert (index >= 0 && index < capacity_) : "update color with quad color: Invalid index";

        totalQuads_ = Math.max(index + 1, totalQuads_);

        if (!withColorArray_)
            initColorArray();

        if (withColorArray_)
            putColor(colors, color.ccColor4B(), index);
    }

    public void insertQuad(CCQuad2 texCoords, CCQuad3 vertexCoords, int index) {
        assert (index >= 0 && index < capacity_) : "insert quad with texture_: Invalid index";

        totalQuads_++;

        int remaining = (totalQuads_ - 1) - index;

        // last object doesn't need to be moved
        if (remaining > 0) {
            // tex coordinates
            arraycopyTexture(textureCoordinates, index, textureCoordinates, index + 1, remaining);

            // vertexCoordinates_
            arraycopyVertex(vertexCoordinates, index, vertexCoordinates, index + 1, remaining);

            // colors_
            if (withColorArray_) {
                arraycopyColor(colors, index, colors, index + 1, remaining);
            }
        }
        putTexCoords(textureCoordinates, texCoords.ccQuad2(), index);
        putVertex(vertexCoordinates, vertexCoords.ccQuad3(), index);
    }


    public void insertQuad(int from, int to) {
        assert (to >= 0 && to < totalQuads_) : "insertQuadFromIndex:atIndex: Invalid index";
        assert (from >= 0 && from < totalQuads_) : "insertQuadFromIndex:atIndex: Invalid index";

        if (from == to)
            return;

        int size = Math.abs(from - to);
        int dst = from;
        int src = from + 1;

        if (from > to) {
            dst = to + 1;
            src = to;
        }

        // tex coordinates
        float[] texCoordsBackup = getTexCoords(textureCoordinates, from);
        arraycopyTexture(textureCoordinates, src, textureCoordinates, dst, size);
        putTexCoords(textureCoordinates, texCoordsBackup, to);

        // vertexCoordinates_ coordinates
        float[] vertexQuadBackup = getVertex(vertexCoordinates, from);
        arraycopyVertex(vertexCoordinates, src, vertexCoordinates, dst, size);
        putVertex(vertexCoordinates, vertexQuadBackup, to);

        // colors_
        if (withColorArray_) {
            byte[] colorsBackup = getColor(colors, from);
            arraycopyColor(colors, src, colors, dst, size);
            putColor(colors, colorsBackup, to);
        }
    }

    public void removeQuad(int index) {
        assert (index >= 0 && index < totalQuads_) : "removeQuadAtIndex: Invalid index";

        int remaining = (totalQuads_ - 1) - index;

        // last object doesn't need to be moved
        if (remaining > 0) {
            // tex coordinates
            arraycopyTexture(textureCoordinates, index + 1, textureCoordinates, index, remaining);

            // vertexCoordinates_
            arraycopyVertex(vertexCoordinates, index + 1, vertexCoordinates, index, remaining);

            // colors_
            if (withColorArray_) {
                arraycopyColor(colors, index + 1, colors, index, remaining);
            }
        }

        totalQuads_--;
    }

    public void removeAllQuads() {
        totalQuads_ = 0;
    }


    public void resizeCapacity(int newCapacity) {
        if (newCapacity == capacity_)
            return;

        // update capacity and getTotalQuads
        totalQuads_ = Math.min(totalQuads_, newCapacity);

        capacity_ = newCapacity;

        ByteBuffer tbb = ByteBuffer.allocateDirect(CCQuad2.size * newCapacity * 4);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer tmpTexCoords = tbb.asFloatBuffer();
        tmpTexCoords.put(textureCoordinates);
        textureCoordinates = tmpTexCoords;
        textureCoordinates.position(0);

        ByteBuffer vbb = ByteBuffer.allocateDirect(CCQuad3.size * newCapacity * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer tmpVertexCoords = vbb.asFloatBuffer();
        tmpVertexCoords.put(vertexCoordinates);
        vertexCoordinates = tmpVertexCoords;
        vertexCoordinates.position(0);

        ByteBuffer isb = ByteBuffer.allocateDirect(6 * newCapacity * 2);
        isb.order(ByteOrder.nativeOrder());
        ShortBuffer tmpIndices = isb.asShortBuffer();
        tmpIndices.put(indices);
        indices = tmpIndices;
        indices.position(0);

        initIndices();

        if (withColorArray_) {
            ByteBuffer cbb = ByteBuffer.allocateDirect(4*CCColor4B.size * newCapacity * 1);
            ByteBuffer tmpColors = cbb;
            tmpColors.put(colors);
            colors = tmpColors;
            colors.position(0);
        }
    }

    public void drawQuads(GL10 gl) {
        draw(gl, totalQuads_);
    }

    public void draw(GL10 gl, int n) {
        texture_.loadTexture(gl);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_.name());

        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexCoordinates);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinates);

        if (withColorArray_)
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, colors);

        gl.glDrawElements(GL10.GL_TRIANGLES, n * 6, GL10.GL_UNSIGNED_SHORT, indices);

    }

    private float[] getTexCoords(FloatBuffer src, int index) {
        float[] quadT = new float[CCQuad2.size];
        for (int i = 0; i < CCQuad2.size; i++) {
            quadT[i] = src.get(index * CCQuad2.size + i);
        }
        return quadT;
    }

    private void putTexCoords(FloatBuffer dst, float[] quadT, int index) {
        for (int i = 0; i < CCQuad2.size; i++) {
            dst.put(index * CCQuad2.size + i, quadT[i]);
        }
    }

    private float[] getVertex(FloatBuffer src, int index) {
        float[] quadV = new float[CCQuad3.size];
        for (int i = 0; i < CCQuad3.size; i++) {
            quadV[i] = src.get(index * CCQuad3.size + i);
        }
        return quadV;
    }

    private void putVertex(FloatBuffer dst, float[] quadV, int index) {
        for (int i = 0; i < CCQuad3.size; i++) {
            dst.put(index * CCQuad3.size + i, quadV[i]);
        }
    }

    private byte[] getColor(ByteBuffer src, int index) {
        byte[] color = new byte[CCColor4B.size * 4];
        for(int j=0; j<4; ++j)
        {
            for (int i = 0; i < CCColor4B.size; ++i) {
                color[i] = src.get(index * CCColor4B.size *4 + 4*j + i);
            }        	
        }

        return color;
    }

    private void putColor(ByteBuffer dst, byte[] color, int index) {
    	for(int j=0; j<4; ++j)
    	{
    		for (int i = 0; i < CCColor4B.size; ++i)
    		{
        		dst.put(index * CCColor4B.size * 4 + 4*j + i, color[i]);
        	}
        }
    }

    private void arraycopyTexture(FloatBuffer src, int srcPos, FloatBuffer dst, int dstPos, int length) {
        if (src == dst) {
            memmoveFloat(src, srcPos * CCQuad2.size, dst, dstPos * CCQuad2.size, length * CCQuad2.size);
        } else {
            memcopyFloat(src, srcPos * CCQuad2.size, dst, dstPos * CCQuad2.size, length * CCQuad2.size);
        }
    }

    private void arraycopyVertex(FloatBuffer src, int srcPos, FloatBuffer dst, int dstPos, int length) {
        if (src == dst) {
            memmoveFloat(src, srcPos * CCQuad3.size, dst, dstPos * CCQuad3.size, length * CCQuad3.size);
        } else {
            memcopyFloat(src, srcPos * CCQuad3.size, dst, dstPos * CCQuad3.size, length * CCQuad3.size);
        }
    }

    private void arraycopyColor(ByteBuffer src, int srcPos, ByteBuffer dst, int dstPos, int length) {
        if (src == dst) {
            memmoveByte(src, srcPos * CCColor4B.size * 4, dst, dstPos * CCColor4B.size*4, length * CCColor4B.size * 4);
        } else {
            memcopyByte(src, srcPos * CCColor4B.size * 4, dst, dstPos * CCColor4B.size*4, length * CCColor4B.size * 4);
        }
    }

    private void memmoveFloat(FloatBuffer src, int from, FloatBuffer dst, int to, int size) {
        if (to < from) {
            memcopyFloat(src, from, dst, to, size);
        } else {
            for (int i = size - 1; i >= 0; i--) {
                dst.put(i + to, src.get(i + from));
            }
        }
    }

    private void memcopyFloat(FloatBuffer src, int from, FloatBuffer dst, int to, int size) {
        for (int i = 0; i < size; i++) {
            dst.put(i + to, src.get(i + from));
        }
    }

    private void memmoveByte(ByteBuffer src, int from, ByteBuffer dst, int to, int size) {
        if (to < from) {
            memcopyByte(src, from, dst, to, size);
        } else {
            for (int i = size - 1; i >= 0; i--) {
                dst.put(i + to, src.get(i + from));
            }
        }
    }

    private void memcopyByte(ByteBuffer src, int from, ByteBuffer dst, int to, int size) {
        for (int i = 0; i < size; i++) {
            dst.put(i + to, src.get(i + from));
        }
    }

}
