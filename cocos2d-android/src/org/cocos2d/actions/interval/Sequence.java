package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

import java.util.ArrayList;

//
// Sequence
//

public class Sequence extends IntervalAction {
    private ArrayList<FiniteTimeAction> actions;
    private float split;
    private int last;


    public static IntervalAction actions(FiniteTimeAction action1, FiniteTimeAction... actions) {
        FiniteTimeAction prev = action1;
        for (FiniteTimeAction now : actions) {
            prev = new Sequence(prev, now);
        }
        return (IntervalAction) prev;
    }

    protected Sequence(FiniteTimeAction one, FiniteTimeAction two) {
        //assert one != null : "Sequence: argument one must be non-null";
        //assert two != null : "Sequence: argument two must be non-null";

        super(one.getDuration() + two.getDuration());

        actions = new ArrayList<FiniteTimeAction>(2);
        actions.add(one);
        actions.add(two);
    }

    @Override
    public IntervalAction copy() {
        return new Sequence(actions.get(0).copy(), actions.get(1).copy());
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        split = actions.get(0).getDuration() / duration;
        last = -1;
    }

    public void stop() {
        for (Action action : actions)
            action.stop();
        super.stop();
    }


    @Override
    public void update(float t) {
        int found;
        float new_t;

        if (t >= split) {
            found = 1;
            if (split == 1)
                new_t = 1;
            else
                new_t = (t - split) / (1 - split);
        } else {
            found = 0;
            if (split != 0)
                new_t = t / split;
            else
                new_t = 1;
        }

        if (last == -1 && found == 1) {
            actions.get(0).start(target);
            actions.get(0).update(1.0f);
            actions.get(0).stop();
        }

        if (last != found) {
            if (last != -1) {
                actions.get(last).update(1.0f);
                actions.get(last).stop();
            }
            actions.get(found).start(target);
        }
        actions.get(found).update(new_t);
        last = found;
    }

    @Override
    public IntervalAction reverse() {
        return new Sequence(actions.get(1).reverse(), actions.get(0).reverse());
    }
}
