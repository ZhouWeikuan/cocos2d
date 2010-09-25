package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * A Transition that supports orientation like.
 * Possible orientation: LeftOver, RightOver, UpOver, DownOver
 */
public abstract class CCOrientedTransitionScene extends CCTransitionScene {
    int orientation;

    /**
     * initializes a transition with duration and incoming scene
     */
    protected CCOrientedTransitionScene(float t, CCScene s, int o) {
        super(t, s);
        orientation = o;
    }
}
