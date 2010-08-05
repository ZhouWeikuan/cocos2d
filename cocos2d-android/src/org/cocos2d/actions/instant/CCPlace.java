package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

/**
 * Places the node in a certain position
 */
public class CCPlace extends CCInstantAction {
    private CGPoint position;

    public static CCPlace action(CGPoint pnt) {
        return new CCPlace(pnt);
    }

    /**
     * creates a Place action with a position
     */
    protected CCPlace(CGPoint pnt) {
    	super();
        position = CGPoint.make(pnt.x, pnt.y);
    }

    @Override
    public CCPlace copy() {
        return new CCPlace(position);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        target.setPosition(position);
    }
}
