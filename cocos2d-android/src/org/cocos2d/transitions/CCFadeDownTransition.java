package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.tile.CCFadeOutDownTiles;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.ccGridSize;

/**
 * FadeDown Transition.
 * Fade the tiles of the outgoing scene from the top to the bottom.
 */
public class CCFadeDownTransition extends CCFadeTRTransition {

    public CCFadeDownTransition(float t, CCScene s) {
        super(t, s);
    }

    protected CCIntervalAction action(ccGridSize v) {
        return CCFadeOutDownTiles.action(v, duration);
    }
}
