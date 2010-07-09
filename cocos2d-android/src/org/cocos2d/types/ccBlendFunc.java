package org.cocos2d.types;

//! Blend Function used for textures
public class ccBlendFunc {
	//! source blend function
	public int src;
	//! destination blend function
	public int dst;

    public ccBlendFunc(int s, int d) {
        src = s;
        dst = d;
    }

    public ccBlendFunc() {
        src = dst = 0;
    }

    public void setSrc(int s) {
        src = s;
    }

    public int getSrc() {
        return src;
    }

    public void setDst(int d) {
        dst = d;
    }

    public int getDst() {
        return dst;
    }

    public String toString() {
        return "< src=" + src + ", dst=" + dst + " >";
    }
}

