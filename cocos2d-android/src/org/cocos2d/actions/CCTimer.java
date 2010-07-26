package org.cocos2d.actions;

import java.lang.reflect.Method;

public class CCTimer {
    //
    // CCTimer
    //
    /** Light weight timer */

    private Object target;
    private String selector;
    private Method invocation;

    /** interval in seconds */
    private float interval;
    private float elapsed;

    public String getSelector() {
    	return selector;
    }
    
    /** Initializes a timer with a target and a selector. */
    public CCTimer(Object targ, String s) {
        this(targ, s, 0);
    }

    /** Initializes a timer with a target, a selector and an interval in seconds.  */
    public CCTimer(Object t, String s, float seconds) {
        target = t;
        selector = s;

        interval = seconds;
        elapsed = -1;

        try {
            Class<?> cls = target.getClass();
            invocation = cls.getMethod(s, new Class[]{Float.TYPE});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInterval(float i) {
        interval = i;
    }

    public float getInterval() {
        return interval;
    }

    /** triggers the timer */
    public void update(float dt) {
        if (elapsed == -1) {
            elapsed = 0;
        } else {
            elapsed += dt;
        }
        if (elapsed >= interval) {
            try {
                invocation.invoke(target, new Object[]{elapsed});
            } catch (Exception e) {
                e.printStackTrace();
            }
            elapsed = 0;
        }
    }

}

