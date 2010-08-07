package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * SlideInR Transition.
 * Slide in the incoming scene from the right border.
 */
public class SlideInRTransition extends SlideInLTransition {

    public static SlideInRTransition transition(float t, CCScene s) {
        return new SlideInRTransition(t, s);
    }

    public SlideInRTransition(float t, CCScene s) {
        super(t, s);
    }

    public void sceneOrder() {
        inSceneOnTop = true;
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = CCDirector.sharedDirector().winSize();
        inScene.setPosition(CGPoint.make(s.width-ADJUST_FACTOR, 0));
    }

    protected CCIntervalAction action() {
        CGSize s = CCDirector.sharedDirector().winSize();
        return CCMoveBy.action(duration, CGPoint.make(-(s.width-ADJUST_FACTOR),0));
    }
    
}
