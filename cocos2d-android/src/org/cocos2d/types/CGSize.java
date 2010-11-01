package org.cocos2d.types;


public class CGSize {
    public float width, height;

    private CGSize() {
        this(0, 0);
    }

    private CGSize(float w, float h) {
        width = w;
        height = h;
    }

    public static CGSize make(float w, float h) {
        return new CGSize(w, h);
    }

    public static CGSize zero() {
        return new CGSize(0, 0);
    }
    
	public void set(CGSize s) {  	
		width = s.width; 
		height = s.height;
	}
	
	public void set(float w, float h) {  	
		width = w; 
		height = h;
	}
	
	private static CGSize ZERO_SIZE = CGSize.zero();
	public static CGSize getZero() {
		return ZERO_SIZE;
	}
	
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public static boolean equalToSize(CGSize s1, CGSize s2) {
        return s1.width == s2.width && s1.height == s2.height;
    }

    public String toString() {
        return "<" + width + ", " + height + ">";
    }
}
