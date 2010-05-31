package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseBackInOut extends EaseAction {

    public static EaseBackInOut action(IntervalAction action) {
        return new EaseBackInOut(action);
    }

    protected EaseBackInOut(IntervalAction action) {
        super(action);
    }

    @Override
    public EaseAction copy() {
        return new EaseBackInOut(other.copy());
    }

    public void update(float t) {
        float overshoot = 1.70158f * 1.525f;

        t = t * 2;
        if (t < 1) {
            other.update((t * t * ((overshoot + 1) * t - overshoot)) / 2);
        } else {
            t = t - 2;
            other.update((t * t * ((overshoot + 1) * t + overshoot)) / 2 + 1);
        }
    }

    public IntervalAction reverse() {
        return new EaseBackInOut(other.reverse());
    }

}
