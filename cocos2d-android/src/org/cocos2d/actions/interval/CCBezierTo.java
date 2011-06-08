package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CCBezierConfig;
import org.cocos2d.types.CGPoint;

/** An action that moves the target with a cubic Bezier curve to a destination point.
 @since v0.8.2
 */
public class CCBezierTo extends CCBezierBy {

	/** We need to record the original config.  Repeating a bezier movement is continually
	 *  more and more distorted if we continue to subtract the start position.
	 */
	final CCBezierConfig originalconfig;
	
    /** creates the action with a duration and a bezier configuration */
    public static CCBezierTo action(float t, CCBezierConfig c) {
        return new CCBezierTo(t, c);
    }

    /** initializes the action with a duration and a bezier configuration */
    protected CCBezierTo(float t, CCBezierConfig c) {
        super(t, c);
        originalconfig = new CCBezierConfig();
        originalconfig.controlPoint_1 = CGPoint.ccp(c.controlPoint_1.x, c.controlPoint_1.y);
        originalconfig.controlPoint_2 = CGPoint.ccp(c.controlPoint_2.x, c.controlPoint_2.y);
        originalconfig.endPosition = CGPoint.ccp(c.endPosition.x, c.endPosition.y);
    }

    @Override
    public CCBezierTo copy() {
        return new CCBezierTo(duration, config);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        config.controlPoint_1 = CGPoint.ccpSub(originalconfig.controlPoint_1, startPosition);
        config.controlPoint_2 = CGPoint.ccpSub(originalconfig.controlPoint_2, startPosition);
        config.endPosition = CGPoint.ccpSub(originalconfig.endPosition, startPosition);
    }

    @Override
    public CCBezierTo reverse() {
        // TODO: reverse it's not working as expected
        CCBezierConfig r = new CCBezierConfig();
        r.endPosition = CGPoint.ccpNeg(config.endPosition);
        r.controlPoint_1 = CGPoint.ccpAdd(config.controlPoint_2, CGPoint.ccpNeg(config.endPosition));
        r.controlPoint_2 = CGPoint.ccpAdd(config.controlPoint_1, CGPoint.ccpNeg(config.endPosition));

        return new CCBezierTo(duration, r);
    }
}

