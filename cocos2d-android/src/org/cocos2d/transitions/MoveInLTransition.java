package org.cocos2d.transitions;

import org.cocos2d.actions.ease.EaseOut;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * MoveInL Transition.
 * Move in from to the left the incoming scene.
 */
public class MoveInLTransition extends TransitionScene {

    public static MoveInLTransition transition(float t, Scene s) {
        return new MoveInLTransition(t, s);
    }

    public MoveInLTransition(float t, Scene s) {
        super(t, s);
    }

    public void onEnter() {
        super.onEnter();

        initScenes();

        IntervalAction a = action();

        inScene.runAction(Sequence.actions(
                easeAction(a),
                CallFunc.action(this, "finish")));

    }

    /**
     * returns the action that will be performed
     */
    protected IntervalAction action() {
        return MoveTo.action(duration, 0, 0);
    }

    protected IntervalAction easeAction(IntervalAction action) {
        return EaseOut.action(action, 2.0f);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = Director.sharedDirector().winSize();
        inScene.setPosition(CGPoint.make(-s.width, 0));
    }
}
