package org.cocos2d.transitions;

import org.cocos2d.actions.grid.CCStopGrid;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.tile.CCFadeOutTRTiles;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccGridSize;

/**
 * FadeTRTransition.
 * Fade the tiles of the outgoing scene from the left-bottom corner to the top-right corner.
 */
public class CCFadeTRTransition extends CCTransitionScene implements CCTransitionEaseScene{

	public static CCFadeTRTransition transition(float t, CCScene s) {
		return new CCFadeTRTransition(t, s);
	}

	public CCFadeTRTransition(float t, CCScene s) {
		super(t, s);
	}

	// override addScenes, and change the order
	@Override
	public void sceneOrder() {
		inSceneOnTop = false;
	}

	@Override
	public void onEnter() {
		super.onEnter();

		CGSize s = CCDirector.sharedDirector().winSize();
		float aspect = s.width / s.height;
		int x = (int) (12 * aspect);
		int y = 12;

		CCIntervalAction action  = this.action(ccGridSize.ccg(x,y));

		outScene.runAction(
				CCSequence.actions(
						easeAction(action),
						CCCallFunc.action(this, "finish"),
						CCStopGrid.action()
				)
		);

	}

	protected CCIntervalAction action(ccGridSize v) {
		return CCFadeOutTRTiles.action(v, duration);
	}

	@Override
	public CCIntervalAction easeAction(CCIntervalAction action) {
		// TODO Auto-generated method stub
		return action;
	}

}
