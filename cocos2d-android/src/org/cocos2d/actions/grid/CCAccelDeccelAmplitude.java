package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.nodes.CCNode;

////////////////////////////////////////////////////////////

/** CCAccelDeccelAmplitude action */
public class CCAccelDeccelAmplitude extends CCIntervalAction {
	/** amplitude rate */	
	float			rate;
	
	CCIntervalAction other;
	

	/** creates the action with an inner action that has the amplitude property, and a duration time */
	public static CCAccelDeccelAmplitude action(CCIntervalAction action, float d) {
		return new CCAccelDeccelAmplitude(action, d);
	}
	
	/** initializes the action with an inner action that has the amplitude property, and a duration time */
	public CCAccelDeccelAmplitude(CCIntervalAction action, float d) {
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
		float f = time * 2;
		
		if (f > 1) {
			f -= 1;
			f = 1 - f;
		}
		
		other.setAmplitudeRate((float)Math.pow(f, rate));
		other.update(time);
	}	

	@Override
	public CCIntervalAction reverse() {
		return CCAccelDeccelAmplitude.action(other.reverse(), duration);
	}

}
