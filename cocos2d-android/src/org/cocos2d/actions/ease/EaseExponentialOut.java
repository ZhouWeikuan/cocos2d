package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseExponentialOut extends EaseAction {

    public static EaseExponentialOut action(IntervalAction action) {
        return new EaseExponentialOut(action);
    }

    protected EaseExponentialOut(IntervalAction action) {
        super(action);
    }

    @Override
    public void update(float t) {
        other.update((t == 1) ? 1 : (-(float) (Math.pow(2, -10 * t / 1) + 1)));
    }

    @Override
    public IntervalAction reverse() {
        return new EaseExponentialOut(other.reverse());
    }

}

