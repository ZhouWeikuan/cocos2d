package org.cocos2d.actions.instant;

import java.lang.reflect.Method;

import org.cocos2d.nodes.CCNode;

//
// CallFunc
//

/**
 * Calls a 'callback'
 */
public class CCCallFunc extends CCInstantAction {
    protected Object targetCallback;
    protected String selector;
    protected Class<?> partypes[];

    protected Method invocation;

    /** creates the action with the callback */
    public static CCCallFunc action(Object target, String selector) {
        return new CCCallFunc(target, selector, null);
    }

    /**
     * creates an action with a callback
     */
    protected CCCallFunc(Object t, String s, Class<?>[] p) {
        targetCallback = t;
        selector = s;
        partypes = p;

        if (partypes == null)
        {
	        try {
	            Class<?> cls = targetCallback.getClass();
	            invocation = cls.getMethod(selector);
	    	} catch (NoSuchMethodException e) {
	    		e.printStackTrace();
	    	}
	    }
        else
        {
        	try {
                Class<?> cls = targetCallback.getClass();
                invocation = cls.getMethod(selector, partypes);
        	} catch (NoSuchMethodException e) {
        		e.printStackTrace();
        	}
        }
    }

    public CCCallFunc copy() {
        return new CCCallFunc(targetCallback, selector, partypes);
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
