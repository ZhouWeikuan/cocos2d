package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.nodes.CCNode;

////////////////////////////////////////////////////////////

/** CCAccelAmplitude action */
public class CCAccelAmplitude extends CCIntervalAction {
	/** amplitude rate */
	float			rate;
	
	CCIntervalAction other;
	
	/** creates the action with an inner action that has the amplitude property,
	 * 	 and a duration time */
	public static CCAccelAmplitude action(CCIntervalAction action, float d) {
		return new CCAccelAmplitude(action, d);
	}

	/** initializes the action with an inner action that has the amplitude property,
	 * 		 and a duration time */
	public CCAccelAmplitude (CCIntervalAction action, float d) {
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
	public void update(float time)	{
		other.setAmplitudeRate((float)Math.pow(time, rate));
		other.update(time);
	}

	@Override
	public CCIntervalAction reverse() {
		return CCAccelAmplitude.action(other.reverse(), duration);
	}

}

