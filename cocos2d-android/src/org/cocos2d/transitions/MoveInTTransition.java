package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;

/**
 * MoveInT Transition.
 * Move in from to the top the incoming scene.
 */
public class MoveInTTransition extends MoveInLTransition {

    public static MoveInTTransition transition(float t, CCScene s) {
        return new MoveInTTransition(t, s);
    }

    public MoveInTTransition(float t, CCScene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        inScene.setPosition(CGPoint.make(0, CCDirector.sharedDirector().winSize().height));
    }
}
