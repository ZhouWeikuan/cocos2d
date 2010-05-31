package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;


/**
 * Base class for Easing actions with rate parameters
 */

public class EaseRateAction extends EaseAction {
    float rate;

    protected EaseRateAction(IntervalAction action, float aRate) {
        super(action);
        rate = aRate;
    }

    @Override
    public EaseRateAction copy() {
        return new EaseRateAction(other.copy(), rate);
    }

    @Override
    public IntervalAction reverse() {
        return new EaseRateAction(other.reverse(), 1 / rate);
    }

}
