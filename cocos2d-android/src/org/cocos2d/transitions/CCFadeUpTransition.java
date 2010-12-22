package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.tile.CCFadeOutUpTiles;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.ccGridSize;

/**
 * FadeUp Transition.
 * Fade the tiles of the outgoing scene from the bottom to the top.
 */
public class CCFadeUpTransition extends CCFadeTRTransition {

	public CCFadeUpTransition(float t, CCScene s) {
		super(t, s);
	}

	protected CCIntervalAction action(ccGridSize v) {
		return CCFadeOutUpTiles.action(v, duration);
	}
}
