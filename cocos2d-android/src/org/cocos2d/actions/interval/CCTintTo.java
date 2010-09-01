package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.ccColor3B;

/** Tints a CCNode that implements the CCNodeRGB protocol from current tint to a custom one.
 @warning This action doesn't support "reverse"
 @since v0.7.2
*/
public class CCTintTo extends CCIntervalAction {
	protected ccColor3B to;	
    protected ccColor3B from;

    /** creates an action with duration and color */
    public static CCTintTo action(float t, ccColor3B c) {
        return new CCTintTo(t, c);
    }

    /** initializes the action with duration and color */
    protected CCTintTo(float t, ccColor3B c) {
        super(t);
        to = new ccColor3B(c);
    }

    @Override
    public CCTintTo copy() {
        return new CCTintTo(duration, to);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        CCRGBAProtocol tn = (CCRGBAProtocol) target;

        from = tn.getColor();
    }

    @Override
    public void update(float t) {
        ((CCRGBAProtocol) target).setColor(
                new ccColor3B((int) (from.r + (to.r - from.r) * t),
                        (int) (from.g + (to.g - from.g) * t),
                        (int) (from.b + (to.b - from.b) * t)));
    }
}

