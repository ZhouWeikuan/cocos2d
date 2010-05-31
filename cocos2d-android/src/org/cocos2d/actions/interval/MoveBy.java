package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// MoveBy
//

public class MoveBy extends MoveTo {

    public static MoveBy action(float t, float x, float y) {
        return new MoveBy(t, x, y);
    }

    protected MoveBy(float t, float x, float y) {
        super(t, x, y);
        deltaX = x;
        deltaY = y;
    }

    @Override
    public IntervalAction copy() {
        return new MoveBy(duration, deltaX, deltaY);
    }

    @Override
    public void start(CocosNode aTarget) {
        float savedX = deltaX;
        float savedY = deltaY;
        super.start(aTarget);
        deltaX = savedX;
        deltaY = savedY;
    }

    @Override
    public IntervalAction reverse() {
        return new MoveBy(duration, -deltaX, -deltaY);
    }
}
