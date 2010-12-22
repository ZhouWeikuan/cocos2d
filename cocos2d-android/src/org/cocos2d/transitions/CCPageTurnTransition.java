package org.cocos2d.transitions;

import org.cocos2d.actions.grid.CCPageTurn3D;
import org.cocos2d.actions.grid.CCStopGrid;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCShow;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCReverseTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccGridSize;


/**
 * A transition which peels back the bottom right hand corner of a scene
 * to transition to the scene beneath it simulating a page turn
 *
 * This uses a 3DAction so it's strongly recommended that depth buffering
 * is turned on in CCDirector using:
 *
 * 	[[CCDirector sharedDirector] setDepthBufferFormat:kDepthBuffer16];
 *
 * @since v0.8.2
 */
public class CCPageTurnTransition extends CCTransitionScene {
    protected boolean back_;

    /**
     * creates a base transition with duration and incoming scene
     * if back is TRUE then the effect is reversed to appear as if the incoming
     * scene is being turned from left over the outgoing scene
     */
    public static CCPageTurnTransition transition(float t, CCScene s, boolean back) {
        return new CCPageTurnTransition(t, s, back);
    }

    public static CCPageTurnTransition transition(float t, CCScene s) {
        return new CCPageTurnTransition(t, s, false);
    }

    /**
     * creates a base transition with duration and incoming scene
     * if back is TRUE then the effect is reversed to appear as if the incoming
     * scene is being turned from left over the outgoing scene
     */
    public CCPageTurnTransition(float t, CCScene s, boolean back) {
        super(t, s);
        back_ = back;
    }

    public void sceneOrder() {
        inSceneOnTop = back_;
    }

    public void onEnter() {
	super.onEnter();

	CGSize s = CCDirector.sharedDirector().winSize();
	int x,y;
	if( s.width > s.height) {
		x=16;
		y=12;
	} else  {
		x=12;
		y=16;
	}

	CCIntervalAction action  = action(ccGridSize.ccg(x,y));
	if (! back_ ) {
		outScene.runAction(CCSequence.actions(
							  action,
							  CCCallFunc.action(this, "finish"),
							  CCStopGrid.action()));
	} else {
		// to prevent initial flicker
		inScene.setVisible(false);
		inScene.runAction(CCSequence.actions(
							 CCShow.action(),
							 action,
							 CCCallFunc.action(this, "finish"),
							 CCStopGrid.action()));
	}
    }

    public CCIntervalAction action(ccGridSize v) {
	if( back_ ) {
		// Get hold of the PageTurn3DAction
		return CCReverseTime.action(CCPageTurn3D.action(v, duration));
	} else {
		// Get hold of the PageTurn3DAction
		return CCPageTurn3D.action(v, duration);
	}
    }
}
