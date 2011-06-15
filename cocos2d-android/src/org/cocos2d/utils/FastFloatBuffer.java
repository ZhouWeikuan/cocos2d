package org.cocos2d.utils;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Convenient work-around for poor {@link FloatBuffer#put(float[])} performance.
 * This should become unnecessary in gingerbread, 
 * @see <a href="http://code.google.com/p/android/issues/detail?id=11078">Issue 11078</a>
 * 
 * @author ryanm
 */
public class FastFloatBuffer {
    /**
     * Underlying data - give this to OpenGL
     */
    public ByteBuffer bytes;

    private FloatBuffer floats;

    private IntBuffer ints;

    private int bufferID = -1;
    private boolean loaded = false;

    /**
     * Use a {@link SoftReference} so that the array can be collected
     * if necessary
     */
    private static SoftReference<int[]> intArray = new SoftReference<int[]>(
            new int[0]);

    public static FastFloatBuffer createBuffer(float[] data) {
        FastFloatBuffer buffer = new FastFloatBuffer(data.length);
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }
    
    public static FastFloatBuffer createBuffer(ByteBuffer buffer) {
    	return new FastFloatBuffer(buffer);
    }
    
    /**
     * Constructs a new direct native-ordered buffer
     * 
     * @param capacity
     *           the number of floats
     */
    public FastFloatBuffer(int capacity) {
        bytes = ByteBuffer.allocateDirect((capacity * 4)).order(
                ByteOrder.nativeOrder());
        floats = bytes.asFloatBuffer();
        ints = bytes.asIntBuffer();
    }
    
    /**
     * Constructs a new direct native-ordered buffer from
     *  an existing ByteBuffer
     * 
     * @param buffer
     * 			the existing ByteBuffer
     */
    public FastFloatBuffer(ByteBuffer buffer) {
    	buffer.position(0);
    	bytes = buffer;
    	floats = bytes.asFloatBuffer();
    	ints = bytes.asIntBuffer();
    }
    
    
    /**
     * See {@link FloatBuffer#flip()}
     */
    public void flip() {
        bytes.flip();
        floats.flip();
        ints.flip();
    }

    /**
     * See {@link FloatBuffer#put(float)}
     * 
     * @param f
     */
    public FastFloatBuffer put(float f) {
        bytes.position(bytes.position() + 4);
        floats.put(f);
        ints.position(ints.position() + 1);
        
        return this;
    }

    /**
     * It's like {@link FloatBuffer#put(float[])}, but about 10 times
     * faster
     * 
     * @param data
     */
    public FastFloatBuffer put(float[] data) {
        int[] ia = intArray.get();
        if (ia == null || ia.length < data.length) {
            ia = new int[data.length];
            intArray = new SoftReference<int[]>(ia);
        }

        for (int i = 0; i < data.length; i++) {
            ia[i] = Float.floatToRawIntBits(data[i]);
        }

        bytes.position(bytes.position() + 4 * data.length);
        floats.position(floats.position() + data.length);
        ints.put(ia, 0, data.length);
        
        return this;
    }

    /**
     * For use with pre-converted data. This is 50x faster than
     * {@link #put(float[])}, and 500x faster than
     * {@link FloatBuffer#put(float[])}, so if you've got float[] data
     * that won't change, {@link #convert(float...)} it to an int[]
     * once and use this method to put it in the buffer
     * 
     * @param data
     *           floats that have been converted with
     *           {@link Float#floatToIntBits(float)}
     */
    public FastFloatBuffer put(int[] data) {
        bytes.position(bytes.position() + 4 * data.length);
        floats.position(floats.position() + data.length);
        ints.put(data, 0, data.length);
        
        return this;
    }

    /**
     * Converts float data to a format that can be quickly added to the
     * buffer with {@link #put(int[])}
     * 
     * @param data
     * @return the int-formatted data
     */
    public static int[] convert(float... data) {
        int[] id = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            id[i] = Float.floatToRawIntBits(data[i]);
        }

        return id;
    }

    /**
     * See {@link FloatBuffer#put(FloatBuffer)}
     * 
     * @param b
     */
    public FastFloatBuffer put(FastFloatBuffer b) {
        bytes.put(b.bytes);
        floats.position(bytes.position() >> 2);
        ints.position(bytes.position() >> 2);
        
        return this;
    }

    /**
     * @return See {@link FloatBuffer#capacity()}
     */
    public int capacity() {
        return floats.capacity();
    }

    /**
     * @return See {@link FloatBuffer#position()}
     */
    public int position() {
        return floats.position();
    }

    /**
     * See {@link FloatBuffer#position(int)}
     * 
     * @param p
     */
    public void position(int p) {
        bytes.position(4 * p);
        floats.position(p);
        ints.position(p);
    }

    /**
     * @return See {@link FloatBuffer#slice()}
     */
    public FloatBuffer slice() {
        return floats.slice();
    }

    /**
     * @return See {@link FloatBuffer#remaining()}
     */
    public int remaining() {
        return floats.remaining();
    }

    /**
     * @return See {@link FloatBuffer#limit()}
     */
    public int limit() {
        return floats.limit();
    }
    
    /**
     * @return See {@link FloatBuffer#limit(int)}
     */
    public void limit(int limit) {
    	bytes.limit(4 * limit);
        floats.limit(limit);
        ints.limit(limit);
    }

    /**
     * See {@link FloatBuffer#clear()}
     */
    public void clear() {
        bytes.clear();
        floats.clear();
        ints.clear();
    }

    public void setBufferID(int id) {
        this .bufferID = id;
    }

    public int getBufferID() {
        return this .bufferID;
    }

    public boolean isLoaded() {
        return this .loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

	public void put(int i, float f) {
		final int j = this.position();
		this.position(i);
		this.put(f);
		this.position(j);
	}

	public float get() {
		return floats.get();
	}

	public float get(int i) {
		return floats.get(i);
	}

	public void get(float[] ret, int i, int limit) {
		floats.get(ret, i, limit);
	}

	public void put(float[] floats, int off, int len) {
		for (int i = off; i < off + len; i++)
	         this.put(floats[i]); 
	}

	public void rewind() {
		this.position(0);
	}
}