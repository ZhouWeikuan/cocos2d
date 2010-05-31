package org.cocos2d.transitions;

import org.cocos2d.actions.ease.EaseOut;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCSize;

/**
 * SlideInL Transition.
 * Slide in the incoming scene from the left border.
 */
public class SlideInLTransition extends TransitionScene {

    protected static final float ADJUST_FACTOR = 0.5f;

    public static SlideInLTransition transition(float t, Scene s) {
        return new SlideInLTransition(t, s);
    }

    public SlideInLTransition(float t, Scene s) {
        super(t, s);
    }

    public void onEnter() {
        super.onEnter();

        initScenes();

        IntervalAction in = action();
        IntervalAction out = action();

        inScene.runAction(easeAction(in));
        outScene.runAction(Sequence.actions(
                easeAction(out),
                CallFunc.action(this, "finish")));
    }

    public void sceneOrder() {
        inSceneOnTop = false;
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CCSize s = Director.sharedDirector().winSize();
        inScene.setPosition(-s.width, 0);
    }

    /**
     * returns the action that will be performed
     */
    protected IntervalAction action() {
        CCSize s = Director.sharedDirector().winSize();
        return MoveBy.action(duration, s.width-ADJUST_FACTOR,0);
    }


    protected IntervalAction easeAction(IntervalAction action) {
        return EaseOut.action(action, 2.0f);
    }

}
