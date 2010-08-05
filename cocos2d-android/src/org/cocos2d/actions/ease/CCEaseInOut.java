package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseInOut extends CCEaseRateAction {

    public static CCEaseInOut action(CCIntervalAction action, float rate) {
        return new CCEaseInOut(action, rate);
    }

    protected CCEaseInOut(CCIntervalAction action, float rate) {
        super(action, rate);
    }

    @Override
    public void update(float t) {
        int sign = 1;
        int r = (int) rate;
        if (r % 2 == 0)
            sign = -1;

        t *= 2;
        if (t < 1)
            other.update(0.5f * (float) Math.pow(t, rate));
        else
            other.update(sign * 0.5f * ((float) Math.pow(t - 2, rate) + sign * 2));
    }


    // InOut and OutIn are symmetrical
    @Override
	public CCIntervalAction reverse()  {
		return new CCEaseInOut(other.reverse(), rate);
	}

}
