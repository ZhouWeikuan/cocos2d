package org.cocos2d.actions.camera;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.ReverseTime;
import org.cocos2d.nodes.CocosNode;

public abstract class CameraAction extends IntervalAction {
    protected float centerXOrig;
    protected float centerYOrig;
    protected float centerZOrig;

    protected float eyeXOrig;
    protected float eyeYOrig;
    protected float eyeZOrig;

    protected float upXOrig;
    protected float upYOrig;
    protected float upZOrig;

    protected CameraAction(float t) {
        super(t);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);

        float x[] = new float[1];
        float y[] = new float[1];
        float z[] = new float[1];

        target.getCamera().getCenter(x, y, z);
        centerXOrig = x[0];
        centerYOrig = y[0];
        centerZOrig = z[0];

        target.getCamera().getEye(x, y, z);
        eyeXOrig = x[0];
        eyeYOrig = y[0];
        eyeZOrig = z[0];

        target.getCamera().getUp(x, y, z);
        upXOrig = x[0];
        upYOrig = y[0];
        upZOrig = z[0];
    }

    @Override
    public IntervalAction reverse() {
        return ReverseTime.action(this);
    }

}
