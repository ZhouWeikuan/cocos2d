package org.cocos2d.types;

//!	A 3D Quad. 12 floats
public class ccQuad3 {

    public static final int size = 4 * 3;

    public float bl_x, bl_y, bl_z;
    public float br_x, br_y, br_z;
    public float tl_x, tl_y, tl_z;
    public float tr_x, tr_y, tr_z;

    public ccQuad3() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public ccQuad3(float[] f) {
        this(f[0], f[1], f[2],
                f[3], f[4], f[5],
                f[6], f[7], f[8],
                f[9], f[10], f[11]
        );
    }

    public ccQuad3(float a, float b, float c, float d, float e, float f, float g, float h, float i, float j, float k, float l) {
        bl_x = a;
        bl_y = b;
        bl_z = c;
        br_x = d;
        br_y = e;
        br_z = f;
        tl_x = g;
        tl_y = h;
        tl_z = i;
        tr_x = j;
        tr_y = k;
        tr_z = l;
    }

    public float[] toFloatArray() {
        return new float[]{bl_x, bl_y, bl_z,
                br_x, br_y, br_z,
                tl_x, tl_y, tl_z,
                tr_x, tr_y, tr_z};
    }

    public String toString() {
        return "CCQuad3: ( " + tl_x + ", " + tl_y + ", " + tl_z + " " +
                tr_x + ", " + tr_y + ", " + tl_z + " " +
                bl_x + ", " + bl_y + ", " + tl_z + " " +
                br_x + ", " + br_y + ", " + tl_z + " )";
    }

}
