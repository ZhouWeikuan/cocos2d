package org.cocos2d.transitions;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

/**
 * Fade Transition.
 * Fade out the outgoing scene and then fade in the incoming scene.
 */
public class CCFadeTransition extends CCTransitionScene {
    ccColor4B color;

    /**
     * creates the transition with a duration and with an RGB color
     */
    public static CCFadeTransition transition(float t, CCScene s, ccColor3B rgb) {
        return new CCFadeTransition(t, s, rgb);
    }
    
    /**
     * creates the transition with a duration
     */
    public static CCFadeTransition transition(float t, CCScene s) {
        return new CCFadeTransition(t, s);
    }

    /**
     * initializes the transition with a duration and with an RGB color
     */
    public CCFadeTransition(float d, CCScene s, ccColor3B rgb) {
        super(d, s);
        color = new ccColor4B(rgb.r, rgb.g, rgb.b, 0);
    }

    /**
     * initializes the transition with a duration
     */
    public CCFadeTransition(float d, CCScene s) {
        this(d, s, new ccColor3B(0, 0, 0));
    }

    @Override
    public void onEnter() {
        super.onEnter();

        CCColorLayer l = CCColorLayer.node(color);
        inScene.setVisible(false);

        addChild(l, 2, kSceneFade);


        CCNode f = getChildByTag(kSceneFade);

        CCIntervalAction a = CCSequence.actions(
                CCFadeIn.action(duration / 2),
                CCCallFunc.action(this, "hideOutShowIn"),
                CCFadeOut.action(duration / 2),
                CCCallFunc.action(this, "finish"));
        f.runAction(a);
    }

    @Override
    public void onExit() {
        super.onExit();
        removeChildByTag(kSceneFade, false);
    }

}
