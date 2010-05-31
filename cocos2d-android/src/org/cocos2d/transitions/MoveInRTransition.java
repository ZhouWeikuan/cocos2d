package org.cocos2d.transitions;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;

/**
 * MoveInR Transition.
 * Move in from to the right the incoming scene.
 */
public class MoveInRTransition extends MoveInLTransition {

    public static MoveInRTransition transition(float t, Scene s) {
        return new MoveInRTransition(t, s);
    }

    public MoveInRTransition(float t, Scene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        inScene.setPosition(Director.sharedDirector().winSize().width, 0);
    }
}

