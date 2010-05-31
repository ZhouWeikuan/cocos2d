package org.cocos2d.types;

//! Blend Function used for textures
public class CCBlendFunc {
    //! source blend function
    public int src;
    //! destination blend function
    public int dst;

    public CCBlendFunc(int s, int d) {
        src = s;
        dst = d;
    }

    public int getSrc() {
        return src;
    }

    public int getDst() {
        return dst;
    }

}
