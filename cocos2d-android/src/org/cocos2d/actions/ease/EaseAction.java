package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.nodes.CocosNode;


public class EaseAction extends IntervalAction {
    protected IntervalAction other;

    protected EaseAction(IntervalAction action) {
        super(action.getDuration());
        other = action;
    }

    @Override
    public EaseAction copy() {
        return new EaseAction(other.copy());
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
        other.update(t);
    }

    @Override
    public IntervalAction reverse() {
        return new EaseAction(other.reverse());
    }

}
