package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// MoveTo
//

public class MoveTo extends IntervalAction {
    private float endPositionX;
    private float endPositionY;
    private float startPositionX;
    private float startPositionY;
    protected float deltaX;
    protected float deltaY;

    public static MoveTo action(float t, float x, float y) {
        return new MoveTo(t, x, y);
    }

    protected MoveTo(float t, float x, float y) {
        super(t);
        endPositionX = x;
        endPositionY = y;
    }

    @Override
    public IntervalAction copy() {
        return new MoveTo(duration, endPositionX, endPositionY);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);

        startPositionX = target.getPositionX();
        startPositionY = target.getPositionY();
        deltaX = endPositionX - startPositionX;
        deltaY = endPositionY - startPositionY;
    }

    @Override
    public void update(float t) {
        target.setPosition(startPositionX + deltaX * t, startPositionY + deltaY * t);
    }
}
