package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// FadeOut
//

public class FadeOut extends IntervalAction {

    public static FadeOut action(float t) {
        return new FadeOut(t);
    }

    protected FadeOut(float t) {
        super(t);
    }

    @Override
    public void update(float t) {
        ((CocosNode.CocosNodeRGBA) target).setOpacity((byte) (255.0f * (1 - t)));
    }

    @Override
    public IntervalAction reverse() {
        return new FadeOut(duration);
    }
}
