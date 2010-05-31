package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseBackOut extends EaseAction {

    public static EaseBackOut action(IntervalAction action) {
        return new EaseBackOut(action);
    }

    protected EaseBackOut(IntervalAction action) {
        super(action);
    }

    @Override
    public EaseAction copy() {
        return new EaseBackOut(other.copy());
    }

    public void update(float t) {
        float overshoot = 1.70158f;

        t = t - 1;
        other.update(t * t * ((overshoot + 1) * t + overshoot) + 1);
    }

    public IntervalAction reverse() {
        return new EaseBackIn(other.reverse());
    }

}
