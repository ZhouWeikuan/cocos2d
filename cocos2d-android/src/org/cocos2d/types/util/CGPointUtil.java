package org.cocos2d.types.util;

import org.cocos2d.types.CGAffineTransform;
import org.cocos2d.types.CGPoint;

public final class CGPointUtil {
	
	/**
	 * Multiply point by floating, write result in the same point.
	 * @param v - src/dst point
	 * @param s - factor value
	 */
	public static void mult(CGPoint v, final float s) {
		v.x *= s;
		v.y *= s;
	}
	
	/**
	 * Multiply point by floating, write result in another point.
	 * @param v - src point
	 * @param s - factor value
	 * @param res - dst point
	 */
	public static void mult(CGPoint v, final float s, CGPoint res) {
		res.x = v.x * s;
		res.y = v.y * s;		
	}
	
	public static void applyAffineTransform(CGPoint p, CGAffineTransform t, CGPoint res) {
		applyAffineTransform(p.x, p.y, t, res);
	}
	
	public static void applyAffineTransform(float x, float y, CGAffineTransform t, CGPoint res) {
		res.x = (float) (x * t.m00 + y * t.m01 + t.m02);
		res.y = (float) (x * t.m10 + y * t.m11 + t.m12);
	}
	
	public static void zero(CGPoint p) {
		p.x = 0;
		p.y = 0;
	}
}
