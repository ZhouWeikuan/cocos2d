package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

//
// Repeat
//

public class Repeat extends IntervalAction {
    private int times;
    private int total;
    private FiniteTimeAction other;

    public static Repeat action(FiniteTimeAction action, int t) {
        return new Repeat(action, t);
    }

    protected Repeat(FiniteTimeAction action, int t) {
        super(action.getDuration() * t);

        times = t;
        other = action;

        total = 0;
    }

    @Override
    public IntervalAction copy() {
        return new Repeat(other.copy(), times);
    }

    @Override
    public void start(CocosNode aTarget) {
        total = 0;
        super.start(aTarget);
        other.start(aTarget);
    }

    public void stop() {

        other.stop();
        super.stop();
    }

//    @Override
//    public void step(float dt) {
//        other.step(dt);
//        if (other.isDone()) {
//            total++;
//            other.start();
//        }
//    }

    // issue #80. Instead of hooking step:, hook update: since it can be called by any
    // container action like Repeat, Sequence, AccelDeccel, etc..

    @Override
    public void update(float dt) {
        float t = dt * times;
        float r = t % 1.0f;
        if (t > total + 1) {
            other.update(1.0f);
            total++;
            other.stop();
            other.start(target);
            other.update(0.0f);
        } else {
            // fix last repeat position
            // else it could be 0.
            if (dt == 1.0f)
                r = 1.0f;
            other.update(Math.min(r, 1));
        }
    }

    @Override
    public boolean isDone() {
        return (total == times);
    }

    @Override
    public IntervalAction reverse() {
        return new Repeat(other.reverse(), times);
    }
}
