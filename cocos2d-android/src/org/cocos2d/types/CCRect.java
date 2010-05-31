package org.cocos2d.types;

public class CCRect {
    public CCPoint origin;
    public CCSize size;

    private CCRect() {
        this(0, 0, 0, 0);
    }

    private CCRect(final CCPoint origin, final CCSize size) {
        this(origin.x, origin.y, size.width, size.height);
    }

    public static CCRect make(final CCPoint origin, final CCSize size) {
        return new CCRect(origin.x, origin.y, size.width, size.height);
    }

    public static CCRect make(float x, float y, float w, float h) {
        return new CCRect(x, y, w, h);
    }

    private CCRect(float x, float y, float w, float h) {
        origin = CCPoint.ccp(x, y);
        size = CCSize.make(w, h);
    }

    public boolean contains(float x, float y) {
        return size.width > 0 && size.height > 0  // check for empty first
                && x >= origin.x && x < (origin.x + size.width)
                && y >= origin.y && y < (origin.y + size.height);
    }

    public String toString() {
        return "((" + origin.x + ", " + origin.y + "),(" + size.width + ", " + size.height + "))";
    }


    public static boolean equalToRect(final CCRect r1, final CCRect r2) {
        return CCPoint.equalToPoint(r1.origin, r2.origin) && CCSize.equalToSize(r1.size, r2.size);
    }

    /**
     * Returns true if aPoint is inside aRect.
     */
    public static boolean containsPoint(final CCRect aRect, final CCPoint aPoint) {
        return ((aPoint.x >= minX(aRect))
                && (aPoint.y >= minY(aRect))
                && (aPoint.x < maxX(aRect))
                && (aPoint.y < maxY(aRect)));
    }

    public static boolean containsRect(final CCRect aRect, final CCRect bRect) {
        return (!isEmptyRect(bRect)
                && (minX(aRect) <= minX(bRect))
                && (minY(aRect) <= minY(bRect))
                && (maxX(aRect) >= maxX(bRect))
                && (maxY(aRect) >= maxY(bRect)));
    }

    /**
     * Returns the greatest x-coordinate value still inside aRect.
     */
    public static float maxX(final CCRect aRect) {
        return aRect.origin.x + aRect.size.width;
    }

    /**
     * Returns the greatest y-coordinate value still inside aRect.
     */
    public static float maxY(final CCRect aRect) {
        return aRect.origin.y + aRect.size.height;
    }

    /**
     * Returns the x-coordinate of aRect's middle point.
     */
    public static float midX(CCRect aRect) {
        return aRect.origin.x + (float) (aRect.size.width / 2.0);
    }

    /**
     * Returns the y-coordinate of aRect's middle point.
     */
    public static float midY(final CCRect aRect) {
        return aRect.origin.y + (float) (aRect.size.height / 2.0);
    }

    /**
     * Returns the least x-coordinate value still inside aRect.
     */
    public static float minX(final CCRect aRect) {
        return aRect.origin.x;
    }

    /**
     * Returns the least y-coordinate value still inside aRect.
     */
    public static float minY(final CCRect aRect) {
        return aRect.origin.y;
    }

    /**
     * Returns aRect's width.
     */
    public static float width(final CCRect aRect) {
        return aRect.size.width;
    }

    /**
     * Returns aRect's height.
     */
    public static float height(final CCRect aRect) {
        return aRect.size.height;
    }


    public static boolean isEmptyRect(CCRect aRect) {
        return (!(aRect.size.width > 0 && aRect.size.height > 0));
    }


    public enum Edge {
        MinXEdge,
        MinYEdge,
        MaxXEdge,
        MaxYEdge,
    }

    private static CCRect sRect = new CCRect();
    private static CCRect rRect = new CCRect();

    public static void divideRect(final CCRect aRect, CCRect[] slice, CCRect[] remainder, float amount, CCRect.Edge edge) {

        if (slice == null) {
            slice = new CCRect[1];
        	slice[0] = sRect;
        }

        if (remainder == null) {
        	remainder = new CCRect[1];
            remainder[0] = rRect;
        }

        if (isEmptyRect(aRect)) {
            slice[0] = CCRect.make(0, 0, 0, 0);
            remainder[0] = CCRect.make(0, 0, 0, 0);
            return;
        }

        switch (edge) {
            case MinXEdge:
                if (amount > aRect.size.width) {
                    slice[0] = aRect;
                    remainder[0] = CCRect.make(maxX(aRect),
                            aRect.origin.y,
                            0,
                            aRect.size.height);
                } else {
                    slice[0] = CCRect.make(aRect.origin.x,
                            aRect.origin.y,
                            amount,
                            aRect.size.height);
                    remainder[0] = CCRect.make(maxX(slice[0]),
                            aRect.origin.y,
                            maxX(aRect) - maxX(slice[0]),
                            aRect.size.height);
                }
                break;
            case MinYEdge:
                if (amount > aRect.size.height) {
                    slice[0] = aRect;
                    remainder[0] = CCRect.make(aRect.origin.x,
                            maxY(aRect),
                            aRect.size.width, 0);
                } else {
                    slice[0] = CCRect.make(aRect.origin.x,
                            aRect.origin.y,
                            aRect.size.width,
                            amount);
                    remainder[0] = CCRect.make(aRect.origin.x,
                            maxY(slice[0]),
                            aRect.size.width,
                            maxY(aRect) - maxY(slice[0]));
                }
                break;
            case MaxXEdge:
                if (amount > aRect.size.width) {
                    slice[0] = aRect;
                    remainder[0] = CCRect.make(aRect.origin.x,
                            aRect.origin.y,
                            0,
                            aRect.size.height);
                } else {
                    slice[0] = CCRect.make(maxX(aRect) - amount,
                            aRect.origin.y,
                            amount,
                            aRect.size.height);
                    remainder[0] = CCRect.make(aRect.origin.x,
                            aRect.origin.y,
                            minX(slice[0]) - aRect.origin.x,
                            aRect.size.height);
                }
                break;
            case MaxYEdge:
                if (amount > aRect.size.height) {
                    slice[0] = aRect;
                    remainder[0] = CCRect.make(aRect.origin.x,
                            aRect.origin.y,
                            aRect.size.width,
                            0);
                } else {
                    slice[0] = CCRect.make(aRect.origin.x,
                            maxY(aRect) - amount,
                            aRect.size.width,
                            amount);
                    remainder[0] = CCRect.make(aRect.origin.x,
                            aRect.origin.y,
                            aRect.size.width,
                            minY(slice[0]) - aRect.origin.y);
                }
                break;
            default:
                break;
        }
    }
}
