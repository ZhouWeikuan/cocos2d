package org.cocos2d.types;

import javax.microedition.khronos.opengles.GL10;

public class CCMacros {

    public static final int INT_MIN = -2147483648;

    public static void CC_SWAP(int x, int y ) {
    	int temp  = x;
		x = y; y = temp;
    }

    public static void CC_SWAP(float x, float y ) {
    	float temp  = x;
		x = y; y = temp;
    }    

    /// returns a random float between -1 and 1
    public static float CCRANDOM_MINUS1_1() {
        return (float) Math.random() * 2.0f - 1.0f;
    }

    /// returns a random float between 0 and 1
    public static float CCRANDOM_0_1() {
        return (float) Math.random();
    }

    // default gl blend src function
    public static final int CC_BLEND_SRC = GL10.GL_SRC_ALPHA;

    // default gl blend dst function
    public static final int CC_BLEND_DST = GL10.GL_ONE_MINUS_SRC_ALPHA;

    public static float CC_DEGREES_TO_RADIANS(float angle) {
        return (angle / 180.0f * (float) Math.PI);
    }

    public static float CC_RADIANS_TO_DEGREES(float angle) {
        return (angle / (float) Math.PI * 180.0f);
    }


}
