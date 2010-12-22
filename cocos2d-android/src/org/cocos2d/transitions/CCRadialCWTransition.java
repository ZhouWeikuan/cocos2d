package org.cocos2d.transitions;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.layers.CCScene;

///
//	A counter colock-wise radial transition to the next scene
///
public class CCRadialCWTransition extends CCRadialCCWTransition {

	public static CCRadialCWTransition transition(float t, CCScene s) {
		return new CCRadialCWTransition(t, s);
	}

	protected CCRadialCWTransition(float t, CCScene s) {
		super(t, s);
	}

	@Override
	public int radialType()	{
		return CCProgressTimer.kCCProgressTimerTypeRadialCW;
	}
}
