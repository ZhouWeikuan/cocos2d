package org.cocos2d.actions.interval;

//
// ReverseTime
//

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

public class ReverseTime extends IntervalAction {
    private FiniteTimeAction other;

    public static ReverseTime action(FiniteTimeAction action) {
        return new ReverseTime(action);
    }

    protected ReverseTime(FiniteTimeAction action) {
        super(action.getDuration());

        other = action;
    }

    @Override
    public IntervalAction copy() {
        return new ReverseTime(other.copy());
    }


    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        other.start(target);
    }

    @Override
    public void stop() {
        other.stop();
        super.stop();
    }

    @Override
    public void update(float t) {
        other.update(1 - t);
    }

    public IntervalAction reverse() {
        return new ReverseTime(other.copy());
    }
}
