package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;


public class CCEaseIn extends CCEaseRateAction {

    public static CCEaseIn action(CCIntervalAction action, float rate) {
        return new CCEaseIn(action, rate);
    }

    protected CCEaseIn(CCIntervalAction action, float rate) {
        super(action, rate);
    }

	@Override
	public CCEaseIn copy() {
		return new CCEaseIn(other.copy(), rate);
	}

    @Override
    public void update(float t) {
        other.update((float) Math.pow(t, rate));
    }

	@Override
	public CCIntervalAction reverse() {
		return new CCEaseOut(other.reverse(), rate);
	}

}
