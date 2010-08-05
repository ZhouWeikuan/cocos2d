package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseBackOut extends CCEaseAction {

    public static CCEaseBackOut action(CCIntervalAction action) {
        return new CCEaseBackOut(action);
    }

    protected CCEaseBackOut(CCIntervalAction action) {
        super(action);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseBackOut(other.copy());
    }

    @Override
    public void update(float t) {
        float overshoot = 1.70158f;

        t = t - 1;
        other.update(t * t * ((overshoot + 1) * t + overshoot) + 1);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseBackIn(other.reverse());
    }

}
