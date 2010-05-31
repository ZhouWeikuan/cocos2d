package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseElasticIn extends EaseElastic {

    public static EaseElasticIn action(IntervalAction action) {
        return new EaseElasticIn(action, 0.3f);
    }

    public static EaseElasticIn action(IntervalAction action, float period) {
        return new EaseElasticIn(action, period);
    }

    protected EaseElasticIn(IntervalAction action, float period) {
        super(action, period);
    }

    @Override
    public EaseAction copy() {
        return new EaseElasticIn(other.copy(), period_);
    }

    public void update(float t) {
        float newT = 0;
        if (t == 0 || t == 1) {
            newT = t;

        } else {
            float s = period_ / 4;
            t = t - 1;
            newT = (float) (-Math.pow(2, 10 * t) * Math.sin((t - s) * (Math.PI * 2) / period_));
        }
        other.update(newT);
    }

    public IntervalAction reverse() {
        return new EaseElasticOut(other.reverse(), period_);
    }

}
