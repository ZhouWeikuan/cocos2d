package org.cocos2d.transitions;

import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;

/**
 * Shrink the outgoing scene while grow the incoming scene
 */
public class ShrinkGrowTransition extends TransitionScene {

    public static ShrinkGrowTransition transition(float t, CCScene s) {
        return new ShrinkGrowTransition(t, s);
    }

    public ShrinkGrowTransition(float t, CCScene s) {
        super(t, s);
    }

    public void onEnter() {
        super.onEnter();

        // CCSize s = Director.sharedDirector().winSize();

        inScene.setScale(0.001f);
        outScene.setScale(1.0f);

        inScene.setAnchorPoint(CGPoint.make(2/3.0f,0.5f));
        outScene.setAnchorPoint(CGPoint.make(1/3.0f,0.5f));	


        CCIntervalAction scaleOut = CCScaleTo.action(duration, 0.01f);
        CCIntervalAction scaleIn = CCScaleTo.action(duration, 1.0f);

        inScene.runAction(easeAction(scaleIn));
        outScene.runAction(CCSequence.actions(
                easeAction(scaleOut),
                CCCallFunc.action(this, "finish")));
    }

    protected CCIntervalAction easeAction(CCIntervalAction action) {
        return CCEaseOut.action(action, 2.0f);
    }

}
