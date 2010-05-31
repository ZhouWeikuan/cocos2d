package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public abstract class EaseElastic extends EaseAction {
    protected float period_;

    protected EaseElastic(IntervalAction action, float period) {
        super(action);
        period_ = period;
    }

    @Override
    public abstract EaseAction copy();

    @Override
    public abstract IntervalAction reverse();
}
