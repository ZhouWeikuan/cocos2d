package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseBounceOut extends EaseBounce {

    public static EaseBounceOut action(IntervalAction action) {
        return new EaseBounceOut(action);
    }

    protected EaseBounceOut(IntervalAction action) {
        super(action);
    }

    @Override
    public EaseAction copy() {
        return new EaseBounceOut(other.copy());
    }

    public void update(float t) {
        float newT = bounceTime(t);
        other.update(newT);
    }

    public IntervalAction reverse() {
        return new EaseBounceIn(other.reverse());
    }

}
