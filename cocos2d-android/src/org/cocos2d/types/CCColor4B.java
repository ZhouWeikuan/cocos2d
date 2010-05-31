package org.cocos2d.types;

public class CCColor4B {
    public static final int size = 4;

    public int r;
    public int g;
    public int b;
    public int a;

    public CCColor4B(int rr, int gg, int bb, int aa) {
        r = rr;
        g = gg;
        b = bb;
        a = aa;
    }

    public byte[] ccColor4B() {
        return new byte[]{(byte) r, (byte) g, (byte) b, (byte) a};
    }

    public String toString() {
        return "< r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + " >";
    }
}
