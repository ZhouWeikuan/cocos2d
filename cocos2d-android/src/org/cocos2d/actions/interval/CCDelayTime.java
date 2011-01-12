package org.cocos2d.actions.interval;

/** 
 * Delays the action a certain amount of seconds
*/
public class CCDelayTime extends CCIntervalAction {

    public static CCDelayTime action(float t) {
        return new CCDelayTime(t);
    }

    protected CCDelayTime(float t) {
        super(t);
    }

	@Override
	public CCDelayTime copy() {
		return new CCDelayTime(duration);
	}

    @Override
    public void update(float t) {
    }

    @Override
    public CCDelayTime reverse() {
        return new CCDelayTime(duration);
    }
}

