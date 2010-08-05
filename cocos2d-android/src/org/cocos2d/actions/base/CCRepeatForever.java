package org.cocos2d.actions.base;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.nodes.CCNode;


/** Repeats an action for ever.
 To repeat the an action for a limited number of times use the Repeat action.
 @warning This action can't be Sequenceable because it is not an IntervalAction
 */
public class CCRepeatForever extends CCAction {
    protected CCIntervalAction other;

    /** creates the action */
    public static CCRepeatForever action(CCIntervalAction action) {
        return new CCRepeatForever(action);
    }

    /** initializes the action */
    protected CCRepeatForever(CCIntervalAction action) {
        other = action;
    }

    @Override
    public CCAction copy() {
        return new CCRepeatForever(other.copy());
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        other.start(target);
    }

    @Override
    public void step(float dt) {
        other.step(dt);
        if (other.isDone()) {
            float diff = dt + other.duration - other.getElapsed();
        	other.start(target);
        	other.step(diff);
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    public CCRepeatForever reverse() {
        return CCRepeatForever.action(other.reverse());
    }

	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
		
	}
}
