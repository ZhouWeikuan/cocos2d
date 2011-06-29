package org.cocos2d.grid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.utils.FastFloatBuffer;


/**
 CCGrid3D is a 3D grid implementation. Each vertex has 3 dimensions: x,y,z
 */
public class CCGrid3D extends CCGridBase {
	protected FastFloatBuffer texCoordinates;
	protected FastFloatBuffer vertices;
	protected FastFloatBuffer originalVertices;
    protected ShortBuffer indices;
    protected FastFloatBuffer mVertexBuffer;

    public CCGrid3D(ccGridSize gSize) {
        super(gSize);
        calculateVertexPoints();
    }

    @Override
    public void blit(GL10 gl) {
        int n = gridSize_.x * gridSize_.y;

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Unneeded states: GL_COLOR_ARRAY
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);	

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.limit()*3*4);
        //System.out.printf("vertices limit = %d\n", vertices.limit());
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = FastFloatBuffer.createBuffer(vbb);            
        mVertexBuffer.clear();          
        mVertexBuffer.position(0);
        for (int i = 0; i < vertices.limit(); i=i+3) {            
            mVertexBuffer.put(vertices.get(i));
            mVertexBuffer.put(vertices.get(i+1));
            mVertexBuffer.put(vertices.get(i+2));
        }
        mVertexBuffer.position(0);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer.bytes);
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates.bytes);
        indices.position(0);

        gl.glDrawElements(GL10.GL_TRIANGLES, n * 6, GL10.GL_UNSIGNED_SHORT, indices);

        // restore GL default state
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }
	
    @Override
    public void calculateVertexPoints() {
        float width = (float)texture_.pixelsWide();
        float height = (float)texture_.pixelsHigh();
        // float imageH = texture_.getContentSize().height;
	
        int x, y, i;

        ByteBuffer vfb = ByteBuffer.allocateDirect(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
        vfb.order(ByteOrder.nativeOrder());
        vertices = FastFloatBuffer.createBuffer(vfb);
        // vertices = BufferProvider.createFloatBuffer(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1));

        ByteBuffer ofb = ByteBuffer.allocateDirect(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
        ofb.order(ByteOrder.nativeOrder());
        originalVertices = FastFloatBuffer.createBuffer(ofb);
        // originalVertices = BufferProvider.createFloatBuffer(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1));
                
        ByteBuffer tfb = ByteBuffer.allocateDirect(2 * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
        tfb.order(ByteOrder.nativeOrder());
        texCoordinates = FastFloatBuffer.createBuffer(tfb);
        // texCoordinates = BufferProvider.createFloatBuffer(2 * (gridSize_.x + 1) * (gridSize_.y + 1));
        
        ByteBuffer isb = ByteBuffer.allocateDirect(6 * (gridSize_.x + 1) * (gridSize_.y + 1) * 2);
        isb.order(ByteOrder.nativeOrder());
        indices = isb.asShortBuffer();
        // indices = BufferProvider.createShortBuffer(6 * (gridSize_.x + 1) * (gridSize_.y + 1));
        
        for (y = 0; y < (gridSize_.y + 1); y++) {
            for (x = 0; x < (gridSize_.x + 1); x++) {
                int idx = (y * (gridSize_.x + 1)) + x;

                vertices.put(idx * 3 + 0, -1);
                vertices.put(idx * 3 + 1, -1);
                vertices.put(idx * 3 + 2, -1);
                vertices.put(idx * 2 + 0, -1);
                vertices.put(idx * 2 + 1, -1);
            }
        }
        vertices.position(0);

        for (x = 0; x < gridSize_.x; x++) {
            for (y = 0; y < gridSize_.y; y++) {
                int idx = (y * gridSize_.x) + x;

                float x1 = x * step_.x;
                float x2 = x1 + step_.x;
                float y1 = y * step_.y;
                float y2 = y1 + step_.y;

                short a = (short) (x * (getGridHeight() + 1) + y);
                short b = (short) ((x + 1) * (getGridHeight() + 1) + y);
                short c = (short) ((x + 1) * (getGridHeight() + 1) + (y + 1));
                short d = (short) (x * (getGridHeight() + 1) + (y + 1));

                short[] tempidx = {a, b, d, b, c, d};

               	indices.position(6 * idx);
               	indices.put(tempidx, 0, 6);

                int[] l1 = {a * 3, b * 3, c * 3, d * 3};
                CCVertex3D e = new CCVertex3D(x1, y1, 0);
                CCVertex3D f = new CCVertex3D(x2, y1, 0);
                CCVertex3D g = new CCVertex3D(x2, y2, 0);
                CCVertex3D h = new CCVertex3D(x1, y2, 0);

                CCVertex3D[] l2 = {e, f, g, h};

                int[] tex1 = {a * 2, b * 2, c * 2, d * 2};
                CGPoint[] tex2 = {CGPoint.ccp(x1, y1), CGPoint.ccp(x2, y1), CGPoint.ccp(x2, y2), CGPoint.ccp(x1, y2)};

                for (i = 0; i < 4; i++) {
                    vertices.put(l1[i] + 0, l2[i].x);
                    vertices.put(l1[i] + 1, l2[i].y);
                    vertices.put(l1[i] + 2, l2[i].z);

                    texCoordinates.put(tex1[i] + 0, tex2[i].x / width);
                    texCoordinates.put(tex1[i] + 1, tex2[i].y / height);
                }
            }
        }
        indices.position(0);
        vertices.position(0);
        texCoordinates.position(0);

        originalVertices.put(vertices);
        originalVertices.position(0);
    }

    /** returns the vertex at a given position */
    public CCVertex3D vertex(ccGridSize pos) {
        int index = (pos.x * (gridSize_.y + 1) + pos.y) * 3;
        CCVertex3D vert = new CCVertex3D(vertices.get(index + 0), vertices.get(index + 1), vertices.get(index + 2));

        return vert;
    }

    /** returns the original (non-transformed) vertex at a given position */
    public CCVertex3D originalVertex(ccGridSize pos) {
        int index = (pos.x * (gridSize_.y + 1) + pos.y) * 3;
        CCVertex3D vert = new CCVertex3D(originalVertices.get(index + 0), originalVertices.get(index + 1), originalVertices.get(index + 2));

        return vert;
    }

    /** sets a new vertex at a given position */
    public void setVertex(ccGridSize pos, CCVertex3D vertex) {
        int index = (pos.x * (gridSize_.y + 1) + pos.y) * 3;
        vertices.put(index + 0, vertex.x);
        vertices.put(index + 1, vertex.y);
        vertices.put(index + 2, vertex.z);
    }

    @Override
    public void reuse(GL10 gl) {
        if (reuseGrid_ > 0) {
//            memcpy(originalVertices, vertices, (getGridWidth()+1)*(getGridHeight()+1)*sizeof(CCVertex3D));
            reuseGrid_--;
        }

    }
}


