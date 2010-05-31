package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseElasticInOut extends EaseElastic {

    public static EaseElasticInOut action(IntervalAction action) {
        return new EaseElasticInOut(action, 0.3f);
    }

    public static EaseElasticInOut action(IntervalAction action, float period) {
        return new EaseElasticInOut(action, period);
    }

    protected EaseElasticInOut(IntervalAction action, float period) {
        super(action, period);
    }

    @Override
    public EaseAction copy() {
        return new EaseElasticInOut(other.copy(), period_);
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
                newT = (float) (-0.5f * Math.pow(2, 10 * t) * Math.sin((t - s) * (Math.PI * 2) / period_));
            } else {
                newT = (float) (Math.pow(2, -10 * t) * Math.sin((t - s) * (Math.PI * 2) / period_) * 0.5f + 1);
            }
        }
        other.update(newT);
    }

    @Override
    public IntervalAction reverse() {
        return new EaseElasticInOut(other.reverse(), period_);
    }

}
