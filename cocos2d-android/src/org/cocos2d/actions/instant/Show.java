package org.cocos2d.actions.instant;

import org.cocos2d.actions.base.FiniteTimeAction;
import org.cocos2d.nodes.CocosNode;

/**
 * Show the node
 */
public class Show extends InstantAction {

    public static Show action() {
        return new Show();
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        target.setVisible(true);
    }

    @Override
    public FiniteTimeAction reverse() {
        return new Hide();
    }
}
