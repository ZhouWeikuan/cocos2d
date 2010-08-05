package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.nodes.CCNode;


/** Base class for Easing actions
 */
public class CCEaseAction extends CCIntervalAction {
	public static final float M_PI_X_2 = (float) (Math.PI * 2.0f);
	
    protected CCIntervalAction other;

    /** creates the action */
    public static CCEaseAction action(CCIntervalAction action) {
    	return new CCEaseAction(action);
    }

    /** initializes the action */
    protected CCEaseAction(CCIntervalAction action) {
    	super(action.getDuration());
        other = action;
    }

    @Override
    public CCEaseAction copy() {
        return new CCEaseAction(other.copy());
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
        other.update(t);
    }

    @Override
    public CCIntervalAction reverse() {
        return new CCEaseAction(other.reverse());
    }

}
