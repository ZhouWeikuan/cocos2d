package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CCNode;

/**
 * Toggles the visibility of a node
 */
public class ToggleVisibility extends InstantAction {

    public static ToggleVisibility action() {
        return new ToggleVisibility();
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        target.setVisible(!target.getVisible());
    }


}
