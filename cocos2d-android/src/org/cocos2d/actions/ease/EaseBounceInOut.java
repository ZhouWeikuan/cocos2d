package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseBounceInOut extends EaseBounce {

    public static EaseBounceInOut action(IntervalAction action) {
        return new EaseBounceInOut(action);
    }

    protected EaseBounceInOut(IntervalAction action) {
        super(action);
    }

    @Override
    public EaseAction copy() {
        return new EaseBounceInOut(other.copy());
    }

    public void update(float t) {
        float newT = 0;
        if (t < 0.5) {
            t = t * 2;
            newT = (1 - bounceTime(1 - t)) * 0.5f;
        } else
            newT = bounceTime(t * 2 - 1) * 0.5f + 0.5f;

        other.update(newT);
    }

    public IntervalAction reverse() {
        return new EaseBounceInOut(other.reverse());
    }

}
