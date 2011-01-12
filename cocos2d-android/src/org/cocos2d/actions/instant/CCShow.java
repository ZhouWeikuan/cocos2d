package org.cocos2d.actions.instant;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.nodes.CCNode;

/**
 * Show the node
 */
public class CCShow extends CCInstantAction {

    public static CCShow action() {
        return new CCShow();
    }

	@Override
	public CCShow copy() {
		return new CCShow();
	}

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        target.setVisible(true);
    }

    @Override
    public CCFiniteTimeAction reverse() {
        return new CCHide();
    }
}
