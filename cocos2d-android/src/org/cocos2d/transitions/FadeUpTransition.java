package org.cocos2d.transitions;

import org.cocos2d.nodes.Scene;

/**
 * FadeUp Transition.
 * Fade the tiles of the outgoing scene from the bottom to the top.
 */
public class FadeUpTransition extends FadeTRTransition {

    public FadeUpTransition(float t, Scene s) {
        super(t, s);
    }

//	   	protected IntervalAction action(CCGridSize v)
//		{
//			return FadeOutUspTiles.action(v, duration);
//		}
}
