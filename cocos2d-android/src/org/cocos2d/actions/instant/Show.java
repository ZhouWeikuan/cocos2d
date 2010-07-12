package org.cocos2d.actions.instant;

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CCNode;

/**
 * Show the node
 */
public class Show extends InstantAction {

    public static Show action() {
        return new Show();
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        target.setVisible(true);
    }

    @Override
    public FiniteTimeAction reverse() {
        return new Hide();
    }
}
