package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.tile.CCFadeOutBLTiles;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.ccGridSize;

/**
 * FadeBLTransition.
 * Fade the tiles of the outgoing scene from the top-right corner to the bottom-left corner.
 */
public class CCFadeBLTransition extends CCFadeTRTransition {

	public CCFadeBLTransition(float t, CCScene s) {
		super(t, s);
	}

	public CCIntervalAction action(ccGridSize v) {
		return CCFadeOutBLTiles.action(v, duration);
	}
}
