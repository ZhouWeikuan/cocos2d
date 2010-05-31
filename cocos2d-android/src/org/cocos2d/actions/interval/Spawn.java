package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

//
// Spawn

//
public class Spawn extends IntervalAction {

    private FiniteTimeAction one;
    private FiniteTimeAction two;

    public static IntervalAction actions(FiniteTimeAction action1, FiniteTimeAction... params) {
        FiniteTimeAction prev = action1;

        if (action1 != null) {
            for (FiniteTimeAction now : params)
                prev = new Spawn(prev, now);
        }
        return (IntervalAction) prev;
    }

    protected Spawn(FiniteTimeAction one_, FiniteTimeAction two_) {
        //assert one != null : "Spawn: argument one must be non-null";
        //assert two != null : "Spawn: argument two must be non-null";

        super(Math.max(one_.getDuration(), two_.getDuration()));


        float d1 = one_.getDuration();
        float d2 = two_.getDuration();

        one = one_;
        two = two_;

        if (d1 > d2)
            two = new Sequence(two_, new DelayTime(d1 - d2));
        else if (d1 < d2)
            one = new Sequence(one_, new DelayTime(d2 - d1));
    }

    @Override
    public IntervalAction copy() {
        return new Spawn(one.copy(), two.copy());
    }


    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        one.start(target);
        two.start(target);
    }

    @Override
    public void stop() {
        one.stop();
        two.stop();
        super.stop();
    }

    @Override
    public void update(float t) {
        one.update(t);
        two.update(t);
    }

    @Override
    public IntervalAction reverse() {
        return new Spawn(one.reverse(), two.reverse());
    }
}
