package org.cocos2d.grid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad2;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.utils.FastFloatBuffer;


/**
 CCTiledGrid3D is a 3D grid implementation. It differs from Grid3D in that
 the tiles can be separated from the grid.
*/

public class CCTiledGrid3D extends CCGridBase {
    FastFloatBuffer texCoordinates;
    FastFloatBuffer vertices;
    FastFloatBuffer originalVertices;
    ShortBuffer indices;

    public static CCTiledGrid3D make(ccGridSize gSize) {
    	return new CCTiledGrid3D(gSize);
    }
    
    public CCTiledGrid3D(ccGridSize gSize) {
        super(gSize);
        calculateVertexPoints();
    }

    public void blit(GL10 gl) {
        int n = gridSize_.x * gridSize_.y;

    	// Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
    	// Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
    	// Unneeded states: GL_COLOR_ARRAY
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices.bytes);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates.bytes);
        gl.glDrawElements(GL10.GL_TRIANGLES, n * 6, GL10.GL_UNSIGNED_SHORT, indices);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }

/*
    -(void)calculateVertexPoints
    {
    	float width = (float)texture_.pixelsWide;
    	float height = (float)texture_.pixelsHigh;
    	float imageH = texture_.contentSize.height;
    	
    	int numQuads = gridSize_.x * gridSize_.y;
    	
    	vertices = malloc(numQuads*12*sizeof(GLfloat));
    	originalVertices = malloc(numQuads*12*sizeof(GLfloat));
    	texCoordinates = malloc(numQuads*8*sizeof(GLfloat));
    	indices = malloc(numQuads*6*sizeof(GLushort));
    	
    	float *vertArray = (float*)vertices;
    	float *texArray = (float*)texCoordinates;
    	GLushort *idxArray = (GLushort *)indices;
    	
    	int x, y;
    	
    	for( x = 0; x < gridSize_.x; x++ )
    	{
    		for( y = 0; y < gridSize_.y; y++ )
    		{
    			float x1 = x * step_.x;
    			float x2 = x1 + step_.x;
    			float y1 = y * step_.y;
    			float y2 = y1 + step_.y;
    			
    			*vertArray++ = x1;
    			*vertArray++ = y1;
    			*vertArray++ = 0;
    			*vertArray++ = x2;
    			*vertArray++ = y1;
    			*vertArray++ = 0;
    			*vertArray++ = x1;
    			*vertArray++ = y2;
    			*vertArray++ = 0;
    			*vertArray++ = x2;
    			*vertArray++ = y2;
    			*vertArray++ = 0;
    			
    			float newY1 = y1;
    			float newY2 = y2;
    			
    			if( isTextureFlipped_ ) {
    				newY1 = imageH - y1;
    				newY2 = imageH - y2;
    			}

    			*texArray++ = x1 / width;
    			*texArray++ = newY1 / height;
    			*texArray++ = x2 / width;
    			*texArray++ = newY1 / height;
    			*texArray++ = x1 / width;
    			*texArray++ = newY2 / height;
    			*texArray++ = x2 / width;
    			*texArray++ = newY2 / height;
    		}
    	}
    	
    	for( x = 0; x < numQuads; x++)
    	{
    		idxArray[x*6+0] = x*4+0;
    		idxArray[x*6+1] = x*4+1;
    		idxArray[x*6+2] = x*4+2;
    		
    		idxArray[x*6+3] = x*4+1;
    		idxArray[x*6+4] = x*4+2;
    		idxArray[x*6+5] = x*4+3;
    	}
    	
    	memcpy(originalVertices, vertices, numQuads*12*sizeof(GLfloat));
    }
*/
    public void calculateVertexPoints() {
        float width = (float) texture_.pixelsWide();
        float height = (float) texture_.pixelsHigh();

        int numQuads = gridSize_.x * gridSize_.y;

        ByteBuffer vfb = ByteBuffer.allocateDirect(ccQuad3.size * numQuads * 4);
        vfb.order(ByteOrder.nativeOrder());
        vertices = FastFloatBuffer.createBuffer(vfb);

        ByteBuffer ofb = ByteBuffer.allocateDirect(ccQuad3.size * numQuads * 4);
        ofb.order(ByteOrder.nativeOrder());
        originalVertices = FastFloatBuffer.createBuffer(ofb);

        ByteBuffer tfb = ByteBuffer.allocateDirect(ccQuad2.size * numQuads * 4);
        tfb.order(ByteOrder.nativeOrder());
        texCoordinates = FastFloatBuffer.createBuffer(tfb);

        ByteBuffer isb = ByteBuffer.allocateDirect(6 * numQuads * 2);
        isb.order(ByteOrder.nativeOrder());
        indices = isb.asShortBuffer();

        for (int x = 0; x < gridSize_.x; x++) {
            for (int y = 0; y < gridSize_.y; y++) {
                float x1 = x * step_.x;
                float x2 = x1 + step_.x;
                float y1 = y * step_.y;
                float y2 = y1 + step_.y;

                vertices.put(x1);
                vertices.put(y1);
                vertices.put(0);
                vertices.put(x2);
                vertices.put(y1);
                vertices.put(0);
                vertices.put(x1);
                vertices.put(y2);
                vertices.put(0);
                vertices.put(x2);
                vertices.put(y2);
                vertices.put(0);

                texCoordinates.put(x1 / width);
                texCoordinates.put(y1 / height);
                texCoordinates.put(x2 / width);
                texCoordinates.put(y1 / height);
                texCoordinates.put(x1 / width);
                texCoordinates.put(y2 / height);
                texCoordinates.put(x2 / width);
                texCoordinates.put(y2 / height);
            }
        }
        vertices.position(0);
        texCoordinates.position(0);

        for (int x = 0; x < numQuads; x++) {
            indices.put(x * 6 + 0, (short) (x * 4 + 0));
            indices.put(x * 6 + 1, (short) (x * 4 + 1));
            indices.put(x * 6 + 2, (short) (x * 4 + 2));

            indices.put(x * 6 + 3, (short) (x * 4 + 1));
            indices.put(x * 6 + 4, (short) (x * 4 + 2));
            indices.put(x * 6 + 5, (short) (x * 4 + 3));
        }

        originalVertices.put(vertices);
        originalVertices.position(0);
    }

    /** sets a new tile */
    public void setTile(ccGridSize pos, ccQuad3 coords) {
        int idx = (gridSize_.y * pos.x + pos.y) * 4 * 3;
        float[] vertArray = coords.toFloatArray();
        for (int i = 0; i < ccQuad3.size; i++) {
            vertices.put(idx + i, vertArray[i]);
        }
        vertices.position(0);
    }

    /** returns the original tile (untransformed) at the given position */
    public ccQuad3 originalTile(ccGridSize pos) {
        int idx = (gridSize_.y * pos.x + pos.y) * ccQuad3.size;

        float[] vertArray = new float[ccQuad3.size];
        for (int i = 0; i < ccQuad3.size; i++) {
            vertArray[i] = originalVertices.get(idx + i);
        }

        return new ccQuad3(vertArray);
    }
    
    /** returns the tile at the given position */
    public ccQuad3 tile(ccGridSize pos) {
        int idx = (gridSize_.y * pos.x + pos.y) * 4 * 3;

        float[] vertArray = new float[ccQuad3.size];
        for (int i = 0; i < ccQuad3.size; i++) {
            vertArray[i] = vertices.get(idx + i);
        }

        return new ccQuad3(vertArray);
    }

    @Override
    public void reuse(GL10 gl) {
        if (reuseGrid_ > 0) {
            final int numQuads = gridSize_.x * gridSize_.y;
            final int total = numQuads * 12;
            for (int i=0; i<total; ++i) {
            	originalVertices.put(i, vertices.get(i));
            }
//            memcpy(originalVertices, vertices, numQuads*12*sizeof(float));
            reuseGrid_--;
        }
    }
}
