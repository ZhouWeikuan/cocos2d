package org.cocos2d.types;

public class CCColor3B {
    public static final int size = 3;

    public int r;
    public int g;
    public int b;

    public CCColor3B(int rr, int gg, int bb) {
        r = rr;
        g = gg;
        b = bb;
    }

    public byte[] ccColor3B() {
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }

    public String toString() {
        return "< r=" + r + ", g=" + g + ", b=" + b + " >";
    }
}
