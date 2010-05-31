package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseExponentialIn extends EaseAction {

    public static EaseExponentialIn action(IntervalAction action) {
        return new EaseExponentialIn(action);
    }

    protected EaseExponentialIn(IntervalAction action) {
        super(action);
    }

    @Override
    public void update(float t) {
        other.update((t == 0) ? 0 : (float) Math.pow(2, 10 * (t / 1 - 1)) - 1 * 0.001f);
    }

    @Override
    public IntervalAction reverse() {
        return new EaseExponentialOut(other.reverse());
    }

}

