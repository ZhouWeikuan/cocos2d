package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;


public class EaseOut extends EaseRateAction {

    public static EaseOut action(IntervalAction action, float rate) {
        return new EaseOut(action, rate);
    }

    protected EaseOut(IntervalAction action, float rate) {
        super(action, rate);
    }

    @Override
    public void update(float t) {
        other.update((float) Math.pow(t, 1 / rate));
    }

}
