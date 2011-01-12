package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseSineInOut extends CCEaseAction {

    public static CCEaseSineInOut action(CCIntervalAction action) {
        return new CCEaseSineInOut(action);
    }

    protected CCEaseSineInOut(CCIntervalAction action) {
        super(action);
    }

	@Override
	public CCEaseSineInOut copy() {
		return new CCEaseSineInOut(other.copy());
	}

    @Override
    public void update(float t) {
        other.update(-0.5f * ((float)Math.cos(Math.PI * t) - 1));
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseSineInOut(other.reverse());
    }

}
