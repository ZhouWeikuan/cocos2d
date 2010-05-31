package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;


public class EaseIn extends EaseRateAction {

    public static EaseIn action(IntervalAction action, float rate) {
        return new EaseIn(action, rate);
    }

    protected EaseIn(IntervalAction action, float rate) {
        super(action, rate);
    }

    @Override
    public void update(float t) {
        other.update((float) Math.pow(t, rate));
    }

}
