package org.cocos2d.transitions;

import org.cocos2d.nodes.Scene;

/**
 * FadeBLTransition.
 * Fade the tiles of the outgoing scene from the top-right corner to the bottom-left corner.
 */
public class FadeBLTransition extends FadeTRTransition {

    public FadeBLTransition(float t, Scene s) {
        super(t, s);
    }


//	   	public IntervalAction action(CCGridSize v)
//		{
//			return FadeOutBLTiles.action(v, duration);
//		}
}
