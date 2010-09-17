package org.cocos2d.types;

/** RGBA color composed of 4 bytes
@since v0.8
*/
public class ccColor4B {
    public static final int size = 4;

    public int r;
    public int g;
    public int b;
    public int a;

    public ccColor4B(int rr, int gg, int bb, int aa) {
        r = rr;
        g = gg;
        b = bb;
        a = aa;
    }

    public byte[] toByteArray() {
        return new byte[]{(byte) r, (byte) g, (byte) b, (byte) a};
    }
    
    public float[] toFloatArray() {
    	return new float[] {r/255f, g/255f, b/255f, a/255f};
    }

    //! helper macro that creates an ccColor4B type
    public static ccColor4B ccc4(final int r, final int g, final int b, final int a) {
        return new ccColor4B(r, g, b, a);
    }

    public String toString() {
        return "< r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + " >";
    }
}

