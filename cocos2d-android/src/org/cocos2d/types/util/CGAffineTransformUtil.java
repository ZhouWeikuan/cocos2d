package org.cocos2d.types.util;

import org.cocos2d.types.CGAffineTransform;

public final class CGAffineTransformUtil {
	
	public static void copy(CGAffineTransform src, CGAffineTransform dst) {
		dst.setTransform(src);
	}
	
	public static void inverse(CGAffineTransform tr) {
        double det = tr.getDeterminant();
        if (Math.abs(det) >= CGAffineTransform.ZERO) {
        	double invDet = 1 / det;
        	tr.setTransform(tr.m11 * invDet, // m00
                -tr.m10 * invDet, // m10
                -tr.m01 * invDet, // m01
                 tr.m00 * invDet, // m11
                (tr.m01 * tr.m12 - tr.m11 * tr.m02) * invDet, // m02
                (tr.m10 * tr.m02 - tr.m00 * tr.m12) * invDet // m12
        	);
        }
	}
	
	public static void inverse(CGAffineTransform src, CGAffineTransform dst) {
        double det = src.getDeterminant();
        if (Math.abs(det) < CGAffineTransform.ZERO) {
//            throw new NoninvertibleTransformException("Determinant is zero");
            dst.setTransform(src);
        } else {
        	double invDet = 1 / det;
        	dst.setTransform(src.m11 * invDet, // m00
                -src.m10 * invDet, // m10
                -src.m01 * invDet, // m01
                 src.m00 * invDet, // m11
                (src.m01 * src.m12 - src.m11 * src.m02) * invDet, // m02
                (src.m10 * src.m02 - src.m00 * src.m12) * invDet // m12
        	);
        }
	}

	public static void multiply(CGAffineTransform t, CGAffineTransform m) {
        t.multiply(m);
	}
	
	/**
	 * preConcate t1 with t2
	 * @param t1 in/out
	 * @param t2 in
	 */
	public static void preConcate(CGAffineTransform t1, CGAffineTransform t2) {
		double m00 = t1.m00 * t2.m00 + t1.m10 * t2.m01;
		double m01 = t1.m00 * t2.m10 + t1.m10 * t2.m11;
		double m10 = t1.m01 * t2.m00 + t1.m11 * t2.m01;
		double m11 = t1.m01 * t2.m10 + t1.m11 * t2.m11;
		double m02 = t1.m02 * t2.m00 + t1.m12 * t2.m01 + t2.m02;
		double m12 = t1.m02 * t2.m10 + t1.m12 * t2.m11 + t2.m12;
		
		t1.m00 = m00;
		t1.m10 = m10;
		t1.m01 = m01;
		t1.m11 = m11;
		t1.m02 = m02;
		t1.m12 = m12;
	}
}
