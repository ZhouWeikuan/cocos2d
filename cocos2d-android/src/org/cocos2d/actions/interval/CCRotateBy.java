package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

//
// RotateBy
//

/**
 * Rotates a CCNode object clockwise a number of degrees by modiying it's rotation attribute.
*/
public class CCRotateBy extends CCIntervalAction {
    private float angle;
    private float startAngle;

    /** creates the action */
    public static CCRotateBy action(float t, float a) {
        return new CCRotateBy(t, a);
    }

    /** initializes the action */
    protected CCRotateBy(float t, float a) {
        super(t);
        angle = a;
    }

    @Override
    public CCRotateBy copy() {
        return new CCRotateBy(duration, angle);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        startAngle = target.getRotation();
    }

    @Override
    public void update(float t) {
        // XXX: shall I add % 360
        target.setRotation(startAngle + angle * t);
    }

    @Override
    public CCRotateBy reverse() {
        return new CCRotateBy(duration, -angle);
    }

}

