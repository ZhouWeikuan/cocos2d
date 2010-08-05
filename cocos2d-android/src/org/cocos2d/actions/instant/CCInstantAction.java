package org.cocos2d.actions.instant;

import org.cocos2d.actions.base.CCFiniteTimeAction;

/** Instant actions are immediate actions. They don't have a duration like
	the CCIntervalAction actions.
*/
public class CCInstantAction extends CCFiniteTimeAction {

    protected CCInstantAction() {
        super(0);
    }

    @Override
    public CCInstantAction copy() {
        return new CCInstantAction();
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void step(float dt) {
        update(1.0f);
    }

    @Override
    public void update(float t) {
        // ignore
    }

    @Override
    public CCFiniteTimeAction reverse() {
        return copy();
    }
}
