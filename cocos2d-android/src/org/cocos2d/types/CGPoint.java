package org.cocos2d.types;

import org.cocos2d.config.ccMacros;

public class CGPoint {
    private static final float kCGPointEpsilon = 0.00000012f; 
    public float x, y;
    
    private static final CGPoint ZERO_POINT = new CGPoint(0, 0);
    public static CGPoint getZero() {
    	return ZERO_POINT;
    }
    
    public static CGPoint zero() {
        return new CGPoint(0, 0);
    }

    public static CGPoint make(float x, float y) {
        return new CGPoint(x, y);
    }

    public CGPoint() {
        this(0, 0);
    }

    private CGPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Set value
     */
    public void set(float x, float y) {
    	this.x = x;
    	this.y = y;
    }
    
    public void set(CGPoint p) {
    	this.x = p.x;
    	this.y = p.y;
    }
    
    
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static boolean equalToPoint(CGPoint p1, CGPoint p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    public static CGPoint applyAffineTransform(CGPoint aPoint, CGAffineTransform aTransform) {
        return aTransform.applyTransform(aPoint);
    }

    /**
     * Helper macro that creates a CCPoint
     *
     * @return CCPoint
     */
    public static CGPoint ccp(float x, float y) {
        return new CGPoint(x, y);
    }


    /**
     * Returns opposite of point.
     *
     * @return CCPoint
     */
    public static CGPoint ccpNeg(final CGPoint v) {
        return ccp(-v.x, -v.y);
    }

    /**
     * Calculates sum of two points.
     *
     * @return CCPoint
     */
    public static CGPoint ccpAdd(final CGPoint v1, final CGPoint v2) {
        return ccp(v1.x + v2.x, v1.y + v2.y);
    }

    /**
     * Calculates difference of two points.
     *
     * @return CCPoint
     */
    public static CGPoint ccpSub(final CGPoint v1, final CGPoint v2) {
        return ccp(v1.x - v2.x, v1.y - v2.y);
    }

    /**
     * Returns point multiplied by given factor.
     *
     * @return CCPoint
     */
    public static CGPoint ccpMult(final CGPoint v, final float s) {
        return ccp(v.x * s, v.y * s);
    }

    /**
     * Calculates midpoint between two points.
     *
     * @return CCPoint
     */
    public static CGPoint ccpMidpoint(final CGPoint v1, final CGPoint v2) {
        return ccpMult(ccpAdd(v1, v2), 0.5f);
    }

    /**
     * Calculates dot product of two points.
     *
     * @return float
     */
    public static float ccpDot(final CGPoint v1, final CGPoint v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    /**
     * Calculates cross product of two points.
     *
     * @return float
     */
    public static float ccpCross(final CGPoint v1, final CGPoint v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    /**
     * Calculates perpendicular of v, rotated 90 degrees counter-clockwise -- cross(v, perp(v)) >= 0
     *
     * @return CCPoint
     */
    public static CGPoint ccpPerp(final CGPoint v) {
        return ccp(-v.y, v.x);
    }

    /**
     * Calculates perpendicular of v, rotated 90 degrees clockwise -- cross(v, rperp(v)) <= 0
     *
     * @return CCPoint
     */
    public static CGPoint ccpRPerp(final CGPoint v) {
        return ccp(v.y, -v.x);
    }

    /**
     * Calculates the projection of v1 over v2.
     *
     * @return CCPoint
     */
    public static CGPoint ccpProject(final CGPoint v1, final CGPoint v2) {
        return ccpMult(v2, ccpDot(v1, v2) / ccpDot(v2, v2));
    }

    /**
     * Rotates two points.
     *
     * @return CCPoint
     */
    public static CGPoint ccpRotate(final CGPoint v1, final CGPoint v2) {
        return ccp(v1.x * v2.x - v1.y * v2.y, v1.x * v2.y + v1.y * v2.x);
    }

    /**
     * Unrotates two points.
     *
     * @return CCPoint
     */
    public static CGPoint ccpUnrotate(final CGPoint v1, final CGPoint v2) {
        return ccp(v1.x * v2.x + v1.y * v2.y, v1.y * v2.x - v1.x * v2.y);
    }

    /**
     * Calculates the square length of a CCPoint (not calling sqrt() )
     *
     * @return float
     */
    public static float ccpLengthSQ(final CGPoint v) {
        return ccpDot(v, v);
    }

    /** Calculates distance between point and origin
     @return CGFloat
     @since v0.7.2
     */
    public static float ccpLength(final CGPoint v) {
        return (float)Math.sqrt(ccpLengthSQ(v));
    }

    /**
     * Calculates the distance between two points
     *
     * @return float
     */
    public static float ccpDistance(final CGPoint v1, final CGPoint v2) {
        return ccpLength(ccpSub(v1, v2));
    }

    /**
     * Returns point multiplied to a length of 1.
     *
     * @return CCPoint
     */
    public static CGPoint ccpNormalize(final CGPoint v) {
        return ccpMult(v, 1.0f / ccpLength(v));
    }

    /**
     * Converts radians to a normalized vector.
     *
     * @return CCPoint
     */
    public static CGPoint ccpForAngle(final float a) {
        return ccp((float)Math.cos(a), (float)Math.sin(a));
    }

    /**
     * Converts a vector to radians.
     *
     * @return float
     */
    public static float ccpToAngle(final CGPoint v) {
        return (float) Math.atan2(v.y, v.x);
    }

    /**
     *  Caculate the rotation(in degrees) between two points,
     *      so that when we move from one point to the other,
     *      we can set the correct rotation to head to that point.
     *
     * @param from
     * @param to
     * @return the rotation in degrees
     */
    public static float ccpCalcRotate(final CGPoint from, final CGPoint to) {
        float o = to.x - from.x;
        float a = to.y - from.y;
        float at = ccMacros.CC_RADIANS_TO_DEGREES((float) Math.atan(o / a));

        if (a < 0) {
            if (o < 0)
                at = 180 + Math.abs(at);
            else
                at = 180 - Math.abs(at);
        }

        return at;
    }


    /** @returns the angle in radians between two vector directions
      @since v0.99.1
      */
    public static float ccpAngle(CGPoint a, CGPoint b) {
        float angle = (float)Math.acos(ccpDot(ccpNormalize(a), ccpNormalize(b)));
        if( Math.abs(angle) < kCGPointEpsilon ) return 0.f;
        return angle;
    }

    /** Linear Interpolation between two points a and b
     @returns
        alpha == 0 ? a
        alpha == 1 ? b
        otherwise a value between a..b
     @since v0.99.1
     */
    public static CGPoint ccpLerp(CGPoint a, CGPoint b, float alpha) {
        return ccpAdd(ccpMult(a, 1.f - alpha), ccpMult(b, alpha));
    }

    /** Clamp a value between from and to.
      @since v0.99.1
      */
    public static float clampf(float value, float min_inclusive, float max_inclusive) {
        if (min_inclusive > max_inclusive) {
            float tmp = min_inclusive;
            min_inclusive = max_inclusive;
            max_inclusive = tmp;
        }

        return value < min_inclusive ? min_inclusive : value < max_inclusive? value : max_inclusive;
    }

    /** Clamp a point between from and to.
      @since v0.99.1
      */
    public static CGPoint ccpClamp(CGPoint p, CGPoint min_inclusive, CGPoint max_inclusive) {
        return ccp(clampf(p.x,min_inclusive.x,max_inclusive.x), 
                    clampf(p.y, min_inclusive.y, max_inclusive.y));
    }

    /** Quickly convert CGSize to a CGPoint
      @since v0.99.1
      */
    public static CGPoint ccpFromSize(CGSize s) {
        return ccp(s.width, s.height);
    }

    /** @returns if points have fuzzy equality which means equal with some degree of variance.
      @since v0.99.1
      */
    public static boolean ccpFuzzyEqual(CGPoint a, CGPoint b, float var) {
        if(a.x - var <= b.x && b.x <= a.x + var)
            if(a.y - var <= b.y && b.y <= a.y + var)
                return true;
        return false;
    }

    /** Multiplies a nd b components, a.x*b.x, a.y*b.y
      @returns a component-wise multiplication
      @since v0.99.1
      */
    public static CGPoint ccpCompMult(CGPoint a, CGPoint b) {
        return ccp(a.x * b.x, a.y * b.y);
    }

    /** @returns the signed angle in radians between two vector directions
      @since v0.99.1
      */
    public static float ccpAngleSigned(CGPoint a, CGPoint b) {
        CGPoint a2 = ccpNormalize(a);
        CGPoint b2 = ccpNormalize(b);
        float angle = (float)Math.atan2(a2.x * b2.y - a2.y * b2.x, ccpDot(a2, b2));
        if( Math.abs(angle) < kCGPointEpsilon ) return 0.f;
        return angle;
    }

    /** Rotates a point counter clockwise by the angle around a pivot
      @param v is the point to rotate
      @param pivot is the pivot, naturally
      @param angle is the angle of rotation cw in radians
      @returns the rotated point
      @since v0.99.1
      */
    public static CGPoint ccpRotateByAngle(CGPoint v, CGPoint pivot, float angle) {
        CGPoint r = ccpSub(v, pivot);
        float t = r.x;
        float cosa = (float)Math.cos(angle);
        float sina = (float)Math.sin(angle);
        r.x = t*cosa - r.y*sina;
        r.y = t*sina + r.y*cosa;
        r = ccpAdd(r, pivot);
        return r;
    }

    /** A general line-line intersection test
      @param p1 
      is the startpoint for the first line P1 = (p1 - p2)
      @param p2 
      is the endpoint for the first line P1 = (p1 - p2)
      @param p3 
      is the startpoint for the second line P2 = (p3 - p4)
      @param p4 
      is the endpoint for the second line P2 = (p3 - p4)
      @param s 
      is the range for a hitpoint in P1 (pa = p1 + s*(p2 - p1))
      @param t
      is the range for a hitpoint in P3 (pa = p2 + t*(p4 - p3))
      @return bool 
      indicating successful intersection of a line
      note that to truly test intersection for segments we have to make 
      sure that s & t lie within [0..1] and for rays, make sure s & t > 0
      the hit point is		p3 + t * (p4 - p3);
      the hit point also is	p1 + s * (p2 - p1);
      @since v0.99.1
      */
    public static boolean ccpLineIntersect(CGPoint p1, CGPoint p2, 
            CGPoint p3, CGPoint p4, CGPoint ret){
        CGPoint p13, p43, p21;
        float d1343, d4321, d1321, d4343, d2121;
        float numer, denom;

        p13 = ccpSub(p1, p3);

        p43 = ccpSub(p4, p3);

        //Roughly equal to zero but with an epsilon deviation for float 
        //correction
        if (ccpFuzzyEqual(p43, CGPoint.zero(), kCGPointEpsilon))
            return false;

        p21 = ccpSub(p2, p1);

        //Roughly equal to zero
        if (ccpFuzzyEqual(p21,CGPoint.zero(), kCGPointEpsilon))
            return false;

        d1343 = ccpDot(p13, p43);
        d4321 = ccpDot(p43, p21);
        d1321 = ccpDot(p13, p21);
        d4343 = ccpDot(p43, p43);
        d2121 = ccpDot(p21, p21);

        denom = d2121 * d4343 - d4321 * d4321;
        if (Math.abs(denom) < kCGPointEpsilon)
            return false;
        numer = d1343 * d4321 - d1321 * d4343;

        ret.x = numer / denom;
        ret.y = (d1343 + d4321 * ret.x) / d4343;

        return true;
    }
}

