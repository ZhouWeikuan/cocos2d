package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseElasticOut extends CCEaseElastic {

    public static CCEaseElasticOut action(CCIntervalAction action) {
        return new CCEaseElasticOut(action, 0.3f);
    }

    public static CCEaseElasticOut action(CCIntervalAction action, float period) {
        return new CCEaseElasticOut(action, period);
    }

    protected CCEaseElasticOut(CCIntervalAction action, float period) {
        super(action, period);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseElasticOut(other.copy(), period_);
    }

    @Override
    public void update(float t) {
        float newT = 0;
        if (t == 0 || t == 1) {
            newT = t;
        } else {
            float s = period_ / 4;
            newT = (float) (Math.pow(2, -10 * t) * Math.sin((t - s) * M_PI_X_2  / period_) + 1);
        }
        other.update(newT);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseElasticIn(other.reverse(), period_);
    }

}
