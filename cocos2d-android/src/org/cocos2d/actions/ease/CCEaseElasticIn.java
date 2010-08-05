package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseElasticIn extends CCEaseElastic {

    public static CCEaseElasticIn action(CCIntervalAction action) {
        return new CCEaseElasticIn(action, 0.3f);
    }

    public static CCEaseElasticIn action(CCIntervalAction action, float period) {
        return new CCEaseElasticIn(action, period);
    }

    protected CCEaseElasticIn(CCIntervalAction action, float period) {
        super(action, period);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseElasticIn(other.copy(), period_);
    }

    @Override
    public void update(float t) {
        float newT = 0;
        if (t == 0 || t == 1) {
            newT = t;

        } else {
            float s = period_ / 4;
            t = t - 1;
            newT = (float) (-Math.pow(2, 10 * t) * Math.sin((t - s) * M_PI_X_2 / period_));
        }
        other.update(newT);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseElasticOut(other.reverse(), period_);
    }

}
