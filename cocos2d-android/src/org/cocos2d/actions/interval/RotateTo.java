package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// RotateTo
//

public class RotateTo extends IntervalAction {
    private float angle;
    private float startAngle;


    public static RotateTo action(float t, float a) {
        return new RotateTo(t, a);
    }

    protected RotateTo(float t, float a) {
        super(t);
        angle = a;
    }

    @Override
    public IntervalAction copy() {
        return new RotateTo(duration, angle);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        startAngle = target.getRotation();
        if (startAngle > 0)
            startAngle = (float) (startAngle % 360.0f);
        else
            startAngle = (float) (startAngle % -360.0f);

        angle -= startAngle;
        if (angle > 180)
            angle = -360 + angle;
        if (angle < -180)
            angle = 360 + angle;
    }

    @Override
    public void update(float t) {
        target.setRotation(startAngle + angle * t);
    }
}
