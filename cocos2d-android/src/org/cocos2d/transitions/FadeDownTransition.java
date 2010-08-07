package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * FadeDown Transition.
 * Fade the tiles of the outgoing scene from the top to the bottom.
 */
public class FadeDownTransition extends FadeTRTransition {

    public FadeDownTransition(float t, CCScene s) {
        super(t, s);
    }

//    protected IntervalAction action(CCGridSize v)
//    {
//        return FadeOutDownTiles.action(v, duration);
//    }
}
