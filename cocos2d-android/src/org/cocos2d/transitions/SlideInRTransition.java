package org.cocos2d.transitions;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCSize;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;

/**
 * SlideInR Transition.
 * Slide in the incoming scene from the right border.
 */
public class SlideInRTransition extends SlideInLTransition {

    public static SlideInRTransition transition(float t, Scene s) {
        return new SlideInRTransition(t, s);
    }

    public SlideInRTransition(float t, Scene s) {
        super(t, s);
    }

    public void sceneOrder() {
        inSceneOnTop = true;
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CCSize s = Director.sharedDirector().winSize();
        inScene.setPosition(s.width-ADJUST_FACTOR, 0);
    }

    protected IntervalAction action() {
        CCSize s = Director.sharedDirector().winSize();
        return MoveBy.action(duration, -(s.width-ADJUST_FACTOR),0);
    }
    
}
