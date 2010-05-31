package org.cocos2d.transitions;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCSize;

/**
 * MoveInB Transition.
 * Move in from to the bottom the incoming scene.
 */
public class MoveInBTransition extends MoveInLTransition {

    public static MoveInBTransition transition(float t, Scene s) {
        return new MoveInBTransition(t, s);
    }

    public MoveInBTransition(float t, Scene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CCSize s = Director.sharedDirector().winSize();
        inScene.setPosition(0, -s.height);
    }
}
