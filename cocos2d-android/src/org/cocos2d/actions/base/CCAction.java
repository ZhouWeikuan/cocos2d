package org.cocos2d.actions.base;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.Copyable;


/** Base class for CCAction objects.
 */
public abstract class CCAction implements Copyable {
    public static final int kCCActionTagInvalid = -1;

    /** The "target". The action will modify the target properties.
     The target will be set with the 'startWithTarget' method.
     When the 'stop' method is called, target will be set to nil.
     The target is 'assigned', it is not 'retained'.
     */
    public CCNode target;

    /** The original target, since target can be nil.
     Is the target that were used to run the action. Unless you are doing something complex, like ActionManager, you should NOT call this method.
     @since v0.8.2
    */
    private CCNode originalTarget;

    /** The action tag. An identifier of the action */
    private int tag;

    public CCNode getOriginalTarget() {
        return originalTarget;
    }

    public void setOriginalTarget(CCNode value) {
        originalTarget = value;
    }

    public CCNode getTarget() {
        return target;
    }

    public void setTarget(CCNode value) {
        target = value;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int value) {
        tag = value;
    }

    /** Allocates and initializes the action */
    public static CCAction action() {
    	return null;
    }

    /** Initializes the action */
    public CCAction() {
        target = originalTarget = null;
        tag = kCCActionTagInvalid;
    }

    public abstract CCAction copy();

    //! called before the action start. It will also set the target.
    public void start(CCNode aTarget) {
        originalTarget = target = aTarget;
    }

    //! called after the action has finished. It will set the 'target' to nil.
    //! IMPORTANT: You should never call "[action stop]" manually. Instead, use: "[target stopAction:action];"
    public void stop() {
        // target = null;
    }

    //! return YES if the action has finished
    public boolean isDone() {
        return true;
    }

    //! called every frame with it's delta time. DON'T override unless you know what you are doing.
    public abstract void step(float dt);

    //! called once per frame. time a value between 0 and 1
    //! For example: 
    //! * 0 means that the action just started
    //! * 0.5 means that the action is in the middle
    //! * 1 means that the action is over
    public abstract void update(float time);

}

