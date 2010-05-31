package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// ScaleBy
//

public class ScaleBy extends ScaleTo {

    public static ScaleBy action(float t, float s) {
        return new ScaleBy(t, s, s);
    }

    public static ScaleBy action(float t, float sx, float sy) {
        return new ScaleBy(t, sx, sy);
    }

    protected ScaleBy(float t, float s) {
        super(t, s, s);
    }

    protected ScaleBy(float t, float sx, float sy) {
        super(t, sx, sy);
    }


    @Override
    public IntervalAction copy() {
        return new ScaleBy(duration, endScaleX, endScaleY);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        deltaX = startScaleX * endScaleX - startScaleX;
        deltaY = startScaleY * endScaleY - startScaleY;
    }

    @Override
    public IntervalAction reverse() {
        return new ScaleBy(duration, 1 / endScaleX, 1 / endScaleY);
    }
}
