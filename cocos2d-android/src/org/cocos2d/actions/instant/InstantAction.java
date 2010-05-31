package org.cocos2d.actions.instant;

import org.cocos2d.actions.base.FiniteTimeAction;

public class InstantAction extends FiniteTimeAction {

    protected InstantAction() {
        super(0);
    }

    @Override
    public InstantAction copy() {
        return new InstantAction();
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void step(float dt) {
        update(1f);
    }

    public void update(float t) {
        // ignore
    }

    @Override
    public FiniteTimeAction reverse() {
        return copy();
    }

}

