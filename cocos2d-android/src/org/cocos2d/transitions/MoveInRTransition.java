package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;

/**
 * MoveInR Transition.
 * Move in from to the right the incoming scene.
 */
public class MoveInRTransition extends MoveInLTransition {

    public static MoveInRTransition transition(float t, CCScene s) {
        return new MoveInRTransition(t, s);
    }

    public MoveInRTransition(float t, CCScene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        inScene.setPosition(CGPoint.make(CCDirector.sharedDirector().winSize().width, 0));
    }
}

