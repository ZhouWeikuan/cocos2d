package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseExponentialInOut extends CCEaseAction {

    public static CCEaseExponentialInOut action(CCIntervalAction action) {
        return new CCEaseExponentialInOut(action);
    }

    protected CCEaseExponentialInOut(CCIntervalAction action) {
        super(action);
    }

    @Override
    public void update(float t) {
    	t /= 0.5f;
        if (t < 1)
            t = 0.5f * (float) Math.pow(2, 10 * (t - 1));
        else
            t = 0.5f * (-(float) Math.pow(2, -10 * (t - 1) ) + 2);
        other.update(t);
    }

}
