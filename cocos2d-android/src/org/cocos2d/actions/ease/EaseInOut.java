package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseInOut extends EaseRateAction {

    public static EaseInOut action(IntervalAction action, float rate) {
        return new EaseInOut(action, rate);
    }

    protected EaseInOut(IntervalAction action, float rate) {
        super(action, rate);
    }

    @Override
    public void update(float t) {
        int sign = 1;
        int r = (int) rate;
        if (r % 2 == 0)
            sign = -1;

        if ((t *= 2) < 1)
            other.update(0.5f * (float) Math.pow(t, 1 / rate));
        else
            other.update(sign * 0.5f * ((float) Math.pow(t - 2, rate) + sign * 2));
    }

}
