package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

/**
 * Moves a CCNode object simulating a parabolic jump movement by modifying it's position attribute.
*/
public class CCJumpBy extends CCIntervalAction {
    protected CGPoint startPosition;
    protected CGPoint delta;
    protected float height;
    protected int jumps;

    /** creates the action */
    public static CCJumpBy action(float time, CGPoint pos, float height, int jumps) {
        return new CCJumpBy(time, pos, height, jumps);
    }

    /** initializes the action */
    protected CCJumpBy(float time, CGPoint pos, float h, int j) {
        super(time);
        startPosition = CGPoint.make(0,0);
        delta = CGPoint.make(pos.x, pos.y);
        height = h;
        jumps = j;
    }

    @Override
    public CCJumpBy copy() {
        return new CCJumpBy(duration, delta, height, jumps);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        CGPoint pnt = target.getPosition();
        startPosition = CGPoint.make(pnt.x, pnt.y);
    }

    @Override
    public void update(float t) {
        // parabolic jump (since v0.8.2)
        float frac = (t * jumps) % 1.0f;
        float y = height * 4 * frac * (1 - frac);
        y += delta.y * t;
        float x = delta.x * t;
        target.setPosition(CGPoint.ccp(startPosition.x + x, startPosition.y + y));
    }

    @Override
    public CCJumpBy reverse() {
        return new CCJumpBy(duration, CGPoint.ccpNeg(delta), height, jumps);
    }
}

