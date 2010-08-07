package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * A Transition that supports orientation like.
 * Possible orientation: LeftOver, RightOver, UpOver, DownOver
 */
public abstract class OrientedTransitionScene extends TransitionScene {
    int orientation;

    /**
     * initializes a transition with duration and incoming scene
     */
    protected OrientedTransitionScene(float t, CCScene s, int o) {
        super(t, s);
        orientation = o;
    }
}
