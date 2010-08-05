package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseBackInOut extends CCEaseAction {

    public static CCEaseBackInOut action(CCIntervalAction action) {
        return new CCEaseBackInOut(action);
    }

    protected CCEaseBackInOut(CCIntervalAction action) {
        super(action);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseBackInOut(other.copy());
    }

    @Override
    public void update(float t) {
        float overshoot = 1.70158f * 1.525f;

        t = t * 2;
        if (t < 1) {
            other.update((t * t * ((overshoot + 1) * t - overshoot)) / 2);
        } else {
            t = t - 2;
            other.update((t * t * ((overshoot + 1) * t + overshoot)) / 2 + 1);
        }
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseBackInOut(other.reverse());
    }

}
