package org.cocos2d.actions.base;

import android.util.Log;

public class FiniteTimeAction extends Action {
    private static final String LOG_TAG = FiniteTimeAction.class.getSimpleName();

    protected float duration;

    public static FiniteTimeAction action(float d) {
        return new FiniteTimeAction(d);
    }

    protected FiniteTimeAction(float d) {
        duration = d;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public FiniteTimeAction copy() {
        return new FiniteTimeAction(duration);
    }

    public FiniteTimeAction reverse() {
        Log.w(LOG_TAG, "Override me");
        return null;
    }

}
