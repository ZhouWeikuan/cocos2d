package org.cocos2d.actions.instant;

import org.cocos2d.actions.ActionCallback;
import org.cocos2d.nodes.CCNode;

//
// CallFunc
//

/**
 * Calls a 'callback'
 */
public class CCCallback extends CCInstantAction {
    protected ActionCallback callback;

    /** creates the action with the callback */
    public static CCCallback action(ActionCallback callback) {
        return new CCCallback(callback);
    }

    /**
     * creates an action with a callback
     */
    protected CCCallback(ActionCallback callback) {
    	this.callback = callback;
    }

    public CCCallback copy() {
        return new CCCallback(callback);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        execute();
    }

    /**
     * executes the callback
     */
    public void execute() {
    	callback.execute();
    }
}
