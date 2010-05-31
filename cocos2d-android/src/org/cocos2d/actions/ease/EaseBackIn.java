package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseBackIn extends EaseAction {

    public static EaseBackIn action(IntervalAction action) {
        return new EaseBackIn(action);
    }

    protected EaseBackIn(IntervalAction action) {
        super(action);
    }

    @Override
    public EaseAction copy() {
        return new EaseBackIn(other.copy());
    }

    public void update(float t) {
        float overshoot = 1.70158f;
        other.update(t * t * ((overshoot + 1) * t - overshoot));
    }

    public IntervalAction reverse() {
        return new EaseBackOut(other.reverse());
    }

}
