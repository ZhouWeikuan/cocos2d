package org.cocos2d.actions.interval;

//
// DelayTime
//

public class DelayTime extends IntervalAction {

    public static DelayTime action(float t) {
        return new DelayTime(t);
    }

    protected DelayTime(float t) {
        super(t);
    }

    @Override
    public void update(float t) {
    }

    @Override
    public IntervalAction reverse() {
        return new DelayTime(duration);
    }
}
