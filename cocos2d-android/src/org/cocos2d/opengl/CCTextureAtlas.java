package org.cocos2d.opengl;

import static javax.microedition.khronos.opengles.GL10.GL_REPEAT;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_S;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_T;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccQuad2;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.utils.CCFormatter;
import org.cocos2d.utils.FastFloatBuffer;

/** A class that implements a Texture Atlas.
 Supported features:
   * The atlas file can be a PVRTC, PNG or any other fomrat supported by Texture2D
   * Quads can be udpated in runtime
   * Quads can be added in runtime
   * Quads can be removed in runtime
   * Quads can be re-ordered in runtime
   * The TextureAtlas capacity can be increased or decreased in runtime
   * OpenGL component: V3F, C4B, T2F.
 The quads are rendered using an OpenGL ES VBO.
 To render the quads using an interleaved vertex array list, you should modify the ccConfig.h file 
 */
public class CCTextureAtlas {
    /** quantity of quads that are going to be drawn */
    private int totalQuads_;
    /** quantity of quads that can be stored with the current texture atlas size */
    private int capacity_;
    /** Texture of the texture atlas */
    private CCTexture2D texture_;

    /** buffers that are going to be rendered */
    private FastFloatBuffer textureCoordinates;
    public FastFloatBuffer getTexCoordsBuffer() {
    	return textureCoordinates;
    }
    
    private FastFloatBuffer vertexCoordinates;
    public FastFloatBuffer getVertexBuffer() {
    	return vertexCoordinates;
    }
    
    private FastFloatBuffer colors;
    private ShortBuffer indices;

    private boolean withColorArray_;

    public int getTotalQuads() {
        return totalQuads_;
    }

    public int capacity() {
        return capacity_;
    }

    public CCTexture2D getTexture() {
        return texture_;
    }

    public void setTexture(CCTexture2D tex) {
        texture_ = tex;
    }

    public boolean withColorArray() {
        return withColorArray_;
    }
    /** creates a TextureAtlas with an filename and with an initial capacity for Quads.
     * The TextureAtlas capacity can be increased in runtime.
     */
    public static CCTextureAtlas textureAtlas(String file, int capacity) {
    	return new CCTextureAtlas(file, capacity);
    }

    /** initializes a TextureAtlas with a filename and with a certain capacity for Quads.
     * The TextureAtlas capacity can be increased in runtime.
     *
     * WARNING: Do not reinitialize the TextureAtlas because it will leak memory (issue #706)
     */
    public CCTextureAtlas(String file, int n) {
        this(CCTextureCache.sharedTextureCache().addImage(file), n);
    }

    /** creates a TextureAtlas with a previously initialized Texture2D object, and
     * with an initial capacity for n Quads. 
     * The TextureAtlas capacity can be increased in runtime.
     */
    public static CCTextureAtlas textureAtlas(CCTexture2D tex, int capacity) {
    	return new CCTextureAtlas(tex, capacity);
    }

    /** initializes a TextureAtlas with a previously initialized Texture2D object, and
     * with an initial capacity for Quads. 
     * The TextureAtlas capacity can be increased in runtime.
     *
     * WARNING: Do not reinitialize the TextureAtlas because it will leak memory (issue #706)
     */
    public CCTextureAtlas(CCTexture2D tex, int n) {
        capacity_ = n;
        texture_ = tex;
        totalQuads_ = 0;

        withColorArray_ = false;

        ByteBuffer tbb = ByteBuffer.allocateDirect(ccQuad2.size * capacity_ * 4);
        tbb.order(ByteOrder.nativeOrder());
        textureCoordinates = FastFloatBuffer.createBuffer(tbb);

        ByteBuffer vbb = ByteBuffer.allocateDirect(ccQuad3.size * capacity_ * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexCoordinates = FastFloatBuffer.createBuffer(vbb);

        ByteBuffer isb = ByteBuffer.allocateDirect(6 * capacity_ * 2);
        isb.order(ByteOrder.nativeOrder());
        indices = isb.asShortBuffer();

        initIndices();
    }

    public String toString() {
        return new CCFormatter().format("<%s = %08X | getTotalQuads = %i>", CCTextureAtlas.class, this, totalQuads_);
    }

    public void initColorArray() {
        if (!withColorArray_) {
            // default color: 255,255,255,255
        	// modify by zt: A Texture in TextureAtlas need four colors for four vertices
            ByteBuffer cbb = ByteBuffer.allocateDirect(4 * capacity_ *ccColor4B.size * 4);
            cbb.order(ByteOrder.nativeOrder());
            colors = FastFloatBuffer.createBuffer(cbb);
            for (int i = 0; i < 4 * ccColor4B.size * capacity_ * 1; i++) {
                colors.put(i, 1.0f);
            }
            colors.position(0);

            withColorArray_ = true;
        }
    }

    public void initIndices() {
        for (int i = 0; i < capacity_; i++) {
        	if (ccConfig.CC_TEXTURE_ATLAS_USE_TRIANGLE_STRIP) {
        		indices.put((short) (i * 6 + 0), (short) (i * 4 + 0));
        		indices.put((short) (i * 6 + 1), (short) (i * 4 + 0));
        		indices.put((short) (i * 6 + 2), (short) (i * 4 + 2));
        		indices.put((short) (i * 6 + 3), (short) (i * 4 + 1));
        		indices.put((short) (i * 6 + 4), (short) (i * 4 + 3));
        		indices.put((short) (i * 6 + 5), (short) (i * 4 + 3));
        	} else {
        		indices.put((short) (i * 6 + 0), (short) (i * 4 + 0));
        		indices.put((short) (i * 6 + 1), (short) (i * 4 + 1));
        		indices.put((short) (i * 6 + 2), (short) (i * 4 + 2));

        		// inverted index.
        		indices.put((short) (i * 6 + 5), (short) (i * 4 + 1));
        		indices.put((short) (i * 6 + 4), (short) (i * 4 + 2));
        		indices.put((short) (i * 6 + 3), (short) (i * 4 + 3));
        	}
        }
        indices.position(0);
    }

    /** updates a Quad (texture, vertex and color) at a certain index
     * index must be between 0 and the atlas capacity - 1
     @since v0.8
     */
    public void updateQuad(FastFloatBuffer texCordBuffer, FastFloatBuffer vertexBuffer, int index) {
        assert (index >= 0 && index < capacity_) : "update quad with texture_: Invalid index";

        totalQuads_ = Math.max(index + 1, totalQuads_);

        putTexCoords(texCordBuffer, index);
        putVertex(vertexBuffer, index);
    }
    
    public void updateQuad(FastFloatBuffer texCordBuffer, float[] vertexData, int index) {
        assert (index >= 0 && index < capacity_) : "update quad with texture_: Invalid index";

        totalQuads_ = Math.max(index + 1, totalQuads_);

        putTexCoords(texCordBuffer, index);
        putVertex(vertexCoordinates, vertexData, index);
    }

    public void updateQuad(ccQuad2 texQuad, ccQuad3 vertexQuad, int index) {
        assert (index >= 0 && index < capacity_) : "update quad with texture_: Invalid index";

        totalQuads_ = Math.max(index + 1, totalQuads_);

        putTexCoords(textureCoordinates, texQuad, index);
        putVertex(vertexCoordinates, vertexQuad, index);
    }

    public void updateColor(ccColor4B[] color, int index) {
        assert (index >= 0 && index < capacity_) : "update color with quad color: Invalid index";

        totalQuads_ = Math.max(index + 1, totalQuads_);

        if (!withColorArray_)
            initColorArray();

        if (withColorArray_)
            putColor(colors, color, index);
    }


    /** Inserts a Quad (texture, vertex and color) at a certain index
    index must be between 0 and the atlas capacity - 1
    @since v0.8
    */
    public void insertQuad(FastFloatBuffer texCordBuffer, FastFloatBuffer vertexBuffer, int index) {
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
        putTexCoords(texCordBuffer, index);
        putVertex(vertexBuffer, index);
    }
 
    /** Removes the quad that is located at a certain index and inserts it at a new index
     This operation is faster than removing and inserting in a quad in 2 different steps
     @since v0.7.2
    */
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
            ccColor4B [] colorsBackup = getColor(colors, from);
            arraycopyColor(colors, src, colors, dst, size);
            putColor(colors, colorsBackup, to);
        }
    }


    /** removes a quad at a given index number.
     The capacity remains the same, but the total number of quads to be drawn is reduced in 1
     @since v0.7.2
     */
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


    /** removes all Quads.
     The TextureAtlas capacity remains untouched. No memory is freed.
     The total number of quads to be drawn will be 0
     @since v0.7.2
     */
    public void removeAllQuads() {
        totalQuads_ = 0;
    }

    /** resize the capacity of the Texture Atlas.
     * The new capacity can be lower or higher than the current one
     * It returns YES if the resize was successful.
     * If it fails to resize the capacity it will return NO with a new capacity of 0.
     */
    public void resizeCapacity(int newCapacity) {
        if (newCapacity == capacity_)
            return;

        // update capacity and getTotalQuads
        totalQuads_ = Math.min(totalQuads_, newCapacity);

        capacity_ = newCapacity;

        ByteBuffer tbb = ByteBuffer.allocateDirect(ccQuad2.size * newCapacity * 4);
        tbb.order(ByteOrder.nativeOrder());
        FastFloatBuffer tmpTexCoords = FastFloatBuffer.createBuffer(tbb);
        tmpTexCoords.put(textureCoordinates);
        textureCoordinates = tmpTexCoords;
        textureCoordinates.position(0);

        ByteBuffer vbb = ByteBuffer.allocateDirect(ccQuad3.size * newCapacity * 4);
        vbb.order(ByteOrder.nativeOrder());
        FastFloatBuffer tmpVertexCoords = FastFloatBuffer.createBuffer(vbb);
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
            ByteBuffer cbb = ByteBuffer.allocateDirect(4*ccColor4B.size * newCapacity * 4);
            cbb.order(ByteOrder.nativeOrder());
            FastFloatBuffer tmpColors = FastFloatBuffer.createBuffer(cbb);
            tmpColors.put(colors);
            colors = tmpColors;
            colors.position(0);
        }
    }


    /** draws all the Atlas's Quads
     */
    public void drawQuads(GL10 gl) {
        draw(gl, totalQuads_);
    }

    /** draws n quads
     * n can't be greater than the capacity of the Atlas
     */
    public void draw(GL10 gl, int n) {
        texture_.loadTexture(gl);

//        // bug fix in case texture name = 0
//        texture_.checkName();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_.name());

        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexCoordinates.bytes);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinates.bytes);

        if (withColorArray_)
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors.bytes);

        if (ccConfig.CC_TEXTURE_ATLAS_USE_TRIANGLE_STRIP) {
        	gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, n * 6, GL10.GL_UNSIGNED_SHORT, indices);
        } else {
        	gl.glDrawElements(GL10.GL_TRIANGLES, n * 6, GL10.GL_UNSIGNED_SHORT, indices);
        }

    }
    
    private float[] getTexCoords(FastFloatBuffer src, int index) {
        float[] quadT = new float[ccQuad2.size];
        final int base = index * ccQuad2.size;
        for (int i = 0; i < ccQuad2.size; i++) {
            quadT[i] = src.get(base + i);
        }
        return quadT;
    }

    protected void putTexCoords(FastFloatBuffer dst, ccQuad2 quadT, int index) {
    	final int base = index * ccQuad2.size;
    	dst.position(base);
    	
    	dst.put(quadT.tl_x);
    	dst.put(quadT.tl_y);
    	dst.put(quadT.tr_x);
    	dst.put(quadT.tr_y);
    	dst.put(quadT.bl_x);
    	dst.put(quadT.bl_y);
    	dst.put(quadT.br_x);
    	dst.put(quadT.br_y);
    	
        dst.position(0);
    }
    
    protected void putTexCoords(FastFloatBuffer dst, float[] quadT, int index) {
    	final int base = index * ccQuad2.size;
    	dst.position(base);
    	dst.put(quadT);
        dst.position(0);
    }
    
    public void putTexCoords(FastFloatBuffer src, int index) {
    	final int base = index * ccQuad2.size;
    	textureCoordinates.position(base);
    	
    	// if textureCoordinates.put(src) then allocation is performed
    	// this solution not efficient may be, need to find best way for copy Buffers
    	int num = src.capacity();
    	for(int i = 0; i < num; i++)
    		textureCoordinates.put(src.get());
    	
    	src.position(0);
    	textureCoordinates.position(0);
    }

    protected void putVertex(FastFloatBuffer src, int index) {
    	final int base = index * ccQuad3.size;
    	vertexCoordinates.position(base);
    	vertexCoordinates.put(src);
    	
    	src.position(0);
        vertexCoordinates.position(0);
    }
    
    private float[] getVertex(FastFloatBuffer src, int index) {
        float[] quadV = new float[ccQuad3.size];
        final int base = index * ccQuad3.size;
        for (int i = 0; i < ccQuad3.size; i++) {
            quadV[i] = src.get(base + i);
        }
        return quadV;
    }
    
    public void putVertex(FastFloatBuffer dst, ccQuad3 quadV, int index) {
    	final int base = index * ccQuad3.size;
    	
    	dst.position(base);

    	dst.put(quadV.bl_x);
    	dst.put(quadV.bl_y);
    	dst.put(quadV.bl_z);
    	dst.put(quadV.br_x);
    	dst.put(quadV.br_y);
    	dst.put(quadV.br_z);
    	dst.put(quadV.tl_x);
    	dst.put(quadV.tl_y);
    	dst.put(quadV.tl_z);
    	dst.put(quadV.tr_x);
    	dst.put(quadV.tr_y);
    	dst.put(quadV.tr_z);
    	
        dst.position(0);
    }
    
    public void putVertex(FastFloatBuffer dst, float[] quadV, int index) {
    	final int base = index * ccQuad3.size;
    	
    	dst.position(base);
    	dst.put(quadV);
        dst.position(0);
    }

    private ccColor4B [] getColor(FastFloatBuffer src, int index) {
    	ccColor4B [] color = new ccColor4B[4];
        
        for(int j=0; j<4; ++j) {
            color[j].r = (int)(255 * src.get(index * ccColor4B.size *4 + 4*j + 0));
            color[j].g = (int)(255 * src.get(index * ccColor4B.size *4 + 4*j + 1));
            color[j].b = (int)(255 * src.get(index * ccColor4B.size *4 + 4*j + 2));
            color[j].a = (int)(255 * src.get(index * ccColor4B.size *4 + 4*j + 3));
        }

        return color;
    }

    private void putColor(FastFloatBuffer dst, ccColor4B color[], int index) {
    	for(int j=0; j<4; ++j) {
    		dst.put(index * ccColor4B.size * 4 + 4*j + 0, color[j].r/255.f);
    		dst.put(index * ccColor4B.size * 4 + 4*j + 1, color[j].g/255.f);
    		dst.put(index * ccColor4B.size * 4 + 4*j + 2, color[j].b/255.f);
    		dst.put(index * ccColor4B.size * 4 + 4*j + 3, color[j].a/255.f);
        }
    	dst.position(0);
    }

    private void arraycopyTexture(FastFloatBuffer src, int srcPos, FastFloatBuffer dst, int dstPos, int length) {
        if (src == dst) {
            memmoveFloat(src, srcPos * ccQuad2.size, dst, dstPos * ccQuad2.size, length * ccQuad2.size);
        } else {
            memcopyFloat(src, srcPos * ccQuad2.size, dst, dstPos * ccQuad2.size, length * ccQuad2.size);
        }
    }

    private void arraycopyVertex(FastFloatBuffer src, int srcPos, FastFloatBuffer dst, int dstPos, int length) {
        if (src == dst) {
            memmoveFloat(src, srcPos * ccQuad3.size, dst, dstPos * ccQuad3.size, length * ccQuad3.size);
        } else {
            memcopyFloat(src, srcPos * ccQuad3.size, dst, dstPos * ccQuad3.size, length * ccQuad3.size);
        }
    }

    private void arraycopyColor(FastFloatBuffer src, int srcPos, FastFloatBuffer dst, int dstPos, int length) {
        if (src == dst) {
            memmoveFloat(src, srcPos * ccColor4B.size * 4, dst, dstPos * ccColor4B.size*4, length * ccColor4B.size * 4);
        } else {
            memcopyFloat(src, srcPos * ccColor4B.size * 4, dst, dstPos * ccColor4B.size*4, length * ccColor4B.size * 4);
        }
    }

    private void memmoveFloat(FastFloatBuffer src, int from, FastFloatBuffer dst, int to, int size) {
        if (to < from) {
            memcopyFloat(src, from, dst, to, size);
        } else {
            for (int i = size - 1; i >= 0; i--) {
                dst.put(i + to, src.get(i + from));
            }
        }
    }

    private void memcopyFloat(FastFloatBuffer src, int from, FastFloatBuffer dst, int to, int size) {
        for (int i = 0; i < size; i++) {
            dst.put(i + to, src.get(i + from));
        }
    }

    public static void memmoveByte(ByteBuffer src, int from, ByteBuffer dst, int to, int size) {
        if (to < from) {
            memcopyByte(src, from, dst, to, size);
        } else {
            for (int i = size - 1; i >= 0; i--) {
                dst.put(i + to, src.get(i + from));
            }
        }
    }

    public static void memcopyByte(ByteBuffer src, int from, ByteBuffer dst, int to, int size) {
        for (int i = 0; i < size; i++) {
            dst.put(i + to, src.get(i + from));
        }
    }

}
