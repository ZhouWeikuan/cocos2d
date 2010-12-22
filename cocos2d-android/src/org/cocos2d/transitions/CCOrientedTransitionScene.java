package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * A Transition that supports orientation like.
 * Possible orientation: LeftOver, RightOver, UpOver, DownOver
 */
public class CCOrientedTransitionScene extends CCTransitionScene {
    int orientation;

    /** creates a base transition with duration and incoming scene */
    public static CCOrientedTransitionScene transition(float t, CCScene s, int o) {
	return new CCOrientedTransitionScene(t, s, o);
    }

    /**
     * initializes a transition with duration and incoming scene
     */
    protected CCOrientedTransitionScene(float t, CCScene s, int o) {
        super(t, s);
        orientation = o;
    }
}
