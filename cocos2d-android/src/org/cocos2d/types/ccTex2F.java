package org.cocos2d.types;

/** A texcoord composed of 2 floats: u, y
 @since v0.8
 */
public class ccTex2F {
    public float u;
    public float v;

    public ccTex2F(float uf, float vf){
        u = uf;
        v = vf;
    }

    public float[] toFloatArray() {
        return new float[] {u, v};
    }
}

