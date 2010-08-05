package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.nodes.CCNode;

////////////////////////////////////////////////////////////

/** CCDeccelAmplitude action */
public class CCDeccelAmplitude extends CCIntervalAction {
	/** amplitude rate */
	float			rate;
	CCIntervalAction other;
	
	public void setRate(float r){
		rate = r;
	}
	public float getRate() {
		return rate;
	}
	
	/** creates the action with an inner action that has the amplitude property, and a duration time */
	public static CCDeccelAmplitude action(CCIntervalAction action, float d) {
		return new CCDeccelAmplitude(action, d);
	}
	/** initializes the action with an inner action that has the amplitude property, and a duration time */
	public CCDeccelAmplitude(CCIntervalAction action, float d) {
		super(d);
		rate = 1.0f;
		other = action;
	}

	@Override
	public void start(CCNode aTarget) {
		super.start(aTarget);
		other.start(target);
	}

	@Override
	public void update(float time) {
		other.setAmplitudeRate((float)Math.pow((1-time), rate));
		other.update(time);
	}

	@Override
	public CCIntervalAction reverse() {
		return CCDeccelAmplitude.action(other.reverse(), duration);
	}

}

