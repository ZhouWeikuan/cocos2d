package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * SlideInT Transition.
 * Slide in the incoming scene from the top border.
 */
public class SlideInTTransition extends SlideInLTransition {

    public static SlideInTTransition transition(float t, CCScene s) {
        return new SlideInTTransition(t, s);
    }

    public SlideInTTransition(float t, CCScene s) {
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

    protected CCIntervalAction action() {
        CGSize s = CCDirector.sharedDirector().winSize();
        return CCMoveBy.action(duration, CGPoint.make(0,-(s.height-ADJUST_FACTOR)));
    }

}
