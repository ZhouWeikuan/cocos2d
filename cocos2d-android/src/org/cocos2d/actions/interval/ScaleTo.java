package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// ScaleTo
//

public class ScaleTo extends IntervalAction {
    protected float scaleX;
    protected float scaleY;
    protected float startScaleX;
    protected float startScaleY;
    protected float endScaleX;
    protected float endScaleY;
    protected float deltaX;
    protected float deltaY;

    public static ScaleTo action(float t, float s) {
        return new ScaleTo(t, s);
    }

    public static ScaleTo action(float t, float sx, float sy) {
        return new ScaleTo(t, sx, sy);
    }

    protected ScaleTo(float t, float s) {
        this(t, s, s);
    }

    protected ScaleTo(float t, float sx, float sy) {
        super(t);
        endScaleX = sx;
        endScaleY = sy;
    }

    @Override
    public IntervalAction copy() {
        return new ScaleTo(duration, endScaleX, endScaleY);
    }


    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        startScaleX = target.getScaleX();
        startScaleY = target.getScaleY();
        deltaX = endScaleX - startScaleX;
        deltaY = endScaleY - startScaleY;
    }

    @Override
    public void update(float t) {
        target.setScaleX(startScaleX + deltaX * t);
        target.setScaleY(startScaleY + deltaY * t);
    }
}
