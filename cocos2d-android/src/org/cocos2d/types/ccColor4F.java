package org.cocos2d.types;

/** RGBA color composed of 4 floats
@since v0.8
*/
public class ccColor4F {
    public float r;
    public float g;
    public float b;
    public float a;

    public ccColor4F() {
        r = g = b = a = 1.0f;
    }

    public ccColor4F(float rr, float gg, float bb, float aa) {
        r = rr;
        g = gg;
        b = bb;
        a = aa;
    }

    public ccColor4F(ccColor4F c) {
        r = c.r;
        g = c.g;
        b = c.b;
        a = c.a;
    }

    public ccColor4F(ccColor4B c)
    {
    	r = c.r/0xFF;
    	g = c.g/0xFF;
    	b = c.b/0xFF;
    	a = c.a/0xFF;
    }

    public ccColor4F(ccColor3B c)
    {
    	r = c.r/0xFF;
    	g = c.g/0xFF;
    	b = c.b/0xFF;
    	a = 1.0f;
    }
    
    public float[] toFloatArray() {
        return new float[]{r, g, b, a};
    }

    /** Returns a ccColor4F from a ccColor3B. Alpha will be 1.
     @since v0.99.1
     */
    public static ccColor4F ccc4FFromccc3B(ccColor3B c)
    {
        return new ccColor4F(c.r/255.f, c.g/255.f, c.b/255.f, 1.f);
    }

    /** Returns a ccColor4F from a ccColor4B.
      @since v0.99.1
      */
    public static ccColor4F ccc4FFromccc4B(ccColor4B c)
    {
        return new ccColor4F(c.r/255.f, c.g/255.f, c.b/255.f, c.a/255.f);
    }

    /** returns YES if both ccColor4F are equal. Otherwise it returns NO.
      @since v0.99.1
      */
    public static boolean ccc4FEqual(ccColor4F a, ccColor4F b)
    {
        return a.r == b.r && a.g == b.g && a.b == b.b && a.a == b.a;
    }

    public String toString() {
        return "< r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + " >";
    }
}

