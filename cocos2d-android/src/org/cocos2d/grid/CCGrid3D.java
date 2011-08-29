package org.cocos2d.grid;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

import com.badlogic.gdx.utils.BufferUtils;


/**
 CCGrid3D is a 3D grid implementation. Each vertex has 3 dimensions: x,y,z
 */
public class CCGrid3D extends CCGridBase {
    private ShortBuffer indices;
    
	private FloatBuffer texCoordinates;
	private FloatBuffer vertices;
	private FloatBuffer originalVertices;
	private FloatBuffer mVertexBuffer;
	private FloatBuffer VBtemp1;
    private FloatBuffer VBtemp2;
    private boolean mvbswitch;
    
	private boolean membersInitialized_ = false;

    public CCGrid3D(ccGridSize gSize) {
        super(gSize);
        initMemberVariables();
        calculateVertexPoints();
    }

    private void initMemberVariables() {
    	x1 = x2 = y1 = y2 = a = b = c = d = 0;
    	
    	if (!membersInitialized_)
    		mvbswitch = true;
        
    	if (vertices == null)
    		vertices = FloatBuffer.allocate(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
    	
    	if (originalVertices == null)
    		originalVertices = FloatBuffer.allocate(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
    	
    	if (texCoordinates == null)
    		texCoordinates = BufferUtils.newFloatBuffer(2 * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
    	
    	if (indices == null)
    		indices = BufferUtils.newShortBuffer(6 * (gridSize_.x + 1) * (gridSize_.y + 1) * 2);
    	
    	if (tex1 == null)
    		tex1 = new int[4];
    	
    	if (l1 == null)
        	l1 = new int[4];
    	
    	if (s_indices == null)
    		s_indices = new short[6];
    	
    	if (textemp == null)
        	textemp = new float[3];
    	
    	if (tex2 == null)
        	tex2 = new CGPoint[] {CGPoint.ccp(0,0),CGPoint.ccp(0,0),CGPoint.ccp(0,0),CGPoint.ccp(0,0)};
    	
    	if (l2 == null)
        	l2 = new CCVertex3D[] {new CCVertex3D(0,0,0), new CCVertex3D(0,0,0), new CCVertex3D(0,0,0), new CCVertex3D(0,0,0)};
    	
    	membersInitialized_  = true;
	}

	float[] vertarray = new float[0];
    int x, y, i = 0;
    @Override
    public void blit(GL10 gl) {
        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Unneeded states: GL_COLOR_ARRAY
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);	
        
	    /** there's no need to create a new buffer on every blit,
	     *  simply switching back and forth to allow the GLThread
	     *  to finish reading one while we load the other is
	     *  sufficient.
	     */
        switch_mVertexBuffer();
        
        mVertexBuffer.clear();          
        mVertexBuffer.position(0);
        
        BufferUtils.copy(vertices.array(), mVertexBuffer, 0);
        mVertexBuffer.limit(mVertexBuffer.capacity());
        
//        for (i = 0; i < vertices.limit(); i+=3) {
//            mVertexBuffer.put(vertices.get(i)).put(vertices.get(i+1)).put(vertices.get(i+2));
//        }
//        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates);
        indices.position(0);

        gl.glDrawElements(GL10.GL_TRIANGLES, gridSize_.x * gridSize_.y * 6, GL10.GL_UNSIGNED_SHORT, indices);

        // restore GL default state
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }
	
    private void switch_mVertexBuffer() {
    	if (mvbswitch) {
        	if (VBtemp1 == null)
        		VBtemp1 = BufferUtils.newFloatBuffer(vertices.limit()*3*4);
        	
        	mVertexBuffer = VBtemp1;
        } else {
        	if (VBtemp2 == null) 
        		VBtemp2 = BufferUtils.newFloatBuffer(vertices.limit()*3*4);
      
        	mVertexBuffer = VBtemp2;
        }
        
    	mVertexBuffer.clear();
    	mVertexBuffer.position(0);
    	
        mvbswitch = !mvbswitch;
	}
    
	float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    short a = 0;
    short b = 0;
    short c = 0;
    short d = 0;
    int[] tex1 = new int[4];
    int[] l1 = new int[4];
    short[] s_indices = new short[6];
    float[] textemp = new float[3];
    float[] varrtemp;
    CGPoint[] tex2 = {CGPoint.ccp(0,0),CGPoint.ccp(0,0),CGPoint.ccp(0,0),CGPoint.ccp(0,0)};
    CCVertex3D[] l2 = new CCVertex3D[] {new CCVertex3D(0,0,0), new CCVertex3D(0,0,0), new CCVertex3D(0,0,0), new CCVertex3D(0,0,0)};
//    CCVertex3D e = ;
//    CCVertex3D f = new CCVertex3D(0,0,0);
//    CCVertex3D g = new CCVertex3D(0,0,0);
//    CCVertex3D h = new CCVertex3D(0,0,0);
    float width = 0;
    float height = 0;
    @Override
    public void calculateVertexPoints() {
    	width = (float)texture_.pixelsWide();
        height = (float)texture_.pixelsHigh();
        // float imageH = texture_.getContentSize().height;
    	if (!membersInitialized_)
    		initMemberVariables();

        /** I decided to use a regular float buffer for vertices and originalVertices since it is very rarely written to
         *  but often read from.  The trade off I observed from direct vs normal buffers it that:
         *  direct = very fast write, slow (and limited!) read
         *  normal = average write (for single float) / EXTREMELY slow write (bulk), 
         *  		 average read time (or instant with buffer.array(), which is impossible with direct buffer)
         */
//        vertices = FloatBuffer.allocate(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
//        originalVertices = FloatBuffer.allocate(ccQuad3.size * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
//        texCoordinates = BufferUtils.newFloatBuffer(2 * (gridSize_.x + 1) * (gridSize_.y + 1) * 4);
//        indices = BufferUtils.newShortBuffer(6 * (gridSize_.x + 1) * (gridSize_.y + 1) * 2);
        
    	
        int idx;
        varrtemp = vertices.array();
        for (y = 0; y < (gridSize_.y + 1); y++) {
            for (x = 0; x < (gridSize_.x + 1); x++) {
            	idx = (y * (gridSize_.x + 1)) + x;
//                vertices.put(idx * 3 + 0, -1);
//                vertices.put(idx * 3 + 1, -1);
//                vertices.put(idx * 3 + 2, -1);
//                vertices.put(idx * 2 + 0, -1);
//                vertices.put(idx * 2 + 1, -1);
            	Arrays.fill(varrtemp, idx*3, idx*3 + 3, -1);
            	Arrays.fill(varrtemp, idx*2, idx*2 + 2, -1);
                
            }
        }
        
        vertices.position(0);

        for (x = 0; x < gridSize_.x; x++) {
            for (y = 0; y < gridSize_.y; y++) {
            	idx = (y * gridSize_.x) + x;

                x1 = x * step_.x;
                x2 = x1 + step_.x;
                y1 = y * step_.y;
                y2 = y1 + step_.y;

                a = (short) (x * (getGridHeight() + 1) + y);
                b = (short) ((x + 1) * (getGridHeight() + 1) + y);
                c = (short) ((x + 1) * (getGridHeight() + 1) + (y + 1));
                d = (short) (x * (getGridHeight() + 1) + (y + 1));

               	indices.position(6 * idx);
               	//indices.put(new short[]  {a, b, d, b, c, d}, 0, 6);
//               	indices.put(a);
//               	indices.put(b);
//               	indices.put(d);
//               	
//               	indices.put(b);
//               	indices.put(c);
//               	indices.put(d);
               	s_indices[0] = a;
               	s_indices[1] = b;
               	s_indices[2] = d;
               	s_indices[3] = b;
               	s_indices[4] = c;
               	s_indices[5] = d;
               	
               	indices.put(s_indices);

                l1[0] = a * 3;
                l1[1] = b * 3;
                l1[2] = c * 3;
                l1[3] = d * 3;
                
                l2[0].x = x1;
                l2[0].y = y1;
                
                l2[1].x = x2;
                l2[1].y = y1;
                
                l2[2].x = x2;
                l2[2].y = y2;
                
                l2[3].x = x1;
                l2[3].y = y2;

//                l2[0] = e;
//                l2[1] = f;
//                l2[2] = g;
//                l2[3] = h;

                tex1[0] = a * 2;
                tex1[1] = b * 2;
                tex1[2] = c * 2;
                tex1[3] = d * 2;
                
                tex2[0].x = x1;
                tex2[0].y = y1;
                
                tex2[1].x = x2;
                tex2[1].y = y1;
                
                tex2[2].x = x2;
                tex2[2].y = y2;
                	
                tex2[3].x = x1;
                tex2[3].y = y2;

                for (i = 0; i < 4; i++) {
//                    vertices.put(l1[i] + 0, l2[i].x);
//                    vertices.put(l1[i] + 1, l2[i].y);
//                    vertices.put(l1[i] + 2, l2[i].z);
                	textemp[0] = l2[i].x;
                	textemp[1] = l2[i].y;
                	textemp[2] = l2[i].z;
//                	vertices.position(l1[i]);
//                	vertices.put(textemp, 0, 3);
                	System.arraycopy(textemp, 0, varrtemp, l1[i], 3);

                    textemp[0] = tex2[i].x / width;
                    textemp[1] = tex2[i].y / height;
                	BufferUtils.copy(textemp, texCoordinates, tex1[i]);
                }
            }
        }
        indices.position(0);
        vertices.position(0);
        texCoordinates.position(0);

        //originalVertices.put(vertices);
        System.arraycopy(varrtemp, 0, originalVertices.array(), 0, varrtemp.length);
        originalVertices.position(0);
    }

    /** returns the vertex at a given position */
    public CCVertex3D vertex(ccGridSize pos) {
        i = (pos.x * (gridSize_.y + 1) + pos.y) * 3;
        //CCVertex3D vert = new CCVertex3D(vertices.get(index + 0), vertices.get(index + 1), vertices.get(index + 2));
        
        temp.x = vertices.get(i);
        temp.y = vertices.get(i+1);
        temp.z = vertices.get(i+2);

        return temp;
    }

    CCVertex3D temp = new CCVertex3D(0,0,0);
    final float[] vtemp = new float[3];
    /** returns the original (non-transformed) vertex at a given position */
    public CCVertex3D originalVertex(ccGridSize pos) {
        i = (pos.x * (gridSize_.y + 1) + pos.y) * 3;

        temp.x = originalVertices.get(i);
        temp.y = originalVertices.get(i+1);
        temp.z = originalVertices.get(i+2);
        return temp;
    }

    /** sets a new vertex at a given position */
    public void setVertex(ccGridSize pos, CCVertex3D vertex) {
        i = (pos.x * (gridSize_.y + 1) + pos.y) * 3;
        vertices.put(i + 0, vertex.x);
        vertices.put(i + 1, vertex.y);
        vertices.put(i + 2, vertex.z);
    }

    @Override
    public void reuse(GL10 gl) {
        if (reuseGrid_ > 0) {
//            memcpy(originalVertices, vertices, (getGridWidth()+1)*(getGridHeight()+1)*sizeof(CCVertex3D));
            reuseGrid_--;
        }

    }
}


