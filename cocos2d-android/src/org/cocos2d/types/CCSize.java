package org.cocos2d.types;


public class CCSize {
    public float width, height;

    private CCSize() {
        this(0, 0);
    }

    private CCSize(float w, float h) {
        width = w;
        height = h;
    }

    public static CCSize make(float w, float h) {
        return new CCSize(w, h);
    }

    public static CCSize zero() {
        return new CCSize(0, 0);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public static boolean equalToSize(CCSize s1, CCSize s2) {
        return s1.width == s2.width && s1.height == s2.height;
    }

    public String toString() {
        return "<" + width + ", " + height + ">";
    }
}
