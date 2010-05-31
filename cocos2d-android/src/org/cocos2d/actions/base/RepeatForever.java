package org.cocos2d.actions.base;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.nodes.CocosNode;

public class RepeatForever extends Action {
    protected IntervalAction other;

    public static RepeatForever action(IntervalAction action) {
        return new RepeatForever(action);
    }


    protected RepeatForever(IntervalAction action) {
        other = action;
    }

    @Override
    public Action copy() {
        return new RepeatForever(other.copy());
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        other.start(target);
    }

    @Override
    public void step(float dt) {
        other.step(dt);
        if (other.isDone())
            other.start(target);
    }


    @Override
    public boolean isDone() {
        return false;
    }

    public RepeatForever reverse() {
        return new RepeatForever(other.reverse());
    }
}
