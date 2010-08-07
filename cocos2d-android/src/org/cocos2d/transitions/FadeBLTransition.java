package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * FadeBLTransition.
 * Fade the tiles of the outgoing scene from the top-right corner to the bottom-left corner.
 */
public class FadeBLTransition extends FadeTRTransition {

    public FadeBLTransition(float t, CCScene s) {
        super(t, s);
    }


//	   	public IntervalAction action(CCGridSize v)
//		{
//			return FadeOutBLTiles.action(v, duration);
//		}
}
