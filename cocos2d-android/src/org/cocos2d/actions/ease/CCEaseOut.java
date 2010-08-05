package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;


public class CCEaseOut extends CCEaseRateAction {

    public static CCEaseOut action(CCIntervalAction action, float rate) {
        return new CCEaseOut(action, rate);
    }

    protected CCEaseOut(CCIntervalAction action, float rate) {
        super(action, rate);
    }

    @Override
    public void update(float t) {
        other.update((float) Math.pow(t, 1 / rate));
    }

}
