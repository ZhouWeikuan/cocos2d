package org.cocos2d.types;

public class CCColorF {
    public float r;
    public float g;
    public float b;
    public float a;

    public CCColorF() {
        r = g = b = a = 1.0f;
    }

    public CCColorF(float rr, float gg, float bb, float aa) {
        r = rr;
        g = gg;
        b = bb;
        a = aa;
    }

    public CCColorF(CCColorF c) {
        r = c.r;
        g = c.g;
        b = c.b;
        a = c.a;
    }

    public CCColorF(CCColor4B c)
    {
    	r = c.r/0xFF;
    	g = c.g/0xFF;
    	b = c.b/0xFF;
    	a = c.a/0xFF;
    }

    public CCColorF(CCColor3B c)
    {
    	r = c.r/0xFF;
    	g = c.g/0xFF;
    	b = c.b/0xFF;
    	a = 1.0f;
    }
    
    public float[] ccColorF() {
        return new float[]{r, g, b, a};
    }

    public String toString() {
        return "< r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + " >";
    }

    
}
