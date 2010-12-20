package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CCNode;

import java.lang.reflect.Method;

//
// CallFunc
//

/**
 * Calls a 'callback'
 */
public class CCCallFunc extends CCInstantAction {
    protected Object targetCallback;
    protected String selector;

    protected Method invocation;

    /** creates the action with the callback */
    public static CCCallFunc action(Object target, String selector) {
        return new CCCallFunc(target, selector);
    }

    /**
     * creates an action with a callback
     */
    protected CCCallFunc(Object t, String s) {
        targetCallback = t;
        selector = s;

        try {
            Class<?> cls = targetCallback.getClass();
            invocation = cls.getMethod(selector);
        } catch (Exception e) {
        }
    }

    public CCCallFunc copy() {
        return new CCCallFunc(targetCallback, selector);
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
        try {
            invocation.invoke(targetCallback);
        } catch (Exception e) {
        }
    }
}
