package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

/** Scales a CCNode object to a zoom factor by modifying it's scale attribute.
 @warning This action doesn't support "reverse"
 */
public class CCScaleTo extends CCIntervalAction {
    protected float scaleX;
    protected float scaleY;
    protected float startScaleX;
    protected float startScaleY;
    protected float endScaleX;
    protected float endScaleY;
    protected float deltaX;
    protected float deltaY;

    /** creates the action with the same scale factor for X and Y */
    public static CCScaleTo action(float t, float s) {
        return new CCScaleTo(t, s);
    }

    /** creates the action with and X factor and a Y factor */
    public static CCScaleTo action(float t, float sx, float sy) {
        return new CCScaleTo(t, sx, sy);
    }

    /** initializes the action with the same scale factor for X and Y */
    protected CCScaleTo(float t, float s) {
        this(t, s, s);
    }

    /** initializes the action with and X factor and a Y factor */
    protected CCScaleTo(float t, float sx, float sy) {
        super(t);
        endScaleX = sx;
        endScaleY = sy;
    }

    @Override
    public CCScaleTo copy() {
        return new CCScaleTo(duration, endScaleX, endScaleY);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        startScaleX = target.getScaleX();
        startScaleY = target.getScaleY();
        deltaX = endScaleX - startScaleX;
        deltaY = endScaleY - startScaleY;
    }

    @Override
    public void update(float t) {
        target.setScaleX(startScaleX + deltaX * t);
        target.setScaleY(startScaleY + deltaY * t);
    }
}

