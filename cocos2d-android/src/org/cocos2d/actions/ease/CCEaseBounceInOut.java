package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseBounceInOut extends CCEaseBounce {

    public static CCEaseBounceInOut action(CCIntervalAction action) {
        return new CCEaseBounceInOut(action);
    }

    protected CCEaseBounceInOut(CCIntervalAction action) {
        super(action);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseBounceInOut(other.copy());
    }

    @Override
    public void update(float t) {
        float newT = 0;
        if (t < 0.5) {
            t = t * 2;
            newT = (1 - bounceTime(1 - t)) * 0.5f;
        } else
            newT = bounceTime(t * 2 - 1) * 0.5f + 0.5f;

        other.update(newT);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseBounceInOut(other.reverse());
    }

}
