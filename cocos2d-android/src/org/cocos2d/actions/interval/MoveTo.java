package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

//
// MoveTo
//

public class MoveTo extends IntervalAction {
    private CGPoint endPosition;
    private CGPoint startPosition;
    protected float deltaX;
    protected float deltaY;

    public static MoveTo action(float t, float x, float y) {
        return new MoveTo(t, x, y);
    }

    protected MoveTo(float t, float x, float y) {
        super(t);
        endPosition = CGPoint.make(x, y);
    }

    @Override
    public IntervalAction copy() {
        return new MoveTo(duration, endPosition.x, endPosition.y);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        startPosition = target.getPosition();
        deltaX = endPosition.x - startPosition.x;
        deltaY = endPosition.y - startPosition.y;
    }

    @Override
    public void update(float t) {
        target.setPosition(CGPoint.make(startPosition.x + deltaX * t, startPosition.y + deltaY * t));
    }
}
