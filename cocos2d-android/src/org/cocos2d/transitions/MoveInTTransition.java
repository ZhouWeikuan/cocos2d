package org.cocos2d.transitions;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;

/**
 * MoveInT Transition.
 * Move in from to the top the incoming scene.
 */
public class MoveInTTransition extends MoveInLTransition {

    public static MoveInTTransition transition(float t, Scene s) {
        return new MoveInTTransition(t, s);
    }

    public MoveInTTransition(float t, Scene s) {
        super(t, s);
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        inScene.setPosition(0, Director.sharedDirector().winSize().height);
    }
}
