package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

//
// FadeIn
//

public class FadeIn extends IntervalAction {

    public static FadeIn action(float t) {
        return new FadeIn(t);
    }

    protected FadeIn(float t) {
        super(t);
    }

    @Override
    public void update(float t) {
        ((CCNode.CocosNodeRGBA) target).setOpacity((byte) (255.0f * t));
    }

    @Override
    public IntervalAction reverse() {
        return new FadeIn(duration);
    }
}
