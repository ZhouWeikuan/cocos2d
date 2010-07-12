package org.cocos2d.transitions;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * SlideInB Transition.
 * Slide in the incoming scene from the bottom border.
 */
public class SlideInBTransition extends SlideInLTransition {

    public static SlideInBTransition transition(float t, Scene s) {
        return new SlideInBTransition(t, s);
    }

    public SlideInBTransition(float t, Scene s) {
        super(t, s);
    }

    public void sceneOrder() {
        inSceneOnTop = false;
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = Director.sharedDirector().winSize();
        inScene.setPosition(CGPoint.make(0,-(s.height-ADJUST_FACTOR)));
    }

    protected IntervalAction action() {
        CGSize s = Director.sharedDirector().winSize();
        return MoveBy.action(duration, 0,s.height-ADJUST_FACTOR);
    }

}
