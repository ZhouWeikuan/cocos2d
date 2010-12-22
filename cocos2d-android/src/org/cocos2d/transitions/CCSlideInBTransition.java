package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * SlideInB Transition.
 * Slide in the incoming scene from the bottom border.
 */
public class CCSlideInBTransition extends CCSlideInLTransition {

    public static CCSlideInBTransition transition(float t, CCScene s) {
        return new CCSlideInBTransition(t, s);
    }

    public CCSlideInBTransition(float t, CCScene s) {
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
        inScene.setPosition(0,-(s.height-ADJUST_FACTOR));
    }

    protected CCIntervalAction action() {
        CGSize s = CCDirector.sharedDirector().winSize();
        return CCMoveBy.action(duration, CGPoint.make(0,s.height-ADJUST_FACTOR));
    }

}
