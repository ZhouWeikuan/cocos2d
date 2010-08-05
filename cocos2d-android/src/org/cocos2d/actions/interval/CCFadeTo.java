package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

/** Fades an object that implements the CCRGBAProtocol protocol.
 * It modifies the opacity from the current value to a custom one.
 * @warning This action doesn't support "reverse"
 */
public class CCFadeTo extends CCIntervalAction {
    byte toOpacity;
    byte fromOpacity;

    /** creates an action with duration and opactiy */
    public static CCFadeTo action(float t, byte a) {
        return new CCFadeTo(t, a);
    }

    /** initializes the action with duration and opacity */
    protected CCFadeTo(float t, byte a) {
        super(t);
        toOpacity = a;
    }

    @Override
    public CCFadeTo copy() {
        return new CCFadeTo(duration, toOpacity);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        fromOpacity = (byte) ((CCNode.CCRGBAProtocol) target).getOpacity();
    }

    @Override
    public void update(float t) {
        ((CCNode.CCRGBAProtocol) target).setOpacity((byte) (fromOpacity + (toOpacity - fromOpacity) * t));
    }
}

