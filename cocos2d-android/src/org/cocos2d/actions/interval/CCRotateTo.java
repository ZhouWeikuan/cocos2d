package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

/**  Rotates a CCNode object to a certain angle by modifying it's
 rotation attribute.
 The direction will be decided by the shortest angle.
*/ 

public class CCRotateTo extends CCIntervalAction {
    private float dstAngle;
    private float diffAngle;
    private float startAngle;

    /** creates the action */
    public static CCRotateTo action(float duration, float ang) {
        return new CCRotateTo(duration, ang);
    }

    /** initializes the action */
    protected CCRotateTo(float duration, float ang) {
        super(duration);
        dstAngle = ang;
    }

    @Override
    public CCRotateTo copy() {
        return new CCRotateTo(duration, dstAngle);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        startAngle = target.getRotation();
        if (startAngle > 0)
            startAngle = (float) (startAngle % 360.0f);
        else
            startAngle = (float) (startAngle % -360.0f);

        diffAngle = dstAngle - startAngle;
        if (diffAngle > 180)
            diffAngle -= 360;
        if (diffAngle < -180)
            diffAngle += 360;
    }

    @Override
    public void update(float t) {
        target.setRotation(startAngle + diffAngle * t);
    }

}

