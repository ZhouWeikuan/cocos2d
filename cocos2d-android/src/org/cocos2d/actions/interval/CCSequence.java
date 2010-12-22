package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.nodes.CCNode;

//
// Sequence
//

/** Runs actions sequentially, one after another
 */
public class CCSequence extends CCIntervalAction {
    private CCFiniteTimeAction[] actions;
    private float split;
    private int last;

    /** helper contructor to create an array of sequenceable actions */
    public static CCSequence actions(CCFiniteTimeAction action1, CCFiniteTimeAction... actions) {
        if(actions.length == 0) {
        	return new CCSequence(action1, CCFiniteTimeAction.action(0));
        } else {
	    	CCFiniteTimeAction prev = action1;
	        for (CCFiniteTimeAction now : actions) {
	            prev = new CCSequence(prev, now);
	        }
	        return (CCSequence) prev;
        }
    }
    
    /** initializes the action */
    protected CCSequence(CCFiniteTimeAction one, CCFiniteTimeAction two) {
        //assert one != null : "Sequence: argument one must be non-null";
        //assert two != null : "Sequence: argument two must be non-null";

        super(one.getDuration() + two.getDuration());

        actions = new CCFiniteTimeAction[2];
        actions[0] = one;
        actions[1] = two;
    }

    @Override
    public CCSequence copy() {
        return new CCSequence(actions[0].copy(), actions[1].copy());
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        split = actions[0].getDuration() / duration;
        last = -1;
    }

    public void stop() {
    	actions[0].stop();
        actions[1].stop();
        
        super.stop();
    }


    @Override
    public void update(float t) {
        int found = 0;
        float new_t = 0.f;

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
            actions[0].start(target);
            actions[0].update(1.0f);
            actions[0].stop();
        }

        if (last != found) {
            if (last != -1) {
                actions[last].update(1.0f);
                actions[last].stop();
            }
            actions[found].start(target);
        }
        actions[found].update(new_t);
        last = found;
    }

    @Override
    public CCSequence reverse() {
        return new CCSequence(actions[1].reverse(), actions[0].reverse());
    }
}
