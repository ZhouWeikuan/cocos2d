package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

/**
 * MoveInB Transition.
 * Move in from to the bottom the incoming scene.
 */
public class CCMoveInBTransition extends CCMoveInLTransition {

    public static CCMoveInBTransition transition(float t, CCScene s) {
        return new CCMoveInBTransition(t, s);
    }

    public CCMoveInBTransition(float t, CCScene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = CCDirector.sharedDirector().winSize();
        inScene.setPosition(0, -s.height);
    }
}
