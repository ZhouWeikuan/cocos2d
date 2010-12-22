package org.cocos2d.transitions;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.layers.CCScene;

/**
 * RotoZoom Transition.
 * Rotate and zoom out the outgoing scene, and then rotate and zoom in the incoming
 */
public class CCRotoZoomTransition extends CCTransitionScene {

    public static CCRotoZoomTransition transition(float t, CCScene s) {
        return new CCRotoZoomTransition(t, s);
    }

    public CCRotoZoomTransition(float t, CCScene s) {
        super(t, s);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        inScene.setScale(0.001f);
        outScene.setScale(1.0f);

        inScene.setAnchorPoint(0.5f, 0.5f);
        outScene.setAnchorPoint(0.5f, 0.5f);

        CCIntervalAction rotozoom = CCSequence.actions(
				CCSpawn.actions(CCScaleBy.action(duration / 2, 0.001f),
					CCRotateBy.action(duration / 2, 360 * 2)
				),
			CCDelayTime.action(duration / 2));


        outScene.runAction(rotozoom);
        inScene.runAction(CCSequence.actions(rotozoom.reverse(),
                CCCallFunc.action(this, "finish")));
    }
}
