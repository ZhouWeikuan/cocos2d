package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;

/**
 * MoveInR Transition.
 * Move in from to the right the incoming scene.
 */
public class CCMoveInRTransition extends CCMoveInLTransition {

    public static CCMoveInRTransition transition(float t, CCScene s) {
        return new CCMoveInRTransition(t, s);
    }

    public CCMoveInRTransition(float t, CCScene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    @Override
    protected void initScenes() {
        inScene.setPosition(CCDirector.sharedDirector().winSize().width, 0);
    }
}
