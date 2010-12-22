package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.tile.CCSplitRows;
import org.cocos2d.layers.CCScene;

/**
 * SplitRows Transition.
 * The odd rows goes to the left while the even rows goes to the right.
 */
public class CCSplitRowsTransition extends CCSplitColsTransition {

	public CCSplitRowsTransition(float t, CCScene s) {
		super(t, s);
	}

	protected CCIntervalAction action() {
		return CCSplitRows.action(3, duration/2.0f);
	}
}
