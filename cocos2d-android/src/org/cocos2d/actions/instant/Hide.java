package org.cocos2d.actions.instant;

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

/**
 * Hide the node
 */
public class Hide extends InstantAction {

    public static Hide action() {
        return new Hide();
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        target.setVisible(false);
    }

    @Override
    public FiniteTimeAction reverse() {
        return new Show();
    }
}
