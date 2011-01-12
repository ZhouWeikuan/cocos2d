package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseExponentialIn extends CCEaseAction {

    public static CCEaseExponentialIn action(CCIntervalAction action) {
        return new CCEaseExponentialIn(action);
    }

    protected CCEaseExponentialIn(CCIntervalAction action) {
        super(action);
    }

	@Override
	public CCEaseExponentialIn copy() {
		return new CCEaseExponentialIn(other.copy());
	}

    @Override
    public void update(float t) {
        other.update((t == 0) ? 0 : (float) Math.pow(2, 10 * (t / 1 - 1)) - 1 * 0.001f);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseExponentialOut(other.reverse());
    }

}
