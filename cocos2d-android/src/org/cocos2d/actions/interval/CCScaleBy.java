package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

/** 
 * Scales a CCNode object a zoom factor by modifying it's scale attribute.
*/
public class CCScaleBy extends CCScaleTo {

    public static CCScaleBy action(float t, float s) {
        return new CCScaleBy(t, s, s);
    }

    public static CCScaleBy action(float t, float sx, float sy) {
        return new CCScaleBy(t, sx, sy);
    }

    protected CCScaleBy(float t, float s) {
        super(t, s, s);
    }

    protected CCScaleBy(float t, float sx, float sy) {
        super(t, sx, sy);
    }


    @Override
    public CCScaleBy copy() {
        return new CCScaleBy(duration, endScaleX, endScaleY);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        deltaX = startScaleX * endScaleX - startScaleX;
        deltaY = startScaleY * endScaleY - startScaleY;
    }

    @Override
    public CCScaleBy reverse() {
        return new CCScaleBy(duration, 1 / endScaleX, 1 / endScaleY);
    }
}

