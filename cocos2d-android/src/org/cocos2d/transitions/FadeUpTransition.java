package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * FadeUp Transition.
 * Fade the tiles of the outgoing scene from the bottom to the top.
 */
public class FadeUpTransition extends FadeTRTransition {

    public FadeUpTransition(float t, CCScene s) {
        super(t, s);
    }

//	   	protected IntervalAction action(CCGridSize v)
//		{
//			return FadeOutUspTiles.action(v, duration);
//		}
}
