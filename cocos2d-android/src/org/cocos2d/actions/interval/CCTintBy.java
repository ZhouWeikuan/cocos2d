package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.ccColor3B;

/** Tints a CCNode that implements the CCNodeRGB protocol from current tint to a custom one.
 @since v0.7.2
 */
public class CCTintBy extends CCIntervalAction {
    protected ccColor3B delta;
    protected ccColor3B from;

    /** creates an action with duration and color */
    public static CCTintBy action(float t, ccColor3B c) {
        return new CCTintBy(t, c);
    }

    /** initializes the action with duration and color */
    protected CCTintBy(float t, ccColor3B c) {
        super(t);
        delta = new ccColor3B(c);
    }

    @Override
    public CCTintBy copy() {
        return new CCTintBy(duration, delta);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        ccColor3B c = ((CCRGBAProtocol) target).getColor();
        from = new ccColor3B(c);
    }

    @Override
    public void update(float t) {
        CCRGBAProtocol tn = (CCRGBAProtocol) target;
        tn.setColor(new ccColor3B((int) (from.r + delta.r * t), 
                    (int) (from.g + delta.g * t),
                    (int) (from.b + delta.b * t)));
    }

    @Override
    public CCTintBy reverse() {
        return new CCTintBy(duration, new ccColor3B(-delta.r, -delta.g, -delta.b));
    }
}

