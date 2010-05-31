package org.cocos2d.types;

public class CCRGBB {
    public byte r;
    public byte g;
    public byte b;

    public CCRGBB(byte rr, byte gg, byte bb) {
        r = rr;
        g = gg;
        b = bb;
    }

    public byte[] ccRGBB() {
        return new byte[]{r, g, b};
    }
}
