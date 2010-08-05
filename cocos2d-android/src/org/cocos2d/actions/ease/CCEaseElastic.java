package org.cocos2d.actions.ease;

import org.cocos2d.actions.interval.CCIntervalAction;

/** Ease Elastic abstract class
 @since v0.8.2
 */
public abstract class CCEaseElastic extends CCEaseAction {
	/** period of the wave in radians. default is 0.3 */
	protected float period_;

	/** Initializes the action with the inner action 
	 * 	and the period in radians (default is 0.3) */
    protected CCEaseElastic(CCIntervalAction action, float period) {
        super(action);
        period_ = period;
    }
    
    protected CCEaseElastic(CCIntervalAction action) {
    	this(action, 0.3f);
    }

    @Override
    public abstract CCEaseAction copy();

    @Override
    public abstract CCIntervalAction reverse();
}
