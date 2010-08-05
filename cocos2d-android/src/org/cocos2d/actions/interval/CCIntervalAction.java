package org.cocos2d.actions.interval;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCNode;


/** An interval action is an action that takes place within a certain period of time.
It has an start time, and a finish time. The finish time is the parameter
duration plus the start time.

These CCIntervalAction actions have some interesting properties, like:
 - They can run normally (default)
 - They can run reversed with the reverse method
 - They can run with the time altered with the Accelerate, AccelDeccel and Speed actions.

For example, you can simulate a Ping Pong effect running the action normally and
then running it again in Reverse mode.

Example:
 
	CCAction * pingPongAction = [CCSequence actions: action, [action reverse], nil];
*/
public class CCIntervalAction extends CCFiniteTimeAction {
	/** how many seconds had elapsed since the actions started to run. */
    protected float elapsed;
	private boolean firstTick;

    public float getElapsed() {
        return elapsed;
    }
    
    /** creates the action */
    public static CCIntervalAction action(float duration) {
    	return new CCIntervalAction(duration);
    }

    /** initializes the action */
    protected CCIntervalAction(float d) {    	
        super(d);
        if (duration == 0)
    		duration = ccMacros.FLT_EPSILON;
        elapsed = 0.0f;
        firstTick = true;
    }

    @Override
    public CCIntervalAction copy() {
        return new CCIntervalAction(duration);
    }

	/** returns YES if the action has finished */
    @Override
    public boolean isDone() {
        return (elapsed >= duration);
    }

    @Override
    public void step(float dt) {
        if (firstTick) {
            firstTick = false;
            elapsed = 0;
        } else
            elapsed += dt;

        update(Math.min(1, elapsed / duration));
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        elapsed = 0.0f;
        firstTick = true;
    }

	/** returns a reversed action */
    @Override
    public CCIntervalAction reverse() {
        assert false:("Reverse action not implemented");
    	return null;
    }
    
    public void setAmplitudeRate(float amp) {
    	assert false:"IntervalAction (Amplitude): Abstract class needs implementation";
    }

    public float getAmplitudeRate() {
    	assert (false) :"IntervalAction (Amplitude): Abstract class needs implementation";
    	return 0;
    }
}
