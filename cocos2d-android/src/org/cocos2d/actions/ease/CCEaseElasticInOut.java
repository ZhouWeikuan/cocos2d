package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseElasticInOut extends CCEaseElastic {

    public static CCEaseElasticInOut action(CCIntervalAction action) {
        return new CCEaseElasticInOut(action, 0.3f);
    }

    public static CCEaseElasticInOut action(CCIntervalAction action, float period) {
        return new CCEaseElasticInOut(action, period);
    }

    protected CCEaseElasticInOut(CCIntervalAction action, float period) {
        super(action, period);
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseElasticInOut(other.copy(), period_);
    }

    public void update(float t) {
        float newT = 0;

        if (t == 0 || t == 1)
            newT = t;
        else {
            t = t * 2;
            if (period_ == 0)
                period_ = 0.3f * 1.5f;
            float s = period_ / 4;

            t = t - 1;
            if (t < 0) {
                newT = (float) (-0.5f * Math.pow(2, 10 * t) * Math.sin((t - s) * M_PI_X_2 / period_));
            } else {
                newT = (float) (Math.pow(2, -10 * t) * Math.sin((t - s) * M_PI_X_2 / period_) * 0.5f + 1);
            }
        }
        other.update(newT);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseElasticInOut(other.reverse(), period_);
    }

}
