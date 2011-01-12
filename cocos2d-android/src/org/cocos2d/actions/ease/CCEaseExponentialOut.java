package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseExponentialOut extends CCEaseAction {

    public static CCEaseExponentialOut action(CCIntervalAction action) {
        return new CCEaseExponentialOut(action);
    }

    protected CCEaseExponentialOut(CCIntervalAction action) {
        super(action);
    }

	@Override
	public CCEaseExponentialOut copy() {
		return new CCEaseExponentialOut(other.copy());
	}

    @Override
    public void update(float t) {
        other.update((t == 1) ? 1 : ((float) (-Math.pow(2, -10 * t / 1) + 1)));
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseExponentialIn(other.reverse());
    }

}
