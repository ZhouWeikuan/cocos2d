package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CCNode;


/**
 * Calls a 'callback' with the node as the first argument
 * N means Node
 */
public class CallFuncN extends CallFunc {

    public static CallFuncN action(Object t, String s) {
        return new CallFuncN(t, s);
    }

    /**
     * creates the action with the callback
     */
    protected CallFuncN(Object t, String s) {
        super(t, s);

        try {
            Class<?> cls = targetCallback.getClass();
            Class<?> partypes[] = new Class[1];
            partypes[0] = CCNode.class;
            invocation = cls.getMethod(selector, partypes);
        } catch (NoSuchMethodException e) {
        }
    }

    /**
     * executes the callback
     */
    public void execute() {
        try {
            invocation.invoke(targetCallback, new Object[]{target});
        } catch (Exception e) {
        }
    }
}
