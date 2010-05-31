package org.cocos2d.actions.base;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.nodes.CocosNode;

public class Speed extends Action {
    protected Action other;
    protected float speed;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public static Speed action(Action action, float r) {
        return new Speed(action, r);
    }

    protected Speed(Action action, float r) {
        other = action;
        speed = r;
    }

    @Override
    public Speed copy() {
        return new Speed(other.copy(), speed);
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
    public void step(float dt) {
        other.step(dt * speed);
    }

    @Override
    public boolean isDone() {
        return other.isDone();
    }

    public Speed reverse() {
        return new Speed(((FiniteTimeAction)other).reverse(), speed);
    }

}
