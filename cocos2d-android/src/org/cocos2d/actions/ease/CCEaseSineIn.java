package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

public class CCEaseSineIn extends CCEaseAction {

    public static CCEaseSineIn action(CCIntervalAction action) {
        return new CCEaseSineIn(action);
    }

    protected CCEaseSineIn(CCIntervalAction action) {
        super(action);
    }

	@Override
	public CCEaseSineIn copy() {
		return new CCEaseSineIn(other.copy());
	}

    @Override
    public void update(float t) {
        other.update(-1 * (float)Math.cos(t * (float) Math.PI / 2) + 1);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseSineOut(other.reverse());
    }


}
