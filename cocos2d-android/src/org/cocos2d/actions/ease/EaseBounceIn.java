package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseBounceIn extends EaseBounce {

    public static EaseBounceIn action(IntervalAction action) {
        return new EaseBounceIn(action);
    }

    protected EaseBounceIn(IntervalAction action) {
        super(action);
    }

    @Override
    public EaseAction copy() {
        return new EaseBounceIn(other.copy());
    }

    public void update(float t) {
        float newT = 1 - bounceTime(1 - t);
        other.update(newT);
    }

    public IntervalAction reverse() {
        return new EaseBounceOut(other.reverse());
    }

}
