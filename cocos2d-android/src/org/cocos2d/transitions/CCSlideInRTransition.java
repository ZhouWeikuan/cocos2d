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
public class CCSlideInRTransition extends CCSlideInLTransition {

    public static CCSlideInRTransition transition(float t, CCScene s) {
        return new CCSlideInRTransition(t, s);
    }

    public CCSlideInRTransition(float t, CCScene s) {
        super(t, s);
    }

    @Override
    public void sceneOrder() {
        inSceneOnTop = true;
    }

    /**
     * initializes the scenes
     */
    @Override
    protected void initScenes() {
        CGSize s = CCDirector.sharedDirector().winSize();
        inScene.setPosition(s.width-ADJUST_FACTOR, 0);
    }

    @Override
    public CCIntervalAction action() {
        CGSize s = CCDirector.sharedDirector().winSize();
        return CCMoveBy.action(duration, CGPoint.make(-(s.width-ADJUST_FACTOR),0));
    }
}
