package org.cocos2d.actions.base;

import org.cocos2d.config.ccMacros;

/** Base class actions that do have a finite time duration.
 Possible actions:
   - An action with a duration of 0 seconds
   - An action with a duration of 35.5 seconds
 Infitite time actions are valid
 */
public class CCFiniteTimeAction extends CCAction {
    private static final String LOG_TAG = CCFiniteTimeAction.class.getSimpleName();

  	// ! duration in seconds of the action
    protected float duration;

    public static CCFiniteTimeAction action(float d) {
        return new CCFiniteTimeAction(d);
    }

    protected CCFiniteTimeAction(float d) {
        duration = d;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public CCFiniteTimeAction copy() {
        return new CCFiniteTimeAction(duration);
    }

    /** returns a reversed action */
    public CCFiniteTimeAction reverse() {
        ccMacros.CCLOG(LOG_TAG, "Override me");
        return null;
    }

	@Override
	public void step(float dt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
	}
}
