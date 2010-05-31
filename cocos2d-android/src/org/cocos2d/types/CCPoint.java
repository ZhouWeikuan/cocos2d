package org.cocos2d.types;

public class CCPoint {
    public float x, y;

    public static CCPoint zero() {
        return new CCPoint(0, 0);
    }

    public CCPoint() {
        this(0, 0);
    }

    public static CCPoint make(float x, float y) {
        return new CCPoint(x, y);
    }

    private CCPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static boolean equalToPoint(CCPoint p1, CCPoint p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }


    public static CCPoint applyAffineTransform(CCPoint aPoint, CCAffineTransform aTransform) {

        return aTransform.transform(aPoint, null);
    }

    /**
     * Helper macro that creates a CCPoint
     *
     * @return CCPoint
     */
    public static CCPoint ccp(float x, float y) {
        return new CCPoint(x, y);
    }


    /**
     * Returns opposite of point.
     *
     * @return CCPoint
     */
    public static CCPoint ccpNeg(final CCPoint v) {
        return ccp(-v.x, -v.y);
    }

    /**
     * Calculates sum of two points.
     *
     * @return CCPoint
     */
    public static CCPoint ccpAdd(final CCPoint v1, final CCPoint v2) {
        return ccp(v1.x + v2.x, v1.y + v2.y);
    }

    /**
     * Calculates difference of two points.
     *
     * @return CCPoint
     */
    public static CCPoint ccpSub(final CCPoint v1, final CCPoint v2) {
        return ccp(v1.x - v2.x, v1.y - v2.y);
    }

    /**
     * Returns point multiplied by given factor.
     *
     * @return CCPoint
     */
    public static CCPoint ccpMult(final CCPoint v, final float s) {
        return ccp(v.x * s, v.y * s);
    }

    /**
     * Calculates midpoint between two points.
     *
     * @return CCPoint
     */
    public static CCPoint ccpMidpoint(final CCPoint v1, final CCPoint v2) {
        return ccpMult(ccpAdd(v1, v2), 0.5f);
    }

    /**
     * Calculates dot product of two points.
     *
     * @return float
     */
    public static float ccpDot(final CCPoint v1, final CCPoint v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    /**
     * Calculates cross product of two points.
     *
     * @return float
     */
    public static float ccpCross(final CCPoint v1, final CCPoint v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    /**
     * Calculates perpendicular of v, rotated 90 degrees counter-clockwise -- cross(v, perp(v)) >= 0
     *
     * @return CCPoint
     */
    public static CCPoint ccpPerp(final CCPoint v) {
        return ccp(-v.y, v.x);
    }

    /**
     * Calculates perpendicular of v, rotated 90 degrees clockwise -- cross(v, rperp(v)) <= 0
     *
     * @return CCPoint
     */
    public static CCPoint ccpRPerp(final CCPoint v) {
        return ccp(v.y, -v.x);
    }

    /**
     * Calculates the projection of v1 over v2.
     *
     * @return CCPoint
     */
    public static CCPoint ccpProject(final CCPoint v1, final CCPoint v2) {
        return ccpMult(v2, ccpDot(v1, v2) / ccpDot(v2, v2));
    }

    /**
     * Rotates two points.
     *
     * @return CCPoint
     */
    public static CCPoint ccpRotate(final CCPoint v1, final CCPoint v2) {
        return ccp(v1.x * v2.x - v1.y * v2.y, v1.x * v2.y + v1.y * v2.x);
    }

    /**
     * Unrotates two points.
     *
     * @return CCPoint
     */
    public static CCPoint ccpUnrotate(final CCPoint v1, final CCPoint v2) {
        return ccp(v1.x * v2.x + v1.y * v2.y, v1.y * v2.x - v1.x * v2.y);
    }

    /**
     * Calculates the square length of a CCPoint (not calling sqrt() )
     *
     * @return float
     */
    public static float ccpLengthSQ(final CCPoint v) {
        return ccpDot(v, v);
    }

    /**
     * Calculates distance between point an origin
     *
     * @return float
     */
    public static float ccpLength(final CCPoint v) {
        return (float)Math.sqrt(ccpLengthSQ(v));
    }

    /**
     * Calculates the distance between two points
     *
     * @return float
     */
    public static float ccpDistance(final CCPoint v1, final CCPoint v2) {
        return ccpLength(ccpSub(v1, v2));
    }

    /**
     * Returns point multiplied to a length of 1.
     *
     * @return CCPoint
     */
    public static CCPoint ccpNormalize(final CCPoint v) {
        return ccpMult(v, 1.0f / ccpLength(v));
    }

    /**
     * Converts radians to a normalized vector.
     *
     * @return CCPoint
     */
    public static CCPoint ccpForAngle(final float a) {
        return ccp((float)Math.cos(a), (float)Math.sin(a));
    }

    /**
     * Converts a vector to radians.
     *
     * @return float
     */
    public static float ccpToAngle(final CCPoint v) {
        return (float) Math.atan2(v.y, v.x);
    }


}
