package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;

public class EaseSineOut extends EaseAction {

    public static EaseSineOut action(IntervalAction action) {
        return new EaseSineOut(action);
    }

    protected EaseSineOut(IntervalAction action) {
        super(action);
    }

    @Override
    public void update(float t) {
        other.update((float)Math.sin(t * (float) Math.PI / 2));
    }

    @Override
    public IntervalAction reverse() {
        return new EaseExponentialOut(other.reverse());
    }

}

