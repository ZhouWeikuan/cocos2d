package org.cocos2d.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class BufferProvider {
	// 64k is big enough for most objects
	private static final int ALLOCATION_SIZE = 1024 * 1024;
	private ByteBuffer currentBuffer = null;
	private static BufferProvider global_synced = new BufferProvider();
	
	public ByteBuffer allocate(int size) {
		if(size >= ALLOCATION_SIZE)
			return ByteBuffer.allocateDirect(size);

		if(currentBuffer == null || size > currentBuffer.remaining())
			currentBuffer = ByteBuffer.allocateDirect(ALLOCATION_SIZE);

		currentBuffer.limit(currentBuffer.position() + size);
		ByteBuffer result = currentBuffer.slice();

		currentBuffer.position(currentBuffer.limit());
		currentBuffer.limit(currentBuffer.capacity());
		return result;
	}

	public static ByteBuffer allocateDirect(int size) {
		synchronized(global_synced) {
			return global_synced.allocate(size);
		}
	}
	
    public static void drawQuads(GL10 gl, FastFloatBuffer fbVert, FastFloatBuffer fbCoord) {
        fbVert.position(0);
        fbCoord.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fbVert.bytes);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, fbCoord.bytes);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }

    public static void fillFloatBuffer(FastFloatBuffer fb, float[] arr) {
        fb.position(0);
        fb.put(arr);
    }

    public static FastFloatBuffer makeFloatBuffer(float[] arr) {
        ByteBuffer bb = BufferProvider.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FastFloatBuffer fb = FastFloatBuffer.createBuffer(bb);
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    public static FastFloatBuffer createFloatBuffer(int arrayElementCount) {
        ByteBuffer temp = BufferProvider.allocateDirect(4 * arrayElementCount);
        temp.order(ByteOrder.nativeOrder());
        
        return FastFloatBuffer.createBuffer(temp);
    }
    
    public static ByteBuffer createByteBuffer(int arrayElementCount) {
        ByteBuffer temp = BufferProvider.allocateDirect(arrayElementCount);
        temp.order(ByteOrder.nativeOrder());
        
        return temp;
    }
    
    public static ShortBuffer createShortBuffer(int arrayElementCount) {
        ByteBuffer temp = BufferProvider.allocateDirect(2 * arrayElementCount);
        temp.order(ByteOrder.nativeOrder());
        
        return temp.asShortBuffer();
    }
    
    public static ByteBuffer bufferFromFile(String path) {
    	ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
    	byte buf[] = new byte[1024];
    	int len = 0;
    	try {
			FileInputStream fis = new FileInputStream(path);
			while(true) {
				len = fis.read(buf);
				if (len == -1)
					break;
				tmpOut.write(buf, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray());
    	return bb;
    }
}
