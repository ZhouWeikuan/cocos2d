package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public abstract class CCEaseBounce extends CCEaseAction {

    protected CCEaseBounce(CCIntervalAction action) {
        super(action);
    }

    @Override
    public abstract void update(float t);

    protected float bounceTime(float t) {
        if (t < 1 / 2.75) {
            return 7.5625f * t * t;
        } else if (t < 2 / 2.75) {
            t -= 1.5f / 2.75f;
            return 7.5625f * t * t + 0.75f;
        } else if (t < 2.5 / 2.75) {
            t -= 2.25f / 2.75f;
            return 7.5625f * t * t + 0.9375f;
        }

        t -= 2.625f / 2.75f;
        return 7.5625f * t * t + 0.984375f;
    }

    @Override
    public abstract CCIntervalAction reverse();
}
