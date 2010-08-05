package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseBounceIn extends CCEaseBounce {

    public static CCEaseBounceIn action(CCIntervalAction action) {
        return new CCEaseBounceIn(action);
    }

    protected CCEaseBounceIn(CCIntervalAction action) {
        super(action);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseBounceIn(other.copy());
    }

    @Override
    public void update(float t) {
        float newT = 1 - bounceTime(1 - t);
        other.update(newT);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseBounceOut(other.reverse());
    }

}
