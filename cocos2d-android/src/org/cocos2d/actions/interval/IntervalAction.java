package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

public class IntervalAction extends FiniteTimeAction {
    protected float elapsed;
    private boolean firstTick;

    public float getElapsed() {
        return elapsed;
    }

    protected IntervalAction(float d) {
        super(d);
        elapsed = 0.0f;
        firstTick = true;
    }

    @Override
    public IntervalAction copy() {
        return new IntervalAction(duration);
    }

    @Override
    public boolean isDone() {
        return (elapsed >= duration);
    }

    @Override
    public void step(float dt) {
        if (firstTick) {
            firstTick = false;
            elapsed = 0;
        } else
            elapsed += dt;

        update(Math.min(1, elapsed / duration));
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        elapsed = 0.0f;
        firstTick = true;
    }

    @Override
    public IntervalAction reverse() {
        throw new ReverseActionNotImplementedException("Reverse action not implemented");
    }

    static class ReverseActionNotImplementedException extends RuntimeException {
        public ReverseActionNotImplementedException(String reason) {
            super(reason);
        }
    }

}













