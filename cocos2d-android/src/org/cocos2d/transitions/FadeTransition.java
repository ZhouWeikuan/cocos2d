package org.cocos2d.transitions;

import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.FadeIn;
import org.cocos2d.actions.interval.FadeOut;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.ColorLayer;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCColor4B;

/**
 * Fade Transition.
 * Fade out the outgoing scene and then fade in the incoming scene.
 */
public class FadeTransition extends TransitionScene {
    CCColor4B RGBA;

    /**
     * creates the transition with a duration and with an RGB color
     */
    public static FadeTransition transition(float t, Scene s, CCColor3B rgb) {
        return new FadeTransition(t, s, rgb);
    }

    /**
     * initializes the transition with a duration and with an RGB color
     */
    public FadeTransition(float d, Scene s, CCColor3B rgb) {
        super(d, s);
        RGBA = new CCColor4B(rgb.r, rgb.g, rgb.b, 0);
    }

    /**
     * initializes the transition with a duration
     */
    public FadeTransition(float d, Scene s) {
        this(d, s, new CCColor3B(0, 0, 0));
    }

    public void onEnter() {
        super.onEnter();

        ColorLayer l = ColorLayer.node(RGBA);
        inScene.setVisible(false);

        addChild(l, 2, kSceneFade);


        CocosNode f = getChild(kSceneFade);

        IntervalAction a = Sequence.actions(
                FadeIn.action(duration / 2),
                CallFunc.action(this, "hideOutShowIn"),
                FadeOut.action(duration / 2),
                CallFunc.action(this, "finish"));
        f.runAction(a);
    }

    public void onExit() {
        super.onExit();
        removeChild(kSceneFade, false);
    }

}
