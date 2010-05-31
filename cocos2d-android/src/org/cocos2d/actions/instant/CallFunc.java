package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CocosNode;

import java.lang.reflect.Method;

//
// CallFunc
//

/**
 * Calls a 'callback'
 */
public class CallFunc extends InstantAction {
    protected Object targetCallback;
    protected String selector;

    protected Method invocation;


    public static CallFunc action(Object target, String selector) {
        return new CallFunc(target, selector);
    }

    /**
     * creates an action with a callback
     */
    protected CallFunc(Object t, String s) {
        targetCallback = t;
        selector = s;

        try {
            Class<?> cls = targetCallback.getClass();
            invocation = cls.getMethod(selector, new Class[]{});
        } catch (Exception e) {
        }
    }

    public CallFunc copy() {
        return new CallFunc(targetCallback, selector);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        execute();
    }

    /**
     * executes the callback
     */
    public void execute() {
        try {
            invocation.invoke(targetCallback, new Object[]{});
        } catch (Exception e) {
        }
    }
}
