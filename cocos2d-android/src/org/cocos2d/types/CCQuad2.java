package org.cocos2d.types;

//!	A 2D Quad. 8 floats
public class CCQuad2 {

    public static final int size = 4 * 2;

    public float tl_x, tl_y;
    public float tr_x, tr_y;
    public float bl_x, bl_y;
    public float br_x, br_y;

    public CCQuad2(float a, float b, float c, float d, float e, float f, float g, float h) {
        tl_x = a;
        tl_y = b;
        tr_x = c;
        tr_y = d;
        bl_x = e;
        bl_y = f;
        br_x = g;
        br_y = h;
    }

    public CCQuad2() {
        this(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
    }

    public CCQuad2(float[] f) {
        this(f[0], f[1],
                f[2], f[3],
                f[4], f[5],
                f[6], f[7]
        );
    }

    public float[] ccQuad2() {
        return new float[]{tl_x, tl_y,
                tr_x, tr_y,
                bl_x, bl_y,
                br_x, br_y};
    }

    public String toString() {
        return "CCQuad2: ( " + tl_x + ", " + tl_y + " " +
                tr_x + ", " + tr_y + " " +
                bl_x + ", " + bl_y + " " +
                br_x + ", " + br_y + " )";
    }
}
