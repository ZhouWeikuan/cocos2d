package org.cocos2d.transitions;

import org.cocos2d.nodes.Scene;

/**
 * SplitRows Transition.
 * The odd rows goes to the left while the even rows goes to the right.
 */
public class SplitRowsTransition extends SplitColsTransition {

    public SplitRowsTransition(float t, Scene s) {
        super(t, s);
    }

//	   	protected IntervalAction action(CCGridSize v)
//		{
//			return SplitRows.action(3, duration/2.0f);
//		}
}
