package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.types.CCColor3B;

//
// TintTo
//

public class TintTo extends IntervalAction {
    protected int toR, toG, toB;
    protected int fromR, fromG, fromB;

    public static TintTo action(float t, int r, int g, int b) {
        return new TintTo(t, r, g, b);
    }

    protected TintTo(float t, int r, int g, int b) {
        super(t);
        toR = r;
        toG = g;
        toB = b;
    }

    @Override
    public IntervalAction copy() {
        return new TintBy(duration, toR, toG, toB);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);

        CocosNode.CocosNodeRGBA tn = (CocosNode.CocosNodeRGBA) target;

        CCColor3B c = tn.getColor();
        fromR = c.r;
        fromG = c.g;
        fromB = c.b;
    }

    @Override
    public void update(float t) {
        ((CocosNode.CocosNodeRGBA) target).setColor(
                new CCColor3B((int) (fromR + (toR - fromR) * t),
                        (int) (fromG + (toG - fromG) * t),
                        (int) (fromB + (toB - fromB) * t)));
    }
}
