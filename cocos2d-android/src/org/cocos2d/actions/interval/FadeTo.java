package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

//
// FadeTo
//

public class FadeTo extends IntervalAction {
    byte toOpacity, fromOpacity;

    public static FadeTo action(float t, byte a) {
        return new FadeTo(t, a);
    }

    protected FadeTo(float t, byte a) {
        super(t);
        toOpacity = a;
    }

    @Override
    public IntervalAction copy() {
        return new FadeTo(duration, toOpacity);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        fromOpacity = (byte) ((CCNode.CocosNodeRGBA) target).getOpacity();
    }

    @Override
    public void update(float t) {
        ((CCNode.CocosNodeRGBA) target).setOpacity((byte) (fromOpacity + (toOpacity - fromOpacity) * t));
    }
}
