package org.cocos2d.transitions;

import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * MoveInL Transition.
 * Move in from to the left the incoming scene.
 */
public class MoveInLTransition extends TransitionScene {

    public static MoveInLTransition transition(float t, CCScene s) {
        return new MoveInLTransition(t, s);
    }

    public MoveInLTransition(float t, CCScene s) {
        super(t, s);
    }

    public void onEnter() {
        super.onEnter();

        initScenes();

        CCIntervalAction a = action();

        inScene.runAction(CCSequence.actions(
                easeAction(a),
                CCCallFunc.action(this, "finish")));

    }

    /**
     * returns the action that will be performed
     */
    protected CCIntervalAction action() {
        return CCMoveTo.action(duration, new CGPoint());
    }

    protected CCIntervalAction easeAction(CCIntervalAction action) {
        return CCEaseOut.action(action, 2.0f);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = CCDirector.sharedDirector().winSize();
        inScene.setPosition(CGPoint.make(-s.width, 0));
    }
}
