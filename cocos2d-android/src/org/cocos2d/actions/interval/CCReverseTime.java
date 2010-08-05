package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.nodes.CCNode;

/** Executes an action in reverse order, from time=duration to time=0
 
 @warning Use this action carefully. This action is not
 sequenceable. Use it as the default "reversed" method
 of your own actions, but using it outside the "reversed"
 scope is not recommended.
*/
public class CCReverseTime extends CCIntervalAction {
    private CCFiniteTimeAction other;

    /** creates the action */
    public static CCReverseTime action(CCFiniteTimeAction action) {
        return new CCReverseTime(action);
    }

    /** initializes the action */
    protected CCReverseTime(CCFiniteTimeAction action) {
        super(action.getDuration());

        other = action;
    }

    @Override
    public CCReverseTime copy() {
        return new CCReverseTime(other.copy());
    }

    @Override
    public void start(CCNode aTarget) {
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
        other.update(1 - t);
    }

    @Override
    public CCReverseTime reverse() {
        return new CCReverseTime(other.copy());
    }
}

