package org.cocos2d.actions.base;

import android.util.Log;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.types.Copyable;

public class Action implements Copyable {
    private static final String LOG_TAG = Action.class.getSimpleName();

    public static final int INVALID_TAG = -1;


    private CocosNode originalTarget;
    public CocosNode target;
    private int tag;

    public CocosNode getOriginalTarget() {
        return originalTarget;
    }

    public void setOriginalTarget(CocosNode value) {
        originalTarget = value;

    }

    public CocosNode getTarget() {
        return target;
    }

    public void setTarget(CocosNode value) {
        target = value;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int value) {
        tag = value;

    }

    public static Action action() {
        return new Action();
    }

    protected Action() {
        target = originalTarget = null;
        tag = INVALID_TAG;
    }

    public Action copy() {
        Action copy = new Action();
        copy.tag = tag;
        return copy;
    }

    public void start(CocosNode aTarget) {
        originalTarget = target = aTarget;
    }

    public void stop() {
        target = null;
    }

    public boolean isDone() {
        return true;
    }


    public void step(float dt) {
        Log.w(LOG_TAG, "Override me");
    }

    public void update(float time) {
        Log.w(LOG_TAG, "Override me");
    }

    public interface CocosActionTag {
        public final int kActionTagInvalid = -1;
    }

}