package org.cocos2d.transitions;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;

/**
 * SlideInT Transition.
 * Slide in the incoming scene from the top border.
 */
public class SlideInTTransition extends SlideInLTransition {

    public static SlideInTTransition transition(float t, Scene s) {
        return new SlideInTTransition(t, s);
    }

    public SlideInTTransition(float t, Scene s) {
        super(t, s);
    }

    public void sceneOrder() {
        inSceneOnTop = false;
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = CCDirector.sharedDirector().winSize();
        inScene.setPosition(CGPoint.make(0,s.height-ADJUST_FACTOR));
    }

    protected IntervalAction action() {
        CGSize s = CCDirector.sharedDirector().winSize();
        return MoveBy.action(duration, 0,-(s.height-ADJUST_FACTOR));
    }

}
