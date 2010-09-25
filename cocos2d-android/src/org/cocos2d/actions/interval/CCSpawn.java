package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.nodes.CCNode;

/** Spawn a new action immediately
 */
//
// Spawn
//
public class CCSpawn extends CCIntervalAction {

    private CCFiniteTimeAction one;
    private CCFiniteTimeAction two;

    /** helper constructor to create an array of spawned actions */
    public static CCSpawn actions(CCFiniteTimeAction action1, CCFiniteTimeAction... params) {
        CCFiniteTimeAction prev = action1;

        if (action1 != null) {
            for (CCFiniteTimeAction now : params)
                prev = new CCSpawn(prev, now);
        }
        return (CCSpawn) prev;
    }
    
    /** initializes the Spawn action with the 2 actions to spawn */
    protected CCSpawn(CCFiniteTimeAction one_, CCFiniteTimeAction two_) {
        // assert one != null : "Spawn: argument one must be non-null";
        // assert two != null : "Spawn: argument two must be non-null";

        super(Math.max(one_.getDuration(), two_.getDuration()));

        float d1 = one_.getDuration();
        float d2 = two_.getDuration();

        one = one_;
        two = two_;

        if (d1 > d2)
            two = new CCSequence(two_, new CCDelayTime(d1 - d2));
        else if (d1 < d2)
            one = new CCSequence(one_, new CCDelayTime(d2 - d1));
    }

    @Override
    public CCIntervalAction copy() {
        return new CCSpawn(one.copy(), two.copy());
    }


    @Override
    public void start(CCNode aTarget) {
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
    public CCIntervalAction reverse() {
        return new CCSpawn(one.reverse(), two.reverse());
    }
}
