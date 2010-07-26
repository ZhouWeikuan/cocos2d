package org.cocos2d.transitions;

import org.cocos2d.actions.ease.EaseOut;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.ScaleTo;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * Shrink the outgoing scene while grow the incoming scene
 */
public class ShrinkGrowTransition extends TransitionScene {

    public static ShrinkGrowTransition transition(float t, Scene s) {
        return new ShrinkGrowTransition(t, s);
    }

    public ShrinkGrowTransition(float t, Scene s) {
        super(t, s);
    }

    public void onEnter() {
        super.onEnter();

        // CCSize s = Director.sharedDirector().winSize();

        inScene.setScale(0.001f);
        outScene.setScale(1.0f);

        inScene.setAnchorPoint(CGPoint.make(2/3.0f,0.5f));
        outScene.setAnchorPoint(CGPoint.make(1/3.0f,0.5f));	


        IntervalAction scaleOut = ScaleTo.action(duration, 0.01f);
        IntervalAction scaleIn = ScaleTo.action(duration, 1.0f);

        inScene.runAction(easeAction(scaleIn));
        outScene.runAction(Sequence.actions(
                easeAction(scaleOut),
                CallFunc.action(this, "finish")));
    }

    protected IntervalAction easeAction(IntervalAction action) {
        return EaseOut.action(action, 2.0f);
    }

}
