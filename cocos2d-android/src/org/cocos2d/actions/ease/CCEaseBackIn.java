package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseBackIn extends CCEaseAction {

    public static CCEaseBackIn action(CCIntervalAction action) {
        return new CCEaseBackIn(action);
    }

    protected CCEaseBackIn(CCIntervalAction action) {
        super(action);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseBackIn(other.copy());
    }

    @Override
    public void update(float t) {
        float overshoot = 1.70158f;
        other.update(t * t * ((overshoot + 1) * t - overshoot));
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseBackOut(other.reverse());
    }

}
