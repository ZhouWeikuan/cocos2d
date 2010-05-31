package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseExponentialInOut extends EaseAction {

    public static EaseExponentialInOut action(IntervalAction action) {
        return new EaseExponentialInOut(action);
    }

    protected EaseExponentialInOut(IntervalAction action) {
        super(action);
    }

    @Override
    public void update(float t) {
        if ((t /= 0.5f) < 1)
            t = 0.5f * (float) Math.pow(2, 10 * (t - 1));
        else
            t = 0.5f * (-(float) Math.pow(2, -10 * --t) + 2);
        other.update(t);
    }

}

