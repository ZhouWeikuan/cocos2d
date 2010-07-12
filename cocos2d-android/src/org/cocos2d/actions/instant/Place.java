package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

/**
 * Places the node in a certain position
 */
public class Place extends InstantAction {
    private CGPoint position;

    public static Place action(float x, float y) {
        return new Place(x, y);
    }

    /**
     * creates a Place action with a position
     */
    protected Place(float x, float y) {
        position = CGPoint.make(x, y);
    }

    @Override
    public Place copy() {
        return new Place(position.x, position.y);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        target.setPosition(position);
    }
}
