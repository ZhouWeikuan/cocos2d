package org.cocos2d.transitions;

import org.cocos2d.actions.grid.CCStopGrid;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.tile.CCTurnOffTiles;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccGridSize;

/**
 * TurnOffTiles Transition.
 * Turn off the tiles of the outgoing scene in random order
 */
public class CCTurnOffTilesTransition extends CCTransitionScene implements CCTransitionEaseScene {
    
    public static CCTransitionScene transition(float t, CCScene s) {
        return new CCTurnOffTilesTransition(t, s);
    }

    public CCTurnOffTilesTransition(float t, CCScene s) {
        super(t, s);
    }

    // override addScenes, and change the order
    public void sceneOrder() {
        inSceneOnTop = false;
    }

    public void onEnter() {
        super.onEnter();

	CGSize s = CCDirector.sharedDirector().winSize();
	float aspect = s.width / s.height;
	int x = (int)(12 * aspect);
	int y = 12;

	CCTurnOffTiles toff = CCTurnOffTiles.action((int)System.currentTimeMillis(), ccGridSize.ccg(x,y), duration);
	CCIntervalAction action = easeAction(toff);
	outScene.runAction(CCSequence.actions(action,
							CCCallFunc.action(this, "finish"),
							CCStopGrid.action()
						)
			);
    }

    @Override
    public CCIntervalAction easeAction(CCIntervalAction action) {
        return action;
    }
}
