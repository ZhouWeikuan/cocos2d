package org.cocos2d.transitions;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * JumpZoom Transition.
 * Zoom out and jump the outgoing scene, and then jump and zoom in the incoming
 */
public class CCJumpZoomTransition extends CCTransitionScene {

    public static CCJumpZoomTransition transition(float t, CCScene s) {
        return new CCJumpZoomTransition(t, s);
    }

    public CCJumpZoomTransition(float t, CCScene s) {
        super(t, s);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        CGSize size = CCDirector.sharedDirector().winSize();
        
        float width = size.width;
        // float height = size.getHeight();

        inScene.setScale(0.5f);
        inScene.setPosition(width, 0);

        inScene.setAnchorPoint(0.5f, 0.5f);
        outScene.setAnchorPoint(0.5f, 0.5f);

        CCIntervalAction jump = CCJumpBy.action(duration / 4, CGPoint.make(-width, 0), width / 4, 2);
        CCIntervalAction scaleIn = CCScaleTo.action(duration / 4, 1.0f);
        CCIntervalAction scaleOut = CCScaleTo.action(duration / 4, 0.5f);

        CCIntervalAction jumpZoomOut = CCSequence.actions(scaleOut, jump);
        CCIntervalAction jumpZoomIn = CCSequence.actions(jump.copy(), scaleIn);

        CCIntervalAction delay = CCDelayTime.action(duration / 2);

        outScene.runAction(jumpZoomOut);
        inScene.runAction(CCSequence.actions(delay, jumpZoomIn,
                CCCallFunc.action(this, "finish")));
    }
}
