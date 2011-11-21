package org.cocos2d.types.util;

import org.cocos2d.types.CGAffineTransform;
import org.cocos2d.types.CGPoint;

public final class CGPointUtil {
	
	/**
	 * Multiply point by floating, write result in the same point.
	 * @param v - src/dst point
	 * @param s - factor value
	 */
	public static void mult(CGPoint v, float s) {
		v.x *= s;
		v.y *= s;
	}
	
	/**
	 * Multiply point by floating, write result in another point.
	 * @param v - src point
	 * @param s - factor value
	 * @param res - dst point
	 */
	public static void mult(CGPoint v, float s, CGPoint res) {
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

	public static void normalize(CGPoint src, CGPoint dst) {
		float invLen = 1 / CGPoint.ccpLength(src);
		dst.set(src.x * invLen, src.y * invLen);
	}

	public static void add(CGPoint first, CGPoint second, CGPoint ret) {
		ret.x = first.x + second.x;
		ret.y = first.y + second.y;
	}

	public static void add(CGPoint v, CGPoint toAdd) {
		v.x += toAdd.x;
		v.y += toAdd.y;
	}

	public static void sub(CGPoint first, CGPoint second, CGPoint ret) {
		ret.x = first.x - second.x;
		ret.y = first.y - second.y;
	}
	
	public static void sub(CGPoint v, CGPoint toAdd) {
		v.x -= toAdd.x;
		v.y -= toAdd.y;
	}

	public static float distance(CGPoint p1, CGPoint p2) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
	
	public static void rotateByAngle(CGPoint v, CGPoint pivot, float angle, CGPoint ret) 
	{
		CGPointUtil.sub(v, pivot,ret);
		float t = ret.x;
		float cosa = (float)Math.cos(angle);
		float sina = (float)Math.sin(angle);
		ret.x = t*cosa - ret.y*sina;
		ret.y = t*sina + ret.y*cosa;
		CGPointUtil.add(ret, pivot);
	}
}
