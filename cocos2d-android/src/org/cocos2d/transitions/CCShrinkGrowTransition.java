package org.cocos2d.transitions;

import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;

/**
 * Shrink the outgoing scene while grow the incoming scene
 */
public class CCShrinkGrowTransition extends CCTransitionScene implements CCTransitionEaseScene {

    public static CCShrinkGrowTransition transition(float t, CCScene s) {
        return new CCShrinkGrowTransition(t, s);
    }

    public CCShrinkGrowTransition(float t, CCScene s) {
        super(t, s);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        inScene.setScale(0.001f);
        outScene.setScale(1.0f);

        inScene.setAnchorPoint(2/3.0f, 0.5f);
        outScene.setAnchorPoint(1/3.0f, 0.5f);

        CCIntervalAction scaleOut = CCScaleTo.action(duration, 0.01f);
        CCIntervalAction scaleIn = CCScaleTo.action(duration, 1.0f);

        inScene.runAction(easeAction(scaleIn));
        outScene.runAction(CCSequence.actions(
                easeAction(scaleOut),
                CCCallFunc.action(this, "finish")));
    }

    @Override
    public CCIntervalAction easeAction(CCIntervalAction action) {
        return CCEaseOut.action(action, 2.0f);
    }

}
